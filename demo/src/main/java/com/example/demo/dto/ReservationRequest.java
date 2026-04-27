package com.example.demo.dto;

// 匯入 Lombok，讓預約請求 DTO 更精簡
import lombok.Getter;
import lombok.Setter;

// 匯入 LocalDate，因為預約日期只需要日期，不需要時間
import java.time.LocalDate;

// 這個 DTO 是使用者按下預約時送給後端的資料
@Getter
@Setter
public class ReservationRequest {

    // 要預約哪一個菜單
    private Long menuId;

    // 預約哪一天
    private LocalDate reservationDate;

    // 同一個菜單要幾份
    private Integer quantity;

    // demo 付款方式（CARD / PAYPAY / CASH）
    private String paymentMethod;
}
