package com.example.demo.service;


import com.example.demo.entity.Menu; //把自己定義的menu導入
import com.example.demo.repository.MenuRepository; //把自己定義的repository導入
import org.springframework.beans.factory.annotation.Autowired; //導入 Autowired 
import org.springframework.stereotype.Service; //導入 Service

import java.util.List; //導入 List
import lombok.NonNull; //導入 Lombok 的 NonNull 注解

@Service //標註這是一個 Service 類別，讓 Spring 知道要管理它
public class MenuService {

    @Autowired //自動注入 MenuRepository 的實例，讓我們可以使用它來操作資料庫
    private MenuRepository menuRepository;


    //查看全部菜單
    public List<Menu> getAllMenus(){  //取得所有菜單的方法
        return menuRepository.findAll(); //使用 JpaRepository 提供的 findAll 方法來獲取所有菜單
    }

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
    
}
