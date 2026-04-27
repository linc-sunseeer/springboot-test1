package com.example.demo.service;

<<<<<<< Updated upstream

import com.example.demo.entity.Menu; //把自己定義的menu導入
import com.example.demo.repository.MenuRepository; //把自己定義的repository導入
import org.springframework.beans.factory.annotation.Autowired; //導入 Autowired 
import org.springframework.stereotype.Service; //導入 Service
=======
// 匯入 LambdaQueryWrapper，這個是 MyBatis-Plus 查詢條件工具
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// 匯入 Menu / Reservation 實體與 mapper
import com.example.demo.entity.Menu;
import com.example.demo.entity.Reservation;
import com.example.demo.mapper.MenuMapper;
import com.example.demo.mapper.ReservationMapper;
// 匯入 NonNull，提醒這個參數不應該是 null
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
>>>>>>> Stashed changes

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// 這個 Service 專門處理菜單相關邏輯
@Service
public class MenuService {

    private final MenuMapper menuMapper;
    private final ReservationMapper reservationMapper;
    private final Path menuImageUploadDir;

    public MenuService(MenuMapper menuMapper,
                       ReservationMapper reservationMapper,
                       @Value("${app.menu-image.upload-dir:uploads/menus}") String menuImageUploadDir) {
        this.menuMapper = menuMapper;
        this.reservationMapper = reservationMapper;
        this.menuImageUploadDir = Paths.get(menuImageUploadDir).toAbsolutePath().normalize();
    }

<<<<<<< Updated upstream
    //查看單一菜單
    public Menu getMenuById(@NonNull Long id){  //根據 ID 查找菜單的方法，參數是菜單 ID
        return menuRepository.findById(id) //使用 JpaRepository 提供的 findById 方法來查找菜單
                .orElseThrow(() -> new IllegalArgumentException("メニューが見つかりません")); //如果找不到菜單，拋出異常
    }


    //新增菜單
    public Menu createMenu(@NonNull Menu menu){  //建立新菜單的方法，參數是一個 Menu 物件
        return menuRepository.save(menu); //使用 JpaRepository 提供的 save 方法來保存菜單到資料庫
    }

    //刪除菜單
    public void deleteMenu(@NonNull Long id){ //刪除菜單的方法，參數是菜單 ID
        if (!menuRepository.existsById(id)) { //使用 JpaRepository 提供的 existsById 方法來檢查菜單是否存在
            throw new IllegalArgumentException("メニューが見つかりません"); //如果菜單不存在，拋出異常
        }
        menuRepository.deleteById(id); //使用 JpaRepository 提供的 deleteById 方法來刪除菜單
    }

    //更新菜單
    public Menu updateMenu(@NonNull Long id, @NonNull Menu updatedMenu){ //更新菜單的方法，參數是菜單 ID 和更新後的 Menu 物件
        Menu existingMenu = menuRepository.findById(id) //使用 JpaRepository 提供的 findById 方法來查找菜單
                .orElseThrow(() -> new IllegalArgumentException("メニューが見つかりません")); //如果找不到菜單，拋出異常

        existingMenu.setName(updatedMenu.getName()); //更新菜單名稱
        existingMenu.setPrice(updatedMenu.getPrice()); //更新菜單價格
        existingMenu.setDescription(updatedMenu.getDescription()); //更新菜單描述

        return menuRepository.save(existingMenu); //使用 JpaRepository 提供的 save 方法來保存更新後的菜單到資料庫
    }
    
=======
    // 查全部菜單
    public List<Menu> getAllMenus() {
        return menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getDeleted, false));
    }

    public Menu getMenuById(Long menuId) {
        if (menuId == null) {
            return null;
        }
        return menuMapper.selectById(menuId);
    }

    // 查某一天可以顯示的菜單
    public List<Menu> getMenusByDate(LocalDate date) {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getAvailableDate, date)
                .eq(Menu::getDeleted, false));

        Map<Long, Integer> reservationCountByMenuId = reservationMapper.selectList(
                        new LambdaQueryWrapper<Reservation>()
                                .eq(Reservation::getReservationDate, date)
                                .ne(Reservation::getOrderStatus, "CANCELLED"))
                .stream()
                .collect(LinkedHashMap::new,
                        (counts, reservation) -> counts.merge(
                                reservation.getMenuId(),
                                reservation.getQuantity() == null ? 1 : reservation.getQuantity(),
                                Integer::sum),
                        Map::putAll);

        menus.forEach(menu -> menu.setReservationCount(reservationCountByMenuId.getOrDefault(menu.getId(), 0)));
        return menus;
    }

    public List<Menu> getMenusByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Menu> menus = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                .between(Menu::getAvailableDate, startDate, endDate)
                .eq(Menu::getDeleted, false)
                .orderByAsc(Menu::getAvailableDate)
                .orderByAsc(Menu::getId));

        Map<Long, Integer> reservationCountByMenuId = reservationMapper.selectList(
                        new LambdaQueryWrapper<Reservation>()
                                .between(Reservation::getReservationDate, startDate, endDate)
                                .ne(Reservation::getOrderStatus, "CANCELLED"))
                .stream()
                .collect(LinkedHashMap::new,
                        (counts, reservation) -> counts.merge(
                                reservation.getMenuId(),
                                reservation.getQuantity() == null ? 1 : reservation.getQuantity(),
                                Integer::sum),
                        Map::putAll);

        menus.forEach(menu -> menu.setReservationCount(reservationCountByMenuId.getOrDefault(menu.getId(), 0)));
        return menus;
    }

    // 新增菜單
    public Menu createMenu(@NonNull Menu menu) {
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());

        if (menu.getDeleted() == null) {
            menu.setDeleted(false);
        }

        menuMapper.insert(menu);
        return menu;
    }

    // 修改菜單
    public Menu updateMenu(Long menuId, @NonNull Menu menu) {
        Menu existing = menuMapper.selectById(menuId);
        if (existing == null) {
            throw new IllegalArgumentException("メニューが存在しません。");
        }

        existing.setName(menu.getName());
        existing.setPrice(menu.getPrice());
        existing.setDescription(menu.getDescription());
        existing.setImageUrl(menu.getImageUrl());
        existing.setAvailableDate(menu.getAvailableDate());
        existing.setUpdatedAt(LocalDateTime.now());
        menuMapper.updateById(existing);
        return existing;
    }

    // 邏輯刪除菜單
    public void deleteMenu(Long menuId) {
        Menu existing = menuMapper.selectById(menuId);
        if (existing == null) {
            throw new IllegalArgumentException("メニューが存在しません。");
        }

        existing.setDeleted(true);
        existing.setUpdatedAt(LocalDateTime.now());
        menuMapper.updateById(existing);
    }

    public String storeMenuImage(@NonNull MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("画像ファイルを選択してください。");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("画像ファイルのみアップロードできます。");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "menu-image" : file.getOriginalFilename());
        String extension = "";
        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex >= 0) {
            extension = originalFilename.substring(extensionIndex);
        }

        String storedFilename = UUID.randomUUID() + extension;
        Path targetFile = menuImageUploadDir.resolve(storedFilename).normalize();

        try {
            Files.createDirectories(menuImageUploadDir);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("画像の保存に失敗しました。", exception);
        }

        return "/uploads/menus/" + storedFilename;
    }
>>>>>>> Stashed changes
}
