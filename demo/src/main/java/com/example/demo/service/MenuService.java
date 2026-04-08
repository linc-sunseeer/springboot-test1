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


    //新增菜單
    public Menu createMenu(@NonNull Menu menu){  //建立新菜單的方法，參數是一個 Menu 物件
        return menuRepository.save(menu); //使用 JpaRepository 提供的 save 方法來保存菜單到資料庫
    }
    
}
