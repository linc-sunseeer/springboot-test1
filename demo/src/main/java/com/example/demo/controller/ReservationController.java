package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CartCheckoutRequest;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.dto.UserActionRequest;
import com.example.demo.entity.Reservation;
import com.example.demo.security.CurrentUserService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.SystemSettingService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final CurrentUserService currentUserService;
    private final SystemSettingService systemSettingService;

    public ReservationController(ReservationService reservationService, CurrentUserService currentUserService,
                                  SystemSettingService systemSettingService) {
        this.reservationService = reservationService;
        this.currentUserService = currentUserService;
        this.systemSettingService = systemSettingService;
    }

    @PostMapping
    public ApiResponse<?> createReservation(Authentication authentication, @RequestBody ReservationRequest request) {
        Long userId = currentUserService.requireUserId(authentication);
        Reservation reservation = reservationService.createReservation(userId, request);

        // 回傳成團門檻資訊
        int threshold = systemSettingService.getMinReservationThreshold();
        int currentCount = reservationService.getActiveReservationCount(reservation.getReservationDate());
        boolean thresholdMet = currentCount >= threshold;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("reservation", reservation);
        data.put("minReservationThreshold", threshold);
        data.put("currentReservationCount", currentCount);
        data.put("thresholdMet", thresholdMet);

        if (thresholdMet) {
            return ApiResponse.ok("成団が確定しました！", data);
        } else {
            data.put("remainingToThreshold", threshold - currentCount);
            return ApiResponse.ok(String.format("予約を受け付けました。あと%d件で成団確定します。", threshold - currentCount), data);
        }
    }

    @PostMapping("/cart-checkout")
    public ApiResponse<?> checkoutCart(Authentication authentication, @RequestBody CartCheckoutRequest request) {
        Long userId = currentUserService.requireUserId(authentication);
        List<Reservation> reservations = reservationService.createReservationsFromCart(userId, request.getItems());
        return ApiResponse.ok("created", reservations);
    }

    @GetMapping
    public ApiResponse<?> getUserReservations(Authentication authentication) {
        Long userId = currentUserService.requireUserId(authentication);
        List<Reservation> reservations = reservationService.getUserReservations(userId);
        return ApiResponse.ok(reservations);
    }

    @GetMapping("/today")
    public ApiResponse<?> getTodayReservations(Authentication authentication) {
        Long userId = currentUserService.requireUserId(authentication);
        List<Reservation> reservations = reservationService.getUserReservations(userId);
        return ApiResponse.ok(reservations);
    }

    @PatchMapping("/{reservationId}/payment")
    public ApiResponse<?> markAsPaid(Authentication authentication,
                                      @PathVariable Long reservationId,
                                      @RequestBody UserActionRequest request) {
        if (!"PAID".equals(request.getPaymentStatus()) && !"PAYMENT_REQUESTED".equals(request.getPaymentStatus())) {
            throw new IllegalArgumentException("支払状態は PAID または PAYMENT_REQUESTED のみ更新可能です。");
        }

        Long userId = currentUserService.requireUserId(authentication);
        Reservation reservation = reservationService.markAsPaid(userId, reservationId);
        return ApiResponse.ok("updated", reservation);
    }

    @PatchMapping("/{reservationId}/status")
    public ApiResponse<?> updateOrderStatus(Authentication authentication,
                                             @PathVariable Long reservationId,
                                             @RequestBody UserActionRequest request) {
        Long userId = currentUserService.requireUserId(authentication);
        Reservation reservation = reservationService.updateOrderStatus(userId, reservationId, request.getOrderStatus());
        return ApiResponse.ok("updated", reservation);
    }
}