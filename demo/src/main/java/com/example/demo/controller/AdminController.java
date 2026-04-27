package com.example.demo.controller;

// 匯入 Menu 實體，管理者 CRUD 菜單時會用到
import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.dto.UserActionRequest;
import com.example.demo.service.AdminAggregateService;
import com.example.demo.service.MenuService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.SystemSettingService;
import com.example.demo.service.UserService;
// 匯入 Spring Web 註解，讓不同方法對應不同 API
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
// 匯入 ResponseEntity 與回應標頭，CSV 下載時會用到
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import com.example.demo.dto.ApiResponse;

// 這個 Controller 是管理者用的 API 入口
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final MenuService menuService;
    private final AdminAggregateService adminAggregateService;
    private final SystemSettingService systemSettingService;
    private final ReservationService reservationService;
    private final UserService userService;

    public AdminController(MenuService menuService,
                           AdminAggregateService adminAggregateService,
                           SystemSettingService systemSettingService,
                           ReservationService reservationService,
                           UserService userService) {
        this.menuService = menuService;
        this.adminAggregateService = adminAggregateService;
        this.systemSettingService = systemSettingService;
        this.reservationService = reservationService;
        this.userService = userService;
    }

    // 管理者ダッシュボード統計
    @GetMapping("/dashboard")
    public ApiResponse<?> getDashboard() {
        LocalDate today = LocalDate.now();
        Map<String, Object> dailyAggregate = adminAggregateService.getDailyAggregate(today);

        Map<String, Object> dashboard = new java.util.LinkedHashMap<>();
        dashboard.put("date", today.toString());
        dashboard.put("totalReservations", dailyAggregate.get("totalReservations"));
        dashboard.put("paidCount", dailyAggregate.get("paidCount"));
        dashboard.put("unpaidCount", dailyAggregate.get("unpaidCount"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> menus = (List<Map<String, Object>>) dailyAggregate.get("menus");
        dashboard.put("menuCount", menus != null ? menus.size() : 0);

        // 料金計算：メニュー価格 × 予約数
        int totalRevenue = 0;
        int paidRevenue = 0;
        if (menus != null) {
            for (Map<String, Object> menu : menus) {
                Long menuId = ((Number) menu.get("menuId")).longValue();
                int count = ((Number) menu.get("reservationCount")).intValue();
                int paid = ((Number) menu.get("paidCount")).intValue();
                Menu menuEntity = menuService.getAllMenus().stream()
                        .filter(m -> m.getId().equals(menuId))
                        .findFirst()
                        .orElse(null);
                int price = menuEntity != null && menuEntity.getPrice() != null ? menuEntity.getPrice().intValue() : 0;
                totalRevenue += price * count;
                paidRevenue += price * paid;
            }
        }
        dashboard.put("totalRevenue", totalRevenue);
        dashboard.put("paidRevenue", paidRevenue);
        dashboard.put("menus", menus);

        // ユーザー数
        dashboard.put("totalUsers", adminAggregateService.getAdminUsers().size());

        return ApiResponse.ok(dashboard);
    }

    // 取得全部菜單
    @GetMapping("/menus")
    public ApiResponse<?> getMenus() {
        return ApiResponse.ok(menuService.getAllMenus());
    }

    // 新增菜單
    @PostMapping("/menus")
    public ApiResponse<?> createMenu(@RequestBody Menu menu) {
        return ApiResponse.ok("created", menuService.createMenu(menu));
    }

    // 修改菜單
    @PutMapping("/menus/{menuId}")
    public ApiResponse<?> updateMenu(@PathVariable Long menuId, @RequestBody Menu menu) {
        return ApiResponse.ok("updated", menuService.updateMenu(menuId, menu));
    }

    // 刪除菜單
    @DeleteMapping("/menus/{menuId}")
    public ApiResponse<?> deleteMenu(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ApiResponse.success("deleted");
    }

    @GetMapping("/menus/{menuId}/reservation-summary")
    public ApiResponse<?> getMenuReservationSummary(@PathVariable Long menuId) {
        long activeReservationCount = reservationService.countActiveReservationsForMenu(menuId);
        return ApiResponse.ok(Map.of(
                "menuId", menuId,
                "activeReservationCount", activeReservationCount,
                "hasActiveReservations", activeReservationCount > 0
        ));
    }

    @PostMapping(value = "/menus/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> uploadMenuImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = menuService.storeMenuImage(file);
        return ApiResponse.ok("uploaded", Map.of("imageUrl", imageUrl));
    }

    // 日次集計
    @GetMapping("/aggregates/daily")
    public ApiResponse<?> getDailyAggregate(@RequestParam LocalDate date) {
        return ApiResponse.ok(adminAggregateService.getDailyAggregate(date));
    }

    // 月次集計
    @GetMapping("/aggregates/monthly")
    public ApiResponse<?> getMonthlyAggregate(@RequestParam String yearMonth) {
        return ApiResponse.ok(adminAggregateService.getMonthlyAggregate(yearMonth));
    }

    // 匯出月次 CSV
    @GetMapping("/aggregates/monthly/export")
    public ResponseEntity<byte[]> exportMonthlyAggregateCsv(@RequestParam String yearMonth) {
        String csv = adminAggregateService.exportMonthlyAggregateCsv(yearMonth);
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"monthly-aggregate-" + yearMonth + ".csv\"; filename*=UTF-8''monthly-aggregate-" + yearMonth + ".csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header("X-Content-Type-Options", "nosniff")
                .body(csvBytes);
    }

    // 匯出日次 CSV
    @GetMapping("/aggregates/daily/export")
    public ResponseEntity<byte[]> exportDailyAggregate(@RequestParam LocalDate date) {
        String csv = adminAggregateService.exportDailyAggregateCsv(date);
        byte[] csvBytes = csv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"daily-aggregate-" + date + ".csv\"; filename*=UTF-8''daily-aggregate-" + date + ".csv")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .header("X-Content-Type-Options", "nosniff")
                .body(csvBytes);
    }

    // 查詢某一天的預約使用者名單
    @GetMapping("/reservations/daily-users")
    public ApiResponse<?> getDailyReservationUsers(@RequestParam LocalDate date) {
        return ApiResponse.ok(adminAggregateService.getDailyReservationUsers(date));
    }

    @GetMapping("/users")
    public ApiResponse<?> getAdminUsers() {
        return ApiResponse.ok(adminAggregateService.getAdminUsers());
    }

    @GetMapping("/users/{userId}/reservations")
    public ApiResponse<?> getUserReservationHistory(@PathVariable Long userId) {
        return ApiResponse.ok(adminAggregateService.getUserReservationHistory(userId));
    }

    // 取得預約截止時間設定
    @GetMapping("/settings/reservation-time")
    public ApiResponse<?> getReservationCutoffTime() {
        return ApiResponse.ok(systemSettingService.getReservationCutoffTime().toString());
    }

    // 更新預約截止時間設定
    @PutMapping("/settings/reservation-time")
    public ApiResponse<?> updateReservationCutoffTime(@RequestBody Map<String, String> payload) {
        String timeStr = payload.get("time");
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("時間を入力してください。");
        }
        systemSettingService.updateReservationCutoffTime(timeStr);
        return ApiResponse.success("updated");
    }

    @GetMapping("/settings/min-threshold")
    public ApiResponse<?> getMinReservationThreshold() {
        return ApiResponse.ok(systemSettingService.getMinReservationThreshold());
    }

    @PutMapping("/settings/min-threshold")
    public ApiResponse<?> updateMinReservationThreshold(@RequestBody Map<String, Integer> payload) {
        Integer value = payload.get("value");
        if (value == null) {
            throw new IllegalArgumentException("値を入力してください。");
        }
        systemSettingService.updateMinReservationThreshold(value);
        return ApiResponse.success("updated");
    }

    @PutMapping("/reservations/{reservationId}/status")
    public ApiResponse<?> updateReservationStatus(@PathVariable Long reservationId,
                                                       @RequestBody UserActionRequest request) {
        Reservation reservation = reservationService.updateOrderStatusAsAdmin(reservationId, request.getOrderStatus());
        return ApiResponse.ok("updated", reservation);
    }

    @PatchMapping("/reservations/{reservationId}/payment")
    public ApiResponse<?> approveReservationPayment(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.approvePaymentAsAdmin(reservationId);
        return ApiResponse.ok("updated", reservation);
    }

    // 管理者單筆退款（含退款原因）
    @PatchMapping("/reservations/{reservationId}/refund")
    public ApiResponse<?> refundReservation(@PathVariable Long reservationId,
                                                    @RequestBody UserActionRequest request) {
        String reason = request.getRefundReason();
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("返金理由を入力してください。");
        }
        Reservation reservation = reservationService.refundReservation(reservationId, reason.trim());
        return ApiResponse.ok("返金完了", reservation);
    }

    // 成団確定：當日所有預約改為 CONFIRMED 並發信通知
    @PostMapping("/reservations/confirm-all")
    public ApiResponse<?> confirmAllReservations(@RequestParam LocalDate date) {
        int confirmedCount = reservationService.confirmAllReservationsForDate(date);
        return ApiResponse.ok("成団確定", Map.of("confirmedCount", confirmedCount));
    }

    // 成団失敗：當日所有預約改為 CANCELLED 並發信通知（含退款）
    @PostMapping("/reservations/cancel-all")
    public ApiResponse<?> cancelAllReservations(@RequestParam LocalDate date) {
        int cancelledCount = reservationService.cancelAllReservationsForDate(date);
        return ApiResponse.ok("成団失敗・全件キャンセル", Map.of("cancelledCount", cancelledCount));
    }

    // 管理者刪除使用者（需管理者密碼確認）
    @DeleteMapping("/users/{userId}")
    public ApiResponse<?> deleteUser(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String adminPassword = payload.get("adminPassword");
        if (adminPassword == null || adminPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("管理者パスワードを入力してください。");
        }
        userService.deleteUserWithPasswordVerification(userId, adminPassword);
        return ApiResponse.success("ユーザーを削除しました。");
    }

}
