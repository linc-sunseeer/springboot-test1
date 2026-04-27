package com.example.demo.dto;

// 匯入 Lombok，幫 DTO 自動產生 getter / setter
import lombok.Getter;
import lombok.Setter;

// 這個 DTO 是使用者註冊時前端送進來的資料
@Getter
@Setter
public class RegisterRequest {

    // 使用者姓名
    private String name;

    // 使用者信箱
    private String email;

    // 使用者輸入的原始密碼
    // 真正存進資料庫之前，Service 會先做 hash
    private String password;
}
