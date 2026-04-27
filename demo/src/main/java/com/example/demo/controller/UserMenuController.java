package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.entity.Menu;
import com.example.demo.service.MenuService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.SystemSettingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// この Controller は一般使用者が今日のメニューを見るための API 入口
@RestController
@RequestMapping("/api/user/menus")
public class UserMenuController {

    private final MenuService menuService;
    private final SystemSettingService systemSettingService;
    private final ReservationService reservationService;

    public UserMenuController(MenuService menuService, SystemSettingService systemSettingService, ReservationService reservationService) {
        this.menuService = menuService;
        this.systemSettingService = systemSettingService;
        this.reservationService = reservationService;
    }

    // 今日予約できるメニューを取得
    @GetMapping("/today")
    public ApiResponse<?> getTodayMenus() {
        List<Menu> menus = menuService.getMenusByDate(LocalDate.now());
        String cutoffTime = systemSettingService.getReservationCutoffTime().toString();
        int threshold = systemSettingService.getMinReservationThreshold();
        int currentCount = reservationService.getActiveReservationCount(LocalDate.now());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("menus", menus);
        data.put("cutoffTime", cutoffTime);
        data.put("minReservationThreshold", threshold);
        data.put("currentReservationCount", currentCount);
        data.put("thresholdMet", currentCount >= threshold);

        return ApiResponse.ok(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getMenuById(@PathVariable Long id) {
        Menu menu = menuService.getMenuById(id);
        if (menu == null || Boolean.TRUE.equals(menu.getDeleted())) {
            return ApiResponse.error("指定したメニューが見つかりません。");
        }
        return ApiResponse.ok(menu);
    }

    @GetMapping("/home")
    public ApiResponse<?> getHomeMenus() {
        LocalDate today = LocalDate.now();
        // 今週の月曜日〜金曜日（今日が土日なら来週分）
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.FRIDAY));

        List<Menu> todayMenus = menuService.getMenusByDate(today);
        List<Menu> weeklyMenus = menuService.getMenusByDateRange(weekStart, weekEnd).stream()
                .filter(menu -> !today.equals(menu.getAvailableDate()))
                .toList();

        String cutoffTime = systemSettingService.getReservationCutoffTime().toString();
        int threshold = systemSettingService.getMinReservationThreshold();
        int currentCount = reservationService.getActiveReservationCount(today);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("todayMenu", todayMenus.isEmpty() ? null : todayMenus.get(0));
        data.put("weeklyMenus", weeklyMenus);
        data.put("cutoffTime", cutoffTime);
        data.put("minReservationThreshold", threshold);
        data.put("currentReservationCount", currentCount);
        data.put("thresholdMet", currentCount >= threshold);

        return ApiResponse.ok(data);
    }
}