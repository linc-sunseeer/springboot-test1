package com.example.demo;

import com.example.demo.dto.ReservationRequest;
import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.mapper.ReservationMapper;
import com.example.demo.service.MailService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.MenuService;
import com.example.demo.service.SystemSettingService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private SystemSettingService systemSettingService;

    @Mock
    private MenuService menuService;

    @Mock
    private UserService userService;

    @Mock
    private MailService mailService;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-04-15T00:30:00Z"), ZoneId.of("Asia/Tokyo")); // 09:30 JST
        reservationService = new ReservationService(reservationMapper, menuService, systemSettingService, userService, mailService, fixedClock);
        lenient().when(systemSettingService.getReservationCutoffTime()).thenReturn(LocalTime.of(9, 45));
        lenient().when(systemSettingService.getMinReservationThreshold()).thenReturn(6);
        Menu todayMenu = Menu.builder().id(1L).availableDate(LocalDate.of(2026, 4, 15)).deleted(false).build();
        Menu secondMenu = Menu.builder().id(2L).availableDate(LocalDate.of(2026, 4, 15)).deleted(false).build();
        lenient().when(menuService.getMenuById(1L)).thenReturn(todayMenu);
        lenient().when(menuService.getMenuById(2L)).thenReturn(secondMenu);
        lenient().when(userService.getUserById(1L)).thenReturn(User.builder().id(1L).email("user@example.com").name("User").build());
    }

    @Test
    void rejectsDuplicateReservationForSameUserAndMenuAndDate() {
        ReservationRequest request = new ReservationRequest();
        request.setMenuId(1L);
        request.setReservationDate(LocalDate.of(2026, 4, 15));
        request.setQuantity(1);

        when(reservationMapper.selectCount(any())).thenReturn(1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(1L, request));

        assertEquals("1日1件まで予約できます。", exception.getMessage());
        verify(reservationMapper, never()).insert(isA(Reservation.class));
    }

    @Test
    void rejectsReservationAfterCutoffTime() {
        Clock lateClock = Clock.fixed(Instant.parse("2026-04-15T00:46:00Z"), ZoneId.of("Asia/Tokyo")); // 09:46 JST
        reservationService = new ReservationService(reservationMapper, menuService, systemSettingService, userService, mailService, lateClock);

        ReservationRequest request = new ReservationRequest();
        request.setMenuId(1L);
        request.setReservationDate(LocalDate.of(2026, 4, 15));
        request.setQuantity(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(1L, request));

        assertEquals("予約可能時間を過ぎています。", exception.getMessage());
        verify(reservationMapper, never()).insert(isA(Reservation.class));
    }

    @Test
    void marksReservationAsPaid() {
        Reservation reservation = new Reservation();
        reservation.setId(10L);
        reservation.setUserId(1L);
        reservation.setPaymentStatus("UNPAID");
        reservation.setPaymentMethod("CARD");

        when(reservationMapper.selectById(10L)).thenReturn(reservation);

        reservationService.markAsPaid(1L, 10L);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationMapper).updateById(captor.capture());
        assertEquals("PAID", captor.getValue().getPaymentStatus());
    }

    @Test
    void requestsCashPaymentInsteadOfDirectPaid() {
        Reservation reservation = new Reservation();
        reservation.setId(11L);
        reservation.setUserId(1L);
        reservation.setPaymentStatus("UNPAID");
        reservation.setPaymentMethod("CASH");

        when(reservationMapper.selectById(11L)).thenReturn(reservation);

        Reservation updated = reservationService.markAsPaid(1L, 11L);

        assertEquals("PAYMENT_REQUESTED", updated.getPaymentStatus());
    }

    @Test
    void approvesRequestedCashPaymentAsAdminAndSendsMail() {
        Reservation reservation = new Reservation();
        reservation.setId(12L);
        reservation.setUserId(1L);
        reservation.setMenuId(1L);
        reservation.setReservationDate(LocalDate.of(2026, 4, 15));
        reservation.setQuantity(1);
        reservation.setPaymentMethod("CASH");
        reservation.setPaymentStatus("PAYMENT_REQUESTED");

        when(reservationMapper.selectById(12L)).thenReturn(reservation);

        Reservation approved = reservationService.approvePaymentAsAdmin(12L);

        assertEquals("PAID", approved.getPaymentStatus());
        verify(mailService).sendMail(org.mockito.ArgumentMatchers.eq("user@example.com"),
                org.mockito.ArgumentMatchers.contains("支払い確認"),
                org.mockito.ArgumentMatchers.contains("2026-04-15"));
    }

    @Test
    void rejectsAdminApprovalWhenCashPaymentNotRequested() {
        Reservation reservation = new Reservation();
        reservation.setId(13L);
        reservation.setUserId(1L);
        reservation.setPaymentMethod("CASH");
        reservation.setPaymentStatus("UNPAID");

        when(reservationMapper.selectById(13L)).thenReturn(reservation);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.approvePaymentAsAdmin(13L));

        assertTrue(exception.getMessage().contains("支払申請"));
    }

    @Test
    void storesQuantityWhenCreatingReservation() {
        ReservationRequest request = new ReservationRequest();
        request.setMenuId(1L);
        request.setReservationDate(LocalDate.of(2026, 4, 15));
        request.setQuantity(1);

        when(reservationMapper.selectCount(any())).thenReturn(0L);

        reservationService.createReservation(1L, request);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationMapper).insert(captor.capture());
        assertEquals(1, captor.getValue().getQuantity());
        assertEquals("CASH", captor.getValue().getPaymentMethod());
    }

    @Test
    void acceptsQuantityAtConfiguredCap() {
        ReservationRequest request = new ReservationRequest();
        request.setMenuId(1L);
        request.setReservationDate(LocalDate.of(2026, 4, 15));
        request.setQuantity(6);

        when(reservationMapper.selectCount(any())).thenReturn(0L);

        reservationService.createReservation(1L, request);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationMapper).insert(captor.capture());
        assertEquals(6, captor.getValue().getQuantity());
    }

    @Test
    void createsMultipleReservationsFromCartCheckout() {
        ReservationRequest first = new ReservationRequest();
        first.setMenuId(1L);
        first.setReservationDate(LocalDate.of(2026, 4, 15));
        first.setQuantity(1);

        ReservationRequest second = new ReservationRequest();
        second.setMenuId(2L);
        second.setReservationDate(LocalDate.of(2026, 4, 15));
        second.setQuantity(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservationsFromCart(1L, List.of(first, second)));

        assertEquals("1日1件まで予約できます。", exception.getMessage());
        verify(reservationMapper, never()).insert(any(Reservation.class));
    }

    @Test
    void rejectsWhenReservationDateDiffersFromMenuDate() {
        ReservationRequest request = new ReservationRequest();
        request.setMenuId(1L);
        request.setReservationDate(LocalDate.of(2026, 4, 14));
        request.setQuantity(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(1L, request));

        assertEquals("注文日がメニュー提供日と一致しません。", exception.getMessage());
    }

    @Test
    void storesSelectedPaymentMethod() {
        ReservationRequest request = new ReservationRequest();
        request.setMenuId(1L);
        request.setReservationDate(LocalDate.of(2026, 4, 15));
        request.setQuantity(1);
        request.setPaymentMethod("PAYPAY");

        when(reservationMapper.selectCount(any())).thenReturn(0L);

        reservationService.createReservation(1L, request);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationMapper).insert(captor.capture());
        assertEquals("PAYPAY", captor.getValue().getPaymentMethod());
    }

    @Test
    void rejectsInvalidPaymentMethod() {
        ReservationRequest request = new ReservationRequest();
        request.setMenuId(1L);
        request.setReservationDate(LocalDate.of(2026, 4, 15));
        request.setQuantity(1);
        request.setPaymentMethod("CRYPTO");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(1L, request));

        assertEquals("支払方法は CARD / PAYPAY / CASH から選択してください。", exception.getMessage());
    }

    @Test
    void countActiveReservationsForMenuIgnoresCancelledOrders() {
        when(reservationMapper.selectCount(any())).thenReturn(3L);

        Long count = reservationService.countActiveReservationsForMenu(10L);

        assertEquals(3L, count);
    }
}
