package com.example.demo;

import com.example.demo.entity.Reservation;
import com.example.demo.security.JwtService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.SystemSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private SystemSettingService systemSettingService;

    private String userToken;

    @BeforeEach
    void setUp() {
        userToken = jwtService.generateUserToken(7L, "TestUser");
        when(systemSettingService.getMinReservationThreshold()).thenReturn(6);
        when(reservationService.getActiveReservationCount(any())).thenReturn(1);
    }

    @Test
    void reservationValidationErrorsReturnConcrete400Message() throws Exception {
        when(reservationService.createReservation(eq(7L), any()))
                .thenThrow(new IllegalArgumentException("予約可能時間を過ぎています。"));

        mockMvc.perform(post("/api/user/reservations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuId\":1,\"reservationDate\":\"2026-04-16\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("予約可能時間を過ぎています。"));
    }

    @Test
    void userCanFetchOwnReservations() throws Exception {
        Reservation reservation = Reservation.builder()
                .id(10L)
                .userId(7L)
                .menuId(1L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("UNPAID")
                .build();

        when(reservationService.getUserReservations(7L)).thenReturn(List.of(reservation));

        mockMvc.perform(get("/api/user/reservations")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(10))
                .andExpect(jsonPath("$.data[0].paymentStatus").value("UNPAID"));
    }

    @Test
    void userCanUpdateOwnOrderStatus() throws Exception {
        Reservation reservation = Reservation.builder()
                .id(10L)
                .userId(7L)
                .menuId(1L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("UNPAID")
                .orderStatus("CONFIRMED")
                .build();

        when(reservationService.updateOrderStatus(7L, 10L, "CONFIRMED")).thenReturn(reservation);

        mockMvc.perform(patch("/api/user/reservations/10/status")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderStatus\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderStatus").value("CONFIRMED"));
    }

    @Test
    void reservationCreateReturnsThresholdInfo() throws Exception {
        Reservation reservation = Reservation.builder()
                .id(10L)
                .userId(7L)
                .menuId(1L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .quantity(2)
                .build();

        when(reservationService.createReservation(eq(7L), any())).thenReturn(reservation);

        mockMvc.perform(post("/api/user/reservations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuId\":1,\"reservationDate\":\"2026-04-16\",\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reservation.quantity").value(2))
                .andExpect(jsonPath("$.data.minReservationThreshold").value(6))
                .andExpect(jsonPath("$.data.thresholdMet").value(false))
                .andExpect(jsonPath("$.data.remainingToThreshold").value(5));
    }

    @Test
    void userCanCheckoutCartItems() throws Exception {
        Reservation first = Reservation.builder()
                .id(10L)
                .userId(7L)
                .menuId(1L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .paymentMethod("PAYPAY")
                .quantity(2)
                .build();

        when(reservationService.createReservationsFromCart(eq(7L), any()))
                .thenReturn(List.of(first));

        mockMvc.perform(post("/api/user/reservations/cart-checkout")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[{\"menuId\":1,\"reservationDate\":\"2026-04-16\",\"quantity\":1,\"paymentMethod\":\"PAYPAY\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].quantity").value(2))
                .andExpect(jsonPath("$.data[0].paymentMethod").value("PAYPAY"));
    }

    @Test
    void reservationCreateReturnsDailyLimitMessage() throws Exception {
        when(reservationService.createReservation(eq(7L), any()))
                .thenThrow(new IllegalArgumentException("1日1件まで予約できます。"));

        mockMvc.perform(post("/api/user/reservations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"menuId\":1,\"reservationDate\":\"2026-04-16\",\"quantity\":1,\"paymentMethod\":\"CARD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("1日1件まで予約できます。"));
    }

    @Test
    void userCanRequestCashPaymentReview() throws Exception {
        Reservation reservation = Reservation.builder()
                .id(10L)
                .userId(7L)
                .paymentStatus("PAYMENT_REQUESTED")
                .paymentMethod("CASH")
                .build();

        when(reservationService.markAsPaid(7L, 10L)).thenReturn(reservation);

        mockMvc.perform(patch("/api/user/reservations/10/payment")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentStatus\":\"PAYMENT_REQUESTED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentStatus").value("PAYMENT_REQUESTED"));
    }
}