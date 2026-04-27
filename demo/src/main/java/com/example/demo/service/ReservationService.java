package com.example.demo.service;

// 匯入 LambdaQueryWrapper，拿來查重複預約條件
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// 匯入 DTO、實體、Mapper
import com.example.demo.dto.ReservationRequest;
import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

// 這個 Service 專門處理預約規則
@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final MenuService menuService;
    private final SystemSettingService systemSettingService;
    private final UserService userService;
    private final MailService mailService;
    private final Clock clock;

    // 正常執行時用系統時間
    @Autowired
    public ReservationService(ReservationMapper reservationMapper,
                              MenuService menuService,
                              SystemSettingService systemSettingService,
                              UserService userService,
                              MailService mailService) {
        this(reservationMapper, menuService, systemSettingService, userService, mailService, Clock.system(ZoneId.of("Asia/Tokyo")));
    }

    // 測試時可以注入固定時間，方便驗證 9:45 規則
    public ReservationService(ReservationMapper reservationMapper,
                              MenuService menuService,
                              SystemSettingService systemSettingService,
                              UserService userService,
                              MailService mailService,
                              Clock clock) {
        this.reservationMapper = reservationMapper;
        this.menuService = menuService;
        this.systemSettingService = systemSettingService;
        this.userService = userService;
        this.mailService = mailService;
        this.clock = clock;
    }

    // 建立預約，若當日有效預約數達到門檻則自動成團確認
    public Reservation createReservation(Long userId, ReservationRequest request) {
        validateReservationRequest(request);

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate effectiveReservationDate = resolveEffectiveReservationDate(request);

        ensureBeforeCutoff(now);

        Long sameDayOrderCount = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getUserId, userId)
                        .eq(Reservation::getReservationDate, effectiveReservationDate)
                        .ne(Reservation::getOrderStatus, "CANCELLED"));
        if (sameDayOrderCount != null && sameDayOrderCount > 0) {
            throw new IllegalArgumentException("1日1件まで予約できます。");
        }

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .menuId(request.getMenuId())
                .reservationDate(effectiveReservationDate)
                .quantity(request.getQuantity())
                .paymentStatus("UNPAID")
                .orderStatus("RESERVED")
                .paymentMethod(normalizePaymentMethod(request.getPaymentMethod()))
                .reservedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        reservationMapper.insert(reservation);

        // 成團門檻檢查：當日有效預約數 ≥ 門檻 → 自動將所有 RESERVED 升級為 CONFIRMED 並寄通知
        boolean groupBuyConfirmed = checkAndConfirmGroupBuy(effectiveReservationDate);

        // 若未達成團門檻，寄送予約受理通知
        if (!groupBuyConfirmed) {
            sendReservationReceivedMail(reservation);
        }

        return reservation;
    }

    // 成團門檻檢查：當日有效預約數達到門檻時，自動確認所有預約。回傳是否觸發成團。
    private boolean checkAndConfirmGroupBuy(LocalDate date) {
        int threshold = systemSettingService.getMinReservationThreshold();
        Long currentTotal = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getReservationDate, date)
                        .ne(Reservation::getOrderStatus, "CANCELLED"));
        int currentCount = currentTotal != null ? currentTotal.intValue() : 0;

        if (currentCount >= threshold) {
            confirmAllReservationsForDate(date);
            return true;
        }
        return false;
    }

    public List<Reservation> createReservationsFromCart(Long userId, List<ReservationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("カートに商品がありません。");
        }

        if (requests.size() > 1) {
            throw new IllegalArgumentException("1日1件まで予約できます。");
        }

        return requests.stream()
                .map(request -> createReservation(userId, request))
                .toList();
        // 成團門檻檢查已在 createReservation 內自動執行
    }

    public List<Reservation> getUserReservations(Long userId) {
        return reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getUserId, userId)
                        .orderByDesc(Reservation::getReservationDate)
                        .orderByDesc(Reservation::getId));
    }

    // 查詢指定日期的有效預約數（排除已取消的）
    public int getActiveReservationCount(LocalDate date) {
        Long currentTotal = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getReservationDate, date)
                        .ne(Reservation::getOrderStatus, "CANCELLED"));
        return currentTotal != null ? currentTotal.intValue() : 0;
    }

    // 把預約改成已付款
    public Reservation markAsPaid(Long userId, Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("予約が存在しません。");
        }

        if (!userId.equals(reservation.getUserId())) {
            throw new IllegalArgumentException("他人の予約は更新できません。");
        }

        if ("PAID".equals(reservation.getPaymentStatus())) {
            throw new IllegalArgumentException("すでに支払済みです。");
        }

        if ("PAYMENT_REQUESTED".equals(reservation.getPaymentStatus())) {
            throw new IllegalArgumentException("すでに支払申請済みです。管理者の確認をお待ちください。");
        }

        if ("CASH".equals(reservation.getPaymentMethod())) {
            reservation.setPaymentStatus("PAYMENT_REQUESTED");
            reservation.setUpdatedAt(LocalDateTime.now(clock));
            reservationMapper.updateById(reservation);
            return reservation;
        }

        reservation.setPaymentStatus("PAID");
        reservation.setPaymentCheckedAt(LocalDateTime.now(clock));
        reservation.setUpdatedAt(LocalDateTime.now(clock));
        reservationMapper.updateById(reservation);
        return reservation;
    }

    public Reservation approvePaymentAsAdmin(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("予約が存在しません。");
        }

        if (!"CASH".equals(reservation.getPaymentMethod())) {
            throw new IllegalArgumentException("現金支払いのみ承認できます。");
        }

        if (!"PAYMENT_REQUESTED".equals(reservation.getPaymentStatus())) {
            throw new IllegalArgumentException("支払申請済みの予約のみ承認できます。");
        }

        reservation.setPaymentStatus("PAID");
        reservation.setPaymentCheckedAt(LocalDateTime.now(clock));
        reservation.setUpdatedAt(LocalDateTime.now(clock));
        reservationMapper.updateById(reservation);
        sendPaymentApprovedMail(reservation);
        return reservation;
    }

    public Reservation updateOrderStatus(Long userId, Long reservationId, String orderStatus) {
        if (orderStatus == null || orderStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("注文状態を入力してください。");
        }

        if (!List.of("RESERVED", "CONFIRMED", "CANCELLED").contains(orderStatus)) {
            throw new IllegalArgumentException("未対応の注文状態です。");
        }

        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("予約が存在しません。");
        }

        if (!userId.equals(reservation.getUserId())) {
            throw new IllegalArgumentException("他人の予約は更新できません。");
        }

        reservation.setOrderStatus(orderStatus);
        reservation.setUpdatedAt(LocalDateTime.now(clock));
        reservationMapper.updateById(reservation);
        return reservation;
    }

    public Reservation updateOrderStatusAsAdmin(Long reservationId, String orderStatus) {
        if (orderStatus == null || orderStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("注文状態を入力してください。");
        }

        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("予約が存在しません。");
        }

        reservation.setOrderStatus(orderStatus);
        reservation.setUpdatedAt(LocalDateTime.now(clock));
        reservationMapper.updateById(reservation);
        return reservation;
    }

    public int clearAllReservations() {
        return reservationMapper.delete(null);
    }

    public Long countActiveReservationsForMenu(Long menuId) {
        if (menuId == null) {
            throw new IllegalArgumentException("メニューIDが必要です。");
        }

        Long count = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getMenuId, menuId)
                        .ne(Reservation::getOrderStatus, "CANCELLED"));
        return count == null ? 0L : count;
    }

    private void validateReservationRequest(ReservationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("予約内容がありません。");
        }

        if (request.getMenuId() == null) {
            throw new IllegalArgumentException("メニューを選択してください。");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("数量は1件以上指定してください。");
        }

        normalizePaymentMethod(request.getPaymentMethod());
    }

    private LocalDate resolveEffectiveReservationDate(ReservationRequest request) {
        Menu menu = menuService.getMenuById(request.getMenuId());
        if (menu == null) {
            throw new IllegalArgumentException("メニューが存在しません。");
        }

        if (request.getReservationDate() != null && !request.getReservationDate().equals(menu.getAvailableDate())) {
            throw new IllegalArgumentException("注文日がメニュー提供日と一致しません。");
        }

        LocalDate today = LocalDate.now(clock);
        if (!today.equals(menu.getAvailableDate())) {
            throw new IllegalArgumentException("本日提供中のメニューのみ注文できます。");
        }

        return menu.getAvailableDate();
    }

    private String normalizePaymentMethod(String paymentMethod) {
        String normalized = paymentMethod == null ? "CASH" : paymentMethod.trim().toUpperCase();
        if (!List.of("CARD", "PAYPAY", "CASH").contains(normalized)) {
            throw new IllegalArgumentException("支払方法は CARD / PAYPAY / CASH から選択してください。");
        }
        return normalized;
    }

    private void ensureBeforeCutoff(LocalDateTime now) {
        if (!now.toLocalTime().isBefore(systemSettingService.getReservationCutoffTime())) {
            throw new IllegalArgumentException("予約可能時間を過ぎています。");
        }
    }
    private void sendPaymentApprovedMail(Reservation reservation) {
        User user = userService.getUserById(reservation.getUserId());
        Menu menu = menuService.getMenuById(reservation.getMenuId());
        if (user == null || menu == null) {
            return;
        }

        String body = "現金支払いの確認が完了しました。\n\n注文日: " + reservation.getReservationDate()
                + "\nメニュー: " + menu.getName()
                + "\n数量: " + reservation.getQuantity()
                + "\n支払状態: 支払済み";
        mailService.sendMail(user.getEmail(), "支払い確認完了", body);
    }

    private void sendReservationReceivedMail(Reservation reservation) {
        User user = userService.getUserById(reservation.getUserId());
        Menu menu = menuService.getMenuById(reservation.getMenuId());
        if (user == null || menu == null) {
            return;
        }

        int threshold = systemSettingService.getMinReservationThreshold();
        Long currentTotal = reservationMapper.selectCount(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getReservationDate, reservation.getReservationDate())
                        .ne(Reservation::getOrderStatus, "CANCELLED"));
        int currentCount = currentTotal != null ? currentTotal.intValue() : 0;
        int remaining = threshold - currentCount;

        StringBuilder body = new StringBuilder();
        body.append("ご予約ありがとうございます。\n\n");
        body.append("注文日: ").append(reservation.getReservationDate()).append("\n");
        body.append("メニュー: ").append(menu.getName()).append("\n");
        body.append("数量: ").append(reservation.getQuantity()).append("\n");
        body.append("支払方法: ").append(reservation.getPaymentMethod()).append("\n\n");
        if (remaining > 0) {
            body.append("現在の予約状況: ").append(currentCount).append(" / ").append(threshold).append(" 件\n");
            body.append("あと ").append(remaining).append(" 件で成団確定です。\n\n");
            body.append("成団確定後に再度ご連絡いたします。");
        } else {
            body.append("成団済みです。当日をお楽しみください。");
        }

        mailService.sendMail(user.getEmail(), "【予約受理】ご予約を受け付けました", body.toString());
    }

    // 管理者單筆退款：將預約改為取消＋退款，並記錄退款原因
    @Transactional
    public Reservation refundReservation(Long reservationId, String reason) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("予約が存在しません。");
        }

        if ("REFUNDED".equals(reservation.getPaymentStatus())) {
            throw new IllegalArgumentException("すでに返金済みです。");
        }

        if ("CANCELLED".equals(reservation.getOrderStatus())) {
            throw new IllegalArgumentException("すでにキャンセル済みの予約です。");
        }

        reservation.setOrderStatus("CANCELLED");
        reservation.setPaymentStatus("REFUNDED");
        reservation.setRefundReason(reason);
        reservation.setUpdatedAt(LocalDateTime.now(clock));
        reservationMapper.updateById(reservation);
        sendRefundMail(reservation);
        return reservation;
    }

    // 成団確定：當日所有預約改為 CONFIRMED 並發信通知
    @Transactional
    public int confirmAllReservationsForDate(LocalDate date) {
        List<Reservation> reservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getReservationDate, date)
                        .ne(Reservation::getOrderStatus, "CANCELLED"));

        int confirmedCount = 0;
        for (Reservation reservation : reservations) {
            if (!"CONFIRMED".equals(reservation.getOrderStatus())) {
                reservation.setOrderStatus("CONFIRMED");
                reservationMapper.updateById(reservation);
                confirmedCount++;
                sendGroupBuyConfirmedMail(reservation, date);
            }
        }
        return confirmedCount;
    }

    // 成団失敗：當日所有預約改為 CANCELLED 並發信通知（含退款）
    @Transactional
    public int cancelAllReservationsForDate(LocalDate date) {
        List<Reservation> reservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getReservationDate, date)
                        .ne(Reservation::getOrderStatus, "CANCELLED"));

        int cancelledCount = 0;
        for (Reservation reservation : reservations) {
            reservation.setOrderStatus("CANCELLED");
            reservation.setPaymentStatus("REFUNDED");
            reservationMapper.updateById(reservation);
            cancelledCount++;
            sendGroupBuyCancelledMail(reservation, date);
        }
        return cancelledCount;
    }

    private void sendGroupBuyConfirmedMail(Reservation reservation, LocalDate date) {
        User user = userService.getUserById(reservation.getUserId());
        Menu menu = menuService.getMenuById(reservation.getMenuId());
        if (user == null || menu == null) return;

        String body = "ご予約の成団が確定しました！\n\n"
                + "注文日: " + date + "\n"
                + "メニュー: " + menu.getName() + "\n"
                + "数量: " + reservation.getQuantity() + "\n"
                + "支払方法: " + reservation.getPaymentMethod() + "\n\n"
                + "当日をお楽しみください。";
        mailService.sendMail(user.getEmail(), "【成団確定】ご予約が確定しました", body);
    }

    private void sendGroupBuyCancelledMail(Reservation reservation, LocalDate date) {
        User user = userService.getUserById(reservation.getUserId());
        Menu menu = menuService.getMenuById(reservation.getMenuId());
        if (user == null || menu == null) return;

        String body = "誠に残念ながら、ご予約の成団人数に達しませんでした。\n\n"
                + "注文日: " + date + "\n"
                + "メニュー: " + menu.getName() + "\n"
                + "数量: " + reservation.getQuantity() + "\n\n"
                + "お支払い済みの金額は全額返金処理いたします。\n"
                + "返金完了まで 3〜5 営業日いただきます。";
        mailService.sendMail(user.getEmail(), "【成団失敗】ご予約をキャンセルいたしました", body);
    }

    private void sendRefundMail(Reservation reservation) {
        User user = userService.getUserById(reservation.getUserId());
        Menu menu = menuService.getMenuById(reservation.getMenuId());
        if (user == null || menu == null) return;

        String body = "ご予約のキャンセル・返金処理が完了しました。\n\n"
                + "注文日: " + reservation.getReservationDate() + "\n"
                + "メニュー: " + menu.getName() + "\n"
                + "数量: " + reservation.getQuantity() + "\n"
                + "キャンセル理由: " + (reservation.getRefundReason() != null ? reservation.getRefundReason() : "特になし") + "\n\n"
                + "お支払い済みの金額は全額返金処理いたします。\n"
                + "返金完了まで 3〜5 営業日いただきます。";
        mailService.sendMail(user.getEmail(), "【返金完了】ご予約をキャンセルいたしました", body);
    }
}
