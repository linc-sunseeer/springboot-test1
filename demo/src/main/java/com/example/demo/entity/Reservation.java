package com.example.demo.entity;

// 匯入 MyBatis-Plus 的主鍵與資料表註解
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
// 匯入 Lombok，幫我們省掉大量樣板 getter / setter
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 匯入日期型別：LocalDate 存純日期，LocalDateTime 存日期+時間
import java.time.LocalDate;
import java.time.LocalDateTime;

// 這個類別是 reservations 資料表對應的 Java 物件
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("reservations")
public class Reservation {

    // 預約主鍵 id
    @TableId(type = IdType.AUTO)
    private Long id;

    // 預約是誰下的，對應 users.id
    private Long userId;

    // 預約的是哪個菜單，對應 menus.id
    private Long menuId;

    // 預約的是哪一天
    private LocalDate reservationDate;

    // 同一種菜單預約幾份
    private Integer quantity;

    // 付款狀態，例如 UNPAID / PAID
    private String paymentStatus;

    // 訂單狀態，例如 RESERVED / CONFIRMED / CANCELLED
    private String orderStatus;

    // 付款方式，例如 CARD / PAYPAY / CASH
    private String paymentMethod;

    // 實際建立預約的時間
    private LocalDateTime reservedAt;

    // 使用者按下支払済み的時間
    private LocalDateTime paymentCheckedAt;

    // 退款原因备注
    private String refundReason;

    // 建立時間
    private LocalDateTime createdAt;

    // 更新時間
    private LocalDateTime updatedAt;
}
