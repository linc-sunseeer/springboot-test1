package com.example.demo;

import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.security.JwtService;
import com.example.demo.service.AdminAggregateService;
import com.example.demo.service.MenuService;
import com.example.demo.service.ReservationService;
import com.example.demo.service.SystemSettingService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private MenuService menuService;

    @MockBean
    private AdminAggregateService adminAggregateService;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private SystemSettingService systemSettingService;

    @MockBean
    private UserService userService;

    private String adminToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtService.generateAdminToken(1L, "AdminUser");
    }

    @Test
    void adminCanCreateMenu() throws Exception {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("唐揚げ弁当");

        when(menuService.createMenu(any(Menu.class))).thenReturn(menu);

        mockMvc.perform(post("/api/admin/menus")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"唐揚げ弁当\",\"price\":550,\"availableDate\":\"2026-04-15\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("唐揚げ弁当"));
    }

    @Test
    void adminCanUpdateMenu() throws Exception {
        Menu menu = new Menu();
        menu.setId(1L);
        menu.setName("更新後弁当");

        when(menuService.updateMenu(eq(1L), any(Menu.class))).thenReturn(menu);

        mockMvc.perform(put("/api/admin/menus/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"更新後弁当\",\"price\":600,\"availableDate\":\"2026-04-15\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("更新後弁当"));
    }

    @Test
    void adminCanDeleteMenu() throws Exception {
        mockMvc.perform(delete("/api/admin/menus/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("deleted"));
    }

    @Test
    void adminCanGetMenuReservationSummary() throws Exception {
        when(reservationService.countActiveReservationsForMenu(1L)).thenReturn(2L);

        mockMvc.perform(get("/api/admin/menus/1/reservation-summary")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menuId").value(1))
                .andExpect(jsonPath("$.data.activeReservationCount").value(2))
                .andExpect(jsonPath("$.data.hasActiveReservations").value(true));
    }

    @Test
    void adminCanUploadMenuImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "menu.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image".getBytes());

        when(menuService.storeMenuImage(any())).thenReturn("/uploads/menus/menu.png");

        mockMvc.perform(multipart("/api/admin/menus/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("uploaded"))
                .andExpect(jsonPath("$.data.imageUrl").value("/uploads/menus/menu.png"));
    }

    @Test
    void adminCanUploadLargeMenuImageWithinConfiguredLimit() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large-menu.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[2 * 1024 * 1024]);

        when(menuService.storeMenuImage(any())).thenReturn("/uploads/menus/large-menu.png");

        mockMvc.perform(multipart("/api/admin/menus/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("uploaded"))
                .andExpect(jsonPath("$.data.imageUrl").value("/uploads/menus/large-menu.png"));
    }

    @Test
    void oversizeUploadReturnsDetailedSizeMessage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "too-large.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[2 * 1024 * 1024]);

        when(menuService.storeMenuImage(any())).thenThrow(new MaxUploadSizeExceededException(1024 * 1024));

        mockMvc.perform(multipart("/api/admin/menus/upload")
                .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("画像サイズが上限を超えています。15MB 以下の画像をアップロードしてください。"));
    }

    @Test
    void invalidUploadFormatReturnsDetailedTypeMessage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "menu.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "not-an-image".getBytes());

        when(menuService.storeMenuImage(any())).thenThrow(new IllegalArgumentException("画像ファイルのみアップロードできます。"));

        mockMvc.perform(multipart("/api/admin/menus/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("画像ファイルのみアップロードできます。PNG / JPG / WEBP などの画像を選択してください。"));
    }

    @Test
    void adminCanGetDailyAggregate() throws Exception {
        when(adminAggregateService.getDailyAggregate(LocalDate.of(2026, 4, 15)))
                .thenReturn(Map.of(
                        "date", "2026-04-15",
                        "totalReservations", 3,
                        "paidCount", 2,
                        "unpaidCount", 1,
                        "menus", List.of(Map.of("menuName", "唐揚げ弁当", "reservationCount", 3))));

        mockMvc.perform(get("/api/admin/aggregates/daily")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("date", "2026-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalReservations").value(3))
                .andExpect(jsonPath("$.data.paidCount").value(2));
    }

    @Test
    void adminCanGetMonthlyAggregate() throws Exception {
        when(adminAggregateService.getMonthlyAggregate("2026-04"))
                .thenReturn(Map.of(
                        "yearMonth", "2026-04",
                        "days", List.of(
                                Map.of("date", "2026-04-15", "reservationCount", 3, "paidCount", 2, "unpaidCount", 1, "menus", List.of(Map.of("menuName", "唐揚げ弁当", "reservationCount", 2))),
                                Map.of("date", "2026-04-16", "reservationCount", 1, "paidCount", 0, "unpaidCount", 1, "menus", List.of(Map.of("menuName", "生姜焼き弁当", "reservationCount", 1))))));

        mockMvc.perform(get("/api/admin/aggregates/monthly")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("yearMonth", "2026-04"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.yearMonth").value("2026-04"))
                .andExpect(jsonPath("$.data.days[0].reservationCount").value(3))
                .andExpect(jsonPath("$.data.days[0].paidCount").value(2))
                .andExpect(jsonPath("$.data.days[0].menus[0].menuName").value("唐揚げ弁当"));
    }

    @Test
    void adminCanExportDailyAggregateCsv() throws Exception {
        when(adminAggregateService.exportDailyAggregateCsv(LocalDate.of(2026, 4, 15)))
                .thenReturn("\uFEFFsep=,\r\n日付,メニュー名,予約数,支払済み数,未支払数,支払済みユーザー,未支払ユーザー\r\n2026-04-15,唐揚げ弁当,3,1,2,田中,佐藤 / 鈴木\r\n");

        mockMvc.perform(get("/api/admin/aggregates/daily/export")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("date", "2026-04-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(content().bytes("\uFEFFsep=,\r\n日付,メニュー名,予約数,支払済み数,未支払数,支払済みユーザー,未支払ユーザー\r\n2026-04-15,唐揚げ弁当,3,1,2,田中,佐藤 / 鈴木\r\n".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    }

    @Test
    void adminCanGetDailyReservationUsers() throws Exception {
        when(adminAggregateService.getDailyReservationUsers(LocalDate.of(2026, 4, 15)))
                .thenReturn(Map.of(
                        "date", "2026-04-15",
                        "users", List.of(
                                Map.of("userName", "Demo User", "menuName", "唐揚げ弁当", "paymentStatus", "UNPAID", "orderStatus", "RESERVED"))));

        mockMvc.perform(get("/api/admin/reservations/daily-users")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("date", "2026-04-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.users[0].userName").value("Demo User"))
                .andExpect(jsonPath("$.data.users[0].menuName").value("唐揚げ弁当"))
                .andExpect(jsonPath("$.data.users[0].orderStatus").value("RESERVED"));
    }

    @Test
    void adminCanGetMinReservationThreshold() throws Exception {
        when(systemSettingService.getMinReservationThreshold()).thenReturn(6);

        mockMvc.perform(get("/api/admin/settings/min-threshold")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(6));
    }

    @Test
    void adminCanUpdateMinReservationThreshold() throws Exception {
        mockMvc.perform(put("/api/admin/settings/min-threshold")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"value\":8}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("updated"));
    }

    @Test
    void adminCanGetUsersWithReservationSummary() throws Exception {
        when(adminAggregateService.getAdminUsers())
                .thenReturn(List.of(Map.of(
                        "userId", 1,
                        "name", "Demo User",
                        "email", "user@example.com",
                        "reservationCount", 2,
                        "paidCount", 1,
                        "latestOrderStatus", "CONFIRMED"
                )));

        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Demo User"))
                .andExpect(jsonPath("$.data[0].reservationCount").value(2));
    }

    @Test
    void adminCanGetUserReservationHistory() throws Exception {
        when(adminAggregateService.getUserReservationHistory(1L))
                .thenReturn(List.of(Map.of(
                        "reservationId", 10,
                        "menuName", "唐揚げ弁当",
                        "paymentStatus", "UNPAID",
                        "orderStatus", "RESERVED",
                        "reservationDate", "2026-04-16"
                )));

        mockMvc.perform(get("/api/admin/users/1/reservations")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].reservationId").value(10))
                .andExpect(jsonPath("$.data[0].orderStatus").value("RESERVED"));
    }

    @Test
    void adminCanUpdateReservationOrderStatus() throws Exception {
        Reservation reservation = Reservation.builder()
                .id(10L)
                .userId(1L)
                .menuId(1L)
                .reservationDate(LocalDate.of(2026, 4, 16))
                .paymentStatus("UNPAID")
                .orderStatus("CONFIRMED")
                .build();

        when(reservationService.updateOrderStatusAsAdmin(10L, "CONFIRMED")).thenReturn(reservation);

        mockMvc.perform(put("/api/admin/reservations/10/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderStatus\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderStatus").value("CONFIRMED"));
    }

    @Test
    void adminCanApproveRequestedCashPayment() throws Exception {
        Reservation reservation = Reservation.builder()
                .id(10L)
                .userId(1L)
                .paymentMethod("CASH")
                .paymentStatus("PAID")
                .build();

        when(reservationService.approvePaymentAsAdmin(10L)).thenReturn(reservation);

        mockMvc.perform(patch("/api/admin/reservations/10/payment")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentStatus").value("PAID"));
    }

}