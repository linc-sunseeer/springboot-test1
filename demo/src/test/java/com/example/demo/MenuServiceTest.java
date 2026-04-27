package com.example.demo;

import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.mapper.MenuMapper;
import com.example.demo.mapper.ReservationMapper;
import com.example.demo.service.MenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuMapper menuMapper;

    @Mock
    private ReservationMapper reservationMapper;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        menuService = new MenuService(menuMapper, reservationMapper, "uploads/menus");
    }

    @Test
    void getAllMenusExcludesLogicallyDeletedMenus() {
        Menu active = Menu.builder()
                .id(1L)
                .name("唐揚げ弁当")
                .price(new BigDecimal("550"))
                .availableDate(LocalDate.of(2026, 4, 20))
                .deleted(false)
                .build();

        when(menuMapper.selectList(any())).thenReturn(List.of(active));

        List<Menu> menus = menuService.getAllMenus();

        assertEquals(1, menus.size());
        assertEquals("唐揚げ弁当", menus.get(0).getName());
    }

    @Test
    void deleteMenuMarksMenuAsDeleted() {
        Menu existing = Menu.builder()
                .id(2L)
                .name("鮭の塩焼き弁当")
                .price(new BigDecimal("680"))
                .availableDate(LocalDate.of(2026, 4, 20))
                .deleted(false)
                .build();

        when(menuMapper.selectById(2L)).thenReturn(existing);

        menuService.deleteMenu(2L);

        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(menuMapper).updateById(menuCaptor.capture());
        assertFalse(Boolean.FALSE.equals(menuCaptor.getValue().getDeleted()));
    }

    @Test
    void getMenusByDateIncludesActiveReservationCountForEachMenu() {
        LocalDate targetDate = LocalDate.of(2026, 4, 20);
        Menu first = Menu.builder()
                .id(1L)
                .name("唐揚げ弁当")
                .description("にんにく醤油で漬け込んだ人気メニュー")
                .price(new BigDecimal("550"))
                .availableDate(targetDate)
                .deleted(false)
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .updatedAt(LocalDateTime.of(2026, 4, 1, 9, 0))
                .build();
        Menu second = Menu.builder()
                .id(2L)
                .name("鮭弁当")
                .description("塩鮭と副菜の定番弁当")
                .price(new BigDecimal("620"))
                .availableDate(targetDate)
                .deleted(false)
                .createdAt(LocalDateTime.of(2026, 4, 1, 9, 30))
                .updatedAt(LocalDateTime.of(2026, 4, 1, 9, 30))
                .build();

        when(menuMapper.selectList(any())).thenReturn(List.of(first, second));
        when(reservationMapper.selectList(any())).thenReturn(List.of());

        List<Menu> menus = menuService.getMenusByDate(targetDate);

        assertEquals(2, menus.size());
        assertEquals(0, menus.get(0).getReservationCount());
        assertEquals(0, menus.get(1).getReservationCount());
    }

    @Test
    void getMenusByDateRangeIncludesMenusAcrossMultipleDaysWithReservationCounts() {
        LocalDate startDate = LocalDate.of(2026, 4, 20);
        LocalDate endDate = LocalDate.of(2026, 4, 24);

        Menu today = Menu.builder()
                .id(1L)
                .name("唐揚げ弁当")
                .price(new BigDecimal("550"))
                .availableDate(startDate)
                .deleted(false)
                .build();
        Menu weekly = Menu.builder()
                .id(2L)
                .name("鮭弁当")
                .price(new BigDecimal("620"))
                .availableDate(startDate.plusDays(2))
                .deleted(false)
                .build();

        Reservation todayReservation = Reservation.builder()
                .menuId(1L)
                .reservationDate(startDate)
                .quantity(2)
                .orderStatus("RESERVED")
                .build();
        Reservation weeklyReservation = Reservation.builder()
                .menuId(2L)
                .reservationDate(startDate.plusDays(2))
                .quantity(3)
                .orderStatus("RESERVED")
                .build();

        when(menuMapper.selectList(any())).thenReturn(List.of(today, weekly));
        when(reservationMapper.selectList(any())).thenReturn(List.of(todayReservation, weeklyReservation));

        List<Menu> menus = menuService.getMenusByDateRange(startDate, endDate);

        assertEquals(2, menus.size());
        assertEquals(2, menus.get(0).getReservationCount());
        assertEquals(3, menus.get(1).getReservationCount());
    }
}
