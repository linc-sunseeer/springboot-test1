package com.example.demo.entity;

// 匯入 MyBatis-Plus 的主鍵與資料表註解
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
// 匯入 Lombok，讓這個類別不要再手寫一堆樣板 code
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 匯入 LocalDateTime，處理建立時間 / 更新時間
import java.time.LocalDateTime;

// 這個類別是管理者 admins 資料表對應的 Java 物件
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admins")
public class Admin {

    // 管理者主鍵 id
    @TableId(type = IdType.AUTO)
    private Long id;

    // 管理者姓名
    private String name;

    // 管理者信箱
    private String email;

    // 管理者密碼，現在也是存 hash 過後的值
    private String password;

    // 管理者角色，通常會是 ADMIN
    private String role;

    // 建立時間
    private LocalDateTime createdAt;

    // 更新時間
    private LocalDateTime updatedAt;
}
