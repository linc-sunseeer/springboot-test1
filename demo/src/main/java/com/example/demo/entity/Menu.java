package com.example.demo.entity;

// 匯入 IdType，主鍵要用資料庫自動遞增時會用到
import com.baomidou.mybatisplus.annotation.IdType;
// 匯入 TableId，標記主鍵欄位
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
// 匯入 TableName，指定這個類別對應 menus 這張表
import com.baomidou.mybatisplus.annotation.TableName;
// 匯入 Lombok，幫我們把重複的 getter / setter / constructor 簡化掉
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 匯入 BigDecimal，價格這種資料用它比 int / double 穩定很多
import java.math.BigDecimal;
// 匯入 LocalDate，這個只存日期，不含時間
import java.time.LocalDate;
// 匯入 LocalDateTime，建立時間和更新時間會用到
import java.time.LocalDateTime;

// 這個類別就是 menus 資料表對應的 Java 物件
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("menus")
public class Menu {

    // 菜單主鍵 id
    @TableId(type = IdType.AUTO)
    private Long id;

    // 菜單名稱
    private String name;

    // 價格，內部管理用
    private BigDecimal price;

    // 熱量（kcal）
    private Integer calorie;

    // 過敏原（逗號分隔）
    private String allergens;

    // 菜單說明
    private String description;

    // 圖片網址
    // 資料庫是 image_url，Java 是 imageUrl，這種底線轉駝峰可以自動對應
    private String imageUrl;

    // 當日預約人數（畫面表示用、DB 欄位なし）
    @TableField(exist = false)
    private Integer reservationCount;

    // 提供日期
    // DB 欄位是 available_date，Java 用 availableDate
    private LocalDate availableDate;

    // 是否已刪除，這邊是邏輯刪除概念
    // DB 欄位是 is_deleted
    @TableField("is_deleted")
    private Boolean deleted;

    // 建立時間
    private LocalDateTime createdAt;

    // 更新時間
    private LocalDateTime updatedAt;
}
