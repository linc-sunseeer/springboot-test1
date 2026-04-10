package com.example.demo.controller;

import com.example.demo.entity.Menu; //把自己定義的menu導入
import com.example.demo.service.MenuService; //把自己定義的service導入
import org.springframework.beans.factory.annotation.Autowired; //導入 Autowired
import org.springframework.web.bind.annotation.*; //導入 RestController、RequestMapping、GetMapping、PostMapping

import java.util.List; //導入 List

@RestController //標註這是一個 REST 控制器，讓 Spring 知道要管理它
@RequestMapping("/menus") //指定這個控制器處理 /menus 路徑的請求

public class MenuController {
    @Autowired //自動注入 MenuService 的實例，讓我們可以使用它來處理業務邏輯
    private MenuService menuService;

    //查看全部菜單
    @GetMapping //處理 GET 請求，對應 /menus 路徑
    public List<Menu> getAllMenus(){  //取得所有菜單的方法，返回一個菜單列表
        return menuService.getAllMenus(); //調用 MenuService 的 getAllMenus 方法來獲取所有菜單
    }

    //查看單一菜單
    @GetMapping("/{id}") //處理 GET 請求，對應 /menus/{id} 路徑，{id} 是一個路徑變量
    public Menu getMenuById(@PathVariable Long id){  //根據 ID 查找菜單的方法，參數是從路徑中獲取的菜單 ID
        return menuService.getMenuById(id); //調用 MenuService 的 getMenuById 方法來獲取指定 ID 的菜單
    }

    //新增菜單
    @PostMapping //處理 POST 請求，對應 /menus 路徑
    public Menu createMenu(@RequestBody Menu menu){  //建立新菜單的方法，參數是一個從請求體中獲取的 Menu 物件
        return menuService.createMenu(menu); //調用 MenuService 的 createMenu 方法來保存新菜單到資料庫
    }
    

    //刪除菜單
    @DeleteMapping("/{id}") //處理 DELETE 請求，對應 /menus/{id} 路徑，{id} 是一個路徑變量
    public void deleteMenu(@PathVariable Long id){  //刪除菜單的方法，參數是從路徑中獲取的菜單 ID
        menuService.deleteMenu(id); //調用 MenuService 的 deleteMenu 方法來刪除指定 ID 的菜單
    }

    //更新菜單
    @PutMapping("/{id}")//處理 PUT 請求，對應 /menus/{id} 路徑，{id} 是一個路徑變量
    public Menu updateMenu(@PathVariable Long id, @RequestBody Menu updatedMenu){  //更新菜單的方法，參數是從路徑中獲取的菜單 ID 和從請求體中獲取的更新後的 Menu 物件
        return menuService.updateMenu(id, updatedMenu); //調用 MenuService 的 updateMenu 方法來更新指定 ID 的菜單
    }
}
