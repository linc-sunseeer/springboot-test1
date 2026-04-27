package com.example.demo;

import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.mapper.MenuMapper;
import com.example.demo.mapper.ReservationMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.AdminAggregateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAggregateServiceTest {

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private UserMapper userMapper;

    private AdminAggregateService adminAggregateService;

    @BeforeEach
    void setUp() {
        adminAggregateService = new AdminAggregateService(reservationMapper, menuMapper, userMapper);
    }

    @Test
    void dailyReservationUsersUseRealUserNames() {
        Reservation reservation = Reservation.builder()
                .id(10L)
                .userId(2L)
                .menuId(3L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .build();
        Menu menu = Menu.builder().id(3L).name("唐揚げ弁当").price(new BigDecimal("550")).build();
        User user = User.builder().id(2L).name("Hanako").email("hanako@example.com").build();

        when(reservationMapper.selectList(any())).thenReturn(List.of(reservation));
        when(menuMapper.selectList(any())).thenReturn(List.of(menu));
        when(userMapper.selectList(any())).thenReturn(List.of());
        when(userMapper.selectList(any())).thenReturn(List.of(user));

        Map<String, Object> result = adminAggregateService.getDailyReservationUsers(LocalDate.of(2026, 4, 16));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> users = (List<Map<String, Object>>) result.get("users");
        assertEquals("Hanako", users.get(0).get("userName"));
        assertEquals("唐揚げ弁当", users.get(0).get("menuName"));
        assertEquals("RESERVED", users.get(0).get("orderStatus"));
    }

    @Test
    void adminUserReservationsContainPaymentAndStatusHistory() {
        Reservation latest = Reservation.builder()
                .id(20L)
                .userId(1L)
                .menuId(3L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("PAID")
                .orderStatus("CONFIRMED")
                .reservedAt(LocalDateTime.of(2026, 4, 16, 8, 0))
                .build();
        Menu menu = Menu.builder().id(3L).name("生姜焼き弁当").price(new BigDecimal("620")).build();

        when(reservationMapper.selectList(any())).thenReturn(List.of(latest));
        when(menuMapper.selectList(any())).thenReturn(List.of(menu));

        List<Map<String, Object>> history = adminAggregateService.getUserReservationHistory(1L);

        assertFalse(history.isEmpty());
        assertEquals("生姜焼き弁当", history.get(0).get("menuName"));
        assertEquals("PAID", history.get(0).get("paymentStatus"));
        assertEquals("CONFIRMED", history.get(0).get("orderStatus"));
    }

    @Test
    void monthlyAggregateIncludesPaidUnpaidAndMenuBreakdown() {
        Reservation first = Reservation.builder()
                .id(1L)
                .userId(1L)
                .menuId(3L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("PAID")
                .orderStatus("CONFIRMED")
                .build();
        Reservation second = Reservation.builder()
                .id(2L)
                .userId(2L)
                .menuId(4L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .build();
        Reservation third = Reservation.builder()
                .id(3L)
                .userId(3L)
                .menuId(3L)
                .reservationDate(LocalDate.of(2026, 4, 17))
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .build();

        Menu karaage = Menu.builder().id(3L).name("唐揚げ弁当").price(new BigDecimal("550")).build();
        Menu ginger = Menu.builder().id(4L).name("生姜焼き弁当").price(new BigDecimal("620")).build();

        when(reservationMapper.selectList(any())).thenReturn(List.of(first, second, third));
        when(menuMapper.selectList(any())).thenReturn(List.of(karaage, ginger));

        Map<String, Object> result = adminAggregateService.getMonthlyAggregate("2026-04");

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> days = (List<Map<String, Object>>) result.get("days");
        assertEquals(2, days.size());
        assertEquals(2, days.get(0).get("reservationCount"));
        assertEquals(1L, days.get(0).get("paidCount"));
        assertEquals(1L, days.get(0).get("unpaidCount"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> menuBreakdown = (List<Map<String, Object>>) days.get(0).get("menus");
        assertEquals("唐揚げ弁当", menuBreakdown.get(0).get("menuName"));
    }

    @Test
    void dailyAggregateCsvUsesBomJapaneseHeadersAndEscapedValues() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .userId(1L)
                .menuId(10L)
                .reservationDate(LocalDate.of(2026, 4, 20))
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .quantity(2)
                .build();
        Menu menu = Menu.builder()
                .id(10L)
                .name("鮭\"特製\",弁当")
                .price(new BigDecimal("680"))
                .build();

        when(reservationMapper.selectList(any())).thenReturn(List.of(reservation));
        when(menuMapper.selectList(any())).thenReturn(List.of(menu));

        String csv = adminAggregateService.exportDailyAggregateCsv(LocalDate.of(2026, 4, 20));

        assertTrue(csv.startsWith("\uFEFFsep=,\r\n日付,メニュー名,予約数,支払済み数,未支払数,支払済みユーザー,未支払ユーザー\r\n"));
        assertTrue(csv.contains("2026-04-20,\"鮭\"\"特製\"\",弁当\",2,0,2,,User-1\r\n"));
    }

    @Test
    void dailyAggregateIncludesPaidAndUnpaidUserListsPerMenu() {
        Reservation paid = Reservation.builder()
                .id(1L)
                .userId(10L)
                .menuId(7L)
                .reservationDate(LocalDate.of(2026, 4, 21))
                .paymentStatus("PAID")
                .orderStatus("CONFIRMED")
                .quantity(1)
                .build();
        Reservation unpaid = Reservation.builder()
                .id(2L)
                .userId(11L)
                .menuId(7L)
                .reservationDate(LocalDate.of(2026, 4, 21))
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .quantity(2)
                .build();
        Menu menu = Menu.builder().id(7L).name("のり弁当").price(new BigDecimal("500")).build();
        User paidUser = User.builder().id(10L).name("Sato").email("sato@example.com").build();
        User unpaidUser = User.builder().id(11L).name("Suzuki").email("suzuki@example.com").build();

        when(reservationMapper.selectList(any())).thenReturn(List.of(paid, unpaid));
        when(menuMapper.selectList(any())).thenReturn(List.of(menu));
        when(userMapper.selectList(any())).thenReturn(List.of(paidUser, unpaidUser));

        Map<String, Object> result = adminAggregateService.getDailyAggregate(LocalDate.of(2026, 4, 21));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> menus = (List<Map<String, Object>>) result.get("menus");
        assertEquals(1, menus.size());
        assertEquals(1, menus.get(0).get("paidCount"));
        assertEquals(2, menus.get(0).get("unpaidCount"));
        assertEquals(List.of("Sato"), menus.get(0).get("paidUserNames"));
        assertEquals(List.of("Suzuki"), menus.get(0).get("unpaidUserNames"));
    }
}
