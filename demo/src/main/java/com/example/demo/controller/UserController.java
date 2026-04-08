package com.example.demo.controller;

import com.example.demo.entity.User; //把自己定義的user導入
import com.example.demo.service.UserService; //把自己定義的service導入
import org.springframework.beans.factory.annotation.Autowired; //導入 Autowired
import org.springframework.web.bind.annotation.*; //導入 RestController、RequestMapping、GetMapping、PostMapping

import java.util.List; //導入 List

@RestController //標註這是一個 REST 控制器，讓 Spring 知道要管理它
@RequestMapping("/users") //指定這個控制器處理 /users 路徑的請求


public class UserController {

    @Autowired //自動注入 UserService 的實例，讓我們可以使用它來處理業務邏輯
    private UserService userService;

    //查看全部用戶
    @GetMapping //處理 GET 請求，對應 /users 路徑
    public List<User> getAllUsers(){  //取得所有用戶的方法，返回一個用戶列表
        return userService.getAllUsers(); //調用 UserService 來獲取用戶列表
    }
    
    //新增用戶
    @PostMapping //處理 POST 請求，對應 /users 路徑
    public String createUser(@RequestBody User user){  //建立新用戶的方法，參數是一個從請求體中獲取的 User 物件
        return "ユーザーを作成しました"; //這裡暫時返回一個字符串，實際上應該調用 UserService 來保存新用戶到資料庫
    }

    //刪除用戶
    @DeleteMapping("/{id}") //處理 DELETE 請求，對應 /users/{id} 路徑，{id} 是一個路徑變量
    public String deleteUser(@PathVariable Long id){  //刪除用戶的方法，參數是從路徑中獲取的用戶 ID
        return "ユーザーを削除しました"; //這裡暫時返回一個字符串，實際上應該調用 UserService 來刪除指定 ID 的用戶
    }

    //登入
    @PostMapping("/login")
    public String login(@RequestBody User user){
        return "ユーザーでログインしました";
    }
}
