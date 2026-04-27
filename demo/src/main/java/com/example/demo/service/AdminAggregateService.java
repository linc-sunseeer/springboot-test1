package com.example.demo.service;

// 匯入 LambdaQueryWrapper，查日次資料時會用到
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// 匯入實體與 mapper，因為統計要從預約和菜單資料組出來
import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.mapper.MenuMapper;
import com.example.demo.mapper.ReservationMapper;
import com.example.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

// 這個 Service 專門做管理者看到的統計資料
@Service
public class AdminAggregateService {

    private final ReservationMapper reservationMapper;
    private final MenuMapper menuMapper;
    private final UserMapper userMapper;

    public AdminAggregateService(ReservationMapper reservationMapper, MenuMapper menuMapper, UserMapper userMapper) {
        this.reservationMapper = reservationMapper;
        this.menuMapper = menuMapper;
        this.userMapper = userMapper;
    }

    // 日次集計：看某一天總共有多少預約、已付款多少、未付款多少、每個菜單幾份
    public Map<String, Object> getDailyAggregate(LocalDate date) {
        List<Reservation> reservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, date));

        int totalReservations = reservations.stream().mapToInt(this::getReservationQuantity).sum();
        int paidCount = reservations.stream()
                .filter(r -> "PAID".equals(r.getPaymentStatus()))
                .mapToInt(this::getReservationQuantity)
                .sum();
        int unpaidCount = totalReservations - paidCount;

        Map<Long, Menu> menuMap = menuMapper.selectList(null).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu, (a, b) -> a));
        Map<Long, User> userMap = userMapper.selectList(null).stream()
                .collect(Collectors.toMap(User::getId, user -> user, (a, b) -> a));

        List<Map<String, Object>> menus = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getMenuId, LinkedHashMap::new, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Reservation> menuReservations = reservations.stream()
                            .filter(reservation -> entry.getKey().equals(reservation.getMenuId()))
                            .toList();
                    int reservationCount = menuReservations.stream()
                            .mapToInt(this::getReservationQuantity)
                            .sum();
                    int paidMenuCount = menuReservations.stream()
                            .filter(reservation -> "PAID".equals(reservation.getPaymentStatus()))
                            .mapToInt(this::getReservationQuantity)
                            .sum();
                    int unpaidMenuCount = reservationCount - paidMenuCount;

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("menuId", entry.getKey());
                    item.put("menuName", menuMap.containsKey(entry.getKey()) ? menuMap.get(entry.getKey()).getName() : "Unknown");
                    item.put("reservationCount", reservationCount);
                    item.put("paidCount", paidMenuCount);
                    item.put("unpaidCount", unpaidMenuCount);
                    item.put("paidUserNames", collectUserNames(menuReservations, userMap, true));
                    item.put("unpaidUserNames", collectUserNames(menuReservations, userMap, false));
                    return item;
                })
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("totalReservations", totalReservations);
        result.put("paidCount", paidCount);
        result.put("unpaidCount", unpaidCount);
        result.put("menus", menus);
        return result;
    }

    // 月次集計：看某個月份每天有幾筆預約
    public Map<String, Object> getMonthlyAggregate(String yearMonth) {
        YearMonth targetMonth = YearMonth.parse(yearMonth);

        List<Reservation> reservations = reservationMapper.selectList(null).stream()
                .filter(reservation -> YearMonth.from(reservation.getReservationDate()).equals(targetMonth))
                .toList();

        Map<Long, Menu> menuMap = menuMapper.selectList(null).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu, (a, b) -> a));

        List<Map<String, Object>> days = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getReservationDate, LinkedHashMap::new, Collectors.toList()))
                .entrySet()
                .stream()
                .map(entry -> {
                    List<Reservation> dayReservations = entry.getValue();
                    long paidCount = dayReservations.stream()
                            .filter(reservation -> "PAID".equals(reservation.getPaymentStatus()))
                            .mapToInt(this::getReservationQuantity)
                            .sum();
                    int reservationCount = dayReservations.stream().mapToInt(this::getReservationQuantity).sum();
                    long unpaidCount = reservationCount - paidCount;
                    List<Map<String, Object>> menus = dayReservations.stream()
                            .collect(Collectors.groupingBy(Reservation::getMenuId, LinkedHashMap::new, Collectors.counting()))
                            .entrySet()
                            .stream()
                            .map(menuEntry -> {
                                Map<String, Object> menuItem = new LinkedHashMap<>();
                                menuItem.put("menuId", menuEntry.getKey());
                                menuItem.put("menuName", menuMap.containsKey(menuEntry.getKey()) ? menuMap.get(menuEntry.getKey()).getName() : "Unknown");
                                menuItem.put("reservationCount", dayReservations.stream()
                                        .filter(reservation -> menuEntry.getKey().equals(reservation.getMenuId()))
                                        .mapToInt(this::getReservationQuantity)
                                        .sum());
                                return menuItem;
                            })
                            .toList();

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("date", entry.getKey().toString());
                    item.put("reservationCount", reservationCount);
                    item.put("paidCount", paidCount);
                    item.put("unpaidCount", unpaidCount);
                    item.put("menus", menus);
                    return item;
                })
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("yearMonth", yearMonth);
        result.put("days", days);
        return result;
    }

    // 匯出月次集計 CSV
    public String exportMonthlyAggregateCsv(String yearMonth) {
        Map<String, Object> monthlyAggregate = getMonthlyAggregate(yearMonth);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> days = (List<Map<String, Object>>) monthlyAggregate.get("days");

        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("日付,メニュー名,予約数,支払済み数,未支払数\r\n");
        for (Map<String, Object> day : days) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> menus = (List<Map<String, Object>>) day.getOrDefault("menus", List.of());
            String dateStr = escapeCsv(day.get("date"));

            if (menus.isEmpty()) {
                csv.append(dateStr).append(",,,,\r\n");
            } else {
                for (Map<String, Object> menu : menus) {
                    csv.append(dateStr)
                            .append(',')
                            .append(escapeCsv(menu.get("menuName")))
                            .append(',')
                            .append(escapeCsv(menu.get("reservationCount")))
                            .append(',')
                            .append(escapeCsv(day.get("paidCount")))
                            .append(',')
                            .append(escapeCsv(day.get("unpaidCount")))
                            .append("\r\n");
                }
            }
        }
        return csv.toString();
    }

    // 匯出日次集計 CSV
    public String exportDailyAggregateCsv(LocalDate date) {
        Map<String, Object> dailyAggregate = getDailyAggregate(date);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> menus = (List<Map<String, Object>>) dailyAggregate.get("menus");

        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("sep=,\r\n");
        csv.append("日付,メニュー名,予約数,支払済み数,未支払数,支払済みユーザー,未支払ユーザー\r\n");
        for (Map<String, Object> menu : menus) {
            @SuppressWarnings("unchecked")
            List<String> paidUserNames = (List<String>) menu.getOrDefault("paidUserNames", List.of());
            @SuppressWarnings("unchecked")
            List<String> unpaidUserNames = (List<String>) menu.getOrDefault("unpaidUserNames", List.of());

            csv.append(escapeCsv(date))
                    .append(',')
                    .append(escapeCsv(menu.get("menuName")))
                    .append(',')
                    .append(escapeCsv(menu.get("reservationCount")))
                    .append(',')
                    .append(escapeCsv(menu.get("paidCount")))
                    .append(',')
                    .append(escapeCsv(menu.get("unpaidCount")))
                    .append(',')
                    .append(escapeCsv(String.join(" / ", paidUserNames)))
                    .append(',')
                    .append(escapeCsv(String.join(" / ", unpaidUserNames)))
                    .append("\r\n");
        }
        return csv.toString();
    }

    // 查某一天的預約使用者名單
    public Map<String, Object> getDailyReservationUsers(LocalDate date) {
        List<Reservation> reservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>().eq(Reservation::getReservationDate, date));

        Map<Long, Menu> menuMap = menuMapper.selectList(null).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu, (a, b) -> a));
        Map<Long, User> userMap = userMapper.selectList(null).stream()
                .collect(Collectors.toMap(User::getId, user -> user, (a, b) -> a));

        List<Map<String, Object>> users = reservations.stream()
                .map(reservation -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("reservationId", reservation.getId());
                    item.put("userId", reservation.getUserId());
                    item.put("userName", userMap.containsKey(reservation.getUserId()) ? userMap.get(reservation.getUserId()).getName() : "User-" + reservation.getUserId());
                    item.put("menuId", reservation.getMenuId());
                    item.put("menuName", menuMap.containsKey(reservation.getMenuId()) ? menuMap.get(reservation.getMenuId()).getName() : "Unknown");
                    item.put("paymentMethod", reservation.getPaymentMethod());
                    item.put("paymentStatus", reservation.getPaymentStatus());
                    item.put("orderStatus", reservation.getOrderStatus() == null ? "RESERVED" : reservation.getOrderStatus());
                    item.put("quantity", getReservationQuantity(reservation));
                    item.put("refundReason", reservation.getRefundReason());
                    return item;
                })
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date", date.toString());
        result.put("users", users);
        return result;
    }

    public List<Map<String, Object>> getAdminUsers() {
        List<User> users = userMapper.selectList(null);
        List<Reservation> reservations = reservationMapper.selectList(null);

        return users.stream()
                .map(user -> {
                    List<Reservation> userReservations = reservations.stream()
                            .filter(reservation -> user.getId().equals(reservation.getUserId()))
                            .toList();

                    long paidCount = userReservations.stream()
                            .filter(reservation -> "PAID".equals(reservation.getPaymentStatus()))
                            .mapToInt(this::getReservationQuantity)
                            .sum();

                    Reservation latestReservation = userReservations.stream()
                            .max(Comparator.comparing(Reservation::getReservationDate)
                                    .thenComparing(Reservation::getId))
                            .orElse(null);

                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("userId", user.getId());
                    item.put("name", user.getName());
                    item.put("email", user.getEmail());
                    item.put("reservationCount", userReservations.stream().mapToInt(this::getReservationQuantity).sum());
                    item.put("paidCount", paidCount);
                    item.put("latestOrderStatus", latestReservation == null || latestReservation.getOrderStatus() == null
                            ? null
                            : latestReservation.getOrderStatus());
                    return item;
                })
                .toList();
    }

    public List<Map<String, Object>> getUserReservationHistory(Long userId) {
        Map<Long, Menu> menuMap = menuMapper.selectList(null).stream()
                .collect(Collectors.toMap(Menu::getId, menu -> menu, (a, b) -> a));

        return reservationMapper.selectList(
                        new LambdaQueryWrapper<Reservation>()
                                .eq(Reservation::getUserId, userId)
                                .orderByDesc(Reservation::getReservationDate)
                                .orderByDesc(Reservation::getId))
                .stream()
                .map(reservation -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("reservationId", reservation.getId());
                    item.put("menuId", reservation.getMenuId());
                    item.put("menuName", menuMap.containsKey(reservation.getMenuId()) ? menuMap.get(reservation.getMenuId()).getName() : "Unknown");
                    item.put("reservationDate", reservation.getReservationDate().toString());
                    item.put("quantity", getReservationQuantity(reservation));
                    item.put("paymentStatus", reservation.getPaymentStatus());
                    item.put("orderStatus", reservation.getOrderStatus() == null ? "RESERVED" : reservation.getOrderStatus());
                    item.put("reservedAt", reservation.getReservedAt());
                    item.put("refundReason", reservation.getRefundReason());
                    return item;
                })
                .toList();
    }

    private int getReservationQuantity(Reservation reservation) {
        return reservation.getQuantity() == null ? 1 : reservation.getQuantity();
    }

    private List<String> collectUserNames(List<Reservation> reservations, Map<Long, User> userMap, boolean paid) {
        List<String> userNames = new ArrayList<>();
        for (Reservation reservation : reservations) {
            boolean isPaid = "PAID".equals(reservation.getPaymentStatus());
            if (paid != isPaid) {
                continue;
            }

            String userName = userMap.containsKey(reservation.getUserId())
                    ? userMap.get(reservation.getUserId()).getName()
                    : "User-" + reservation.getUserId();
            userNames.add(userName);
        }
        return userNames;
    }

    private String escapeCsv(Object value) {
        String text = value == null ? "" : String.valueOf(value);
        String escaped = text.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return '"' + escaped + '"';
        }
        return escaped;
    }
}
