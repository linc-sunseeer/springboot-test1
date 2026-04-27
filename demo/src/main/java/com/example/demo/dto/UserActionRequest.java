package com.example.demo.dto;

// 匯入 Lombok，讓這種接前端資料的小物件不要再手寫 getter / setter
import lombok.Getter;
import lombok.Setter;

// 這個 DTO 是我幫你收斂後的共用動作請求物件
// 白話就是：很多小功能其實都只是送幾個字串進來，沒必要每個都拆一個 class
// 所以像改名字、改密碼、忘記密碼申請、重設密碼、更新付款狀態，都先收在這裡
@Getter
@Setter
public class UserActionRequest {

    // 改名字時會用到
    private String name;

    // 改密碼或 reset 密碼時會用到
    private String password;

    // 更新付款狀態時會用到，例如 PAID
    private String paymentStatus;

    // 更新訂單狀態時會用到，例如 CONFIRMED / CANCELLED
    private String orderStatus;

    // reset 密碼時會用到的 token
    private String token;

    // 忘記密碼申請時會用到的 email
    private String email;

    // 飲食偏好：目標熱量（kcal）
    private Integer targetCalorie;

    // 飲食偏好：過敏原設定（逗號分隔）
    private String allergenSettings;

    // 退款原因备注
    private String refundReason;
}
