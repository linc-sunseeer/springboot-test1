package com.example.demo;

import com.example.demo.entity.Menu;
import com.example.demo.security.JwtService;
import com.example.demo.service.MenuService;
import com.example.demo.service.SystemSettingService;
import com.example.demo.mapper.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserMenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private MenuService menuService;

    @MockBean
    private SystemSettingService systemSettingService;

    @MockBean
    private ReservationMapper reservationMapper;

    private String userToken;

    @BeforeEach
    void setUp() {
        userToken = jwtService.generateUserToken(7L, "TestUser");
    }

    @Test
    void todayMenusIncludesCurrentDayReservationCount() throws Exception {
        Menu menu = Menu.builder()
                .id(1L)
                .name("唐揚げ弁当")
                .description("人気メニュー")
                .price(new BigDecimal("550"))
                .availableDate(LocalDate.of(2026, 4, 21))
                .reservationCount(3)
                .deleted(false)
                .build();

        when(menuService.getMenusByDate(LocalDate.now())).thenReturn(List.of(menu));
        when(systemSettingService.getReservationCutoffTime()).thenReturn(LocalTime.of(9, 45));
        when(systemSettingService.getMinReservationThreshold()).thenReturn(6);
        when(reservationMapper.selectCount(any())).thenReturn(3L);

        mockMvc.perform(get("/api/user/menus/today").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.menus[0].reservationCount").value(3))
                .andExpect(jsonPath("$.data.menus[0].description").value("人気メニュー"))
                .andExpect(jsonPath("$.data.minReservationThreshold").value(6));
    }

    @Test
    void homeMenuPayloadIncludesTodayMenuAndWeeklyPreview() throws Exception {
        LocalDate today = LocalDate.now();
        Menu todayMenu = Menu.builder()
                .id(1L)
                .name("唐揚げ弁当")
                .description("本日のメイン")
                .price(new BigDecimal("550"))
                .availableDate(today)
                .reservationCount(2)
                .deleted(false)
                .build();
        Menu weeklyMenu = Menu.builder()
                .id(2L)
                .name("鮭弁当")
                .description("今週のプレビュー")
                .price(new BigDecimal("620"))
                .availableDate(today.plusDays(1))
                .reservationCount(1)
                .deleted(false)
                .build();

        when(menuService.getMenusByDate(today)).thenReturn(List.of(todayMenu));
        when(menuService.getMenusByDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of(todayMenu, weeklyMenu));
        when(systemSettingService.getReservationCutoffTime()).thenReturn(LocalTime.of(9, 45));
        when(systemSettingService.getMinReservationThreshold()).thenReturn(6);
        when(reservationMapper.selectCount(any())).thenReturn(2L);

        mockMvc.perform(get("/api/user/menus/home").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.todayMenu.id").value(1))
                .andExpect(jsonPath("$.data.cutoffTime").value("09:45"))
                .andExpect(jsonPath("$.data.minReservationThreshold").value(6));
    }
}