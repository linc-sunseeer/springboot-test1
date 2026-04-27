package com.example.demo.entity;

// 匯入 IdType，這個是在設定主鍵 id 要怎麼自動長出來
import com.baomidou.mybatisplus.annotation.IdType;
// 匯入 TableId，這個是在標記「哪個欄位是主鍵」
import com.baomidou.mybatisplus.annotation.TableId;
// 匯入 TableName，這個是在告訴 MyBatis-Plus 這個類別對應哪張資料表
import com.baomidou.mybatisplus.annotation.TableName;
// 匯入 Builder，這樣之後如果要用 builder 方式建立物件會比較方便
import lombok.Builder;
// 匯入 Getter / Setter，這樣就不用手寫一大堆 getXxx() / setXxx()
import lombok.Getter;
import lombok.Setter;
// 匯入無參數 / 全參數建構子，很多框架或測試都很常用到
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// 匯入 LocalDateTime，這個是拿來存「日期 + 時間」的型別
import java.time.LocalDateTime;

// 這個類別就是 users 資料表對應的 Java 物件
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
public class User {

    // 使用者主鍵 id，AUTO 代表交給資料庫自動遞增
    @TableId(type = IdType.AUTO)
    private Long id;

    // 使用者姓名
    private String name;

    // 使用者信箱，登入時會用到
    private String email;

    // 密碼欄位，現在存的是 hash 過後的內容，不是明文
    private String password;

    // 是否已完成信箱驗證
    private Boolean emailVerified;

    // 使用者角色，例如 USER / ADMIN
    private String role;

    // 每日目標熱量（kcal）
    private Integer targetCalorie;

    // 過敏原設定（逗號分隔）
    private String allergenSettings;

    // 建立時間
    // 資料庫欄位叫 created_at，但因為有開啟底線轉駝峰，所以不用再手寫 @TableField
    private LocalDateTime createdAt;

    // 最後更新時間
    // 資料庫欄位叫 updated_at，一樣靠駝峰轉換自動對應
    private LocalDateTime updatedAt;
}
