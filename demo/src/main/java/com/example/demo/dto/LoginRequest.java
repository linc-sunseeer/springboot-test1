package com.example.demo.dto;

// 匯入 Lombok，讓這種單純資料傳遞物件不用手寫 getter / setter
import lombok.Getter;
import lombok.Setter;

// 這個 DTO 是登入時前端送進來的資料
@Getter
@Setter
public class LoginRequest {

    // 使用者輸入的信箱
    private String email;

    // 使用者輸入的密碼
    private String password;
}
