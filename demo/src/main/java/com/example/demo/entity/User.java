package com.example.demo.entity;


import jakarta.persistence.*; //引入 JPA 的註解，用於定義實體類別和資料表映射
import java.time.LocalDateTime; //引入 LocalDateTime 類別用於表示日期和時間


@Entity //標註這是一個 JPA 實體類別，讓 Spring 知道要將它映射到資料庫中的一個表
@Table(name = "usee") // 指定資料表名稱
public class User {

    @Id // 主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成ID不用你自己手動設定，讓資料庫自動幫你產生
    private Long id;
    private String name;
    private String email;
    private String account;
    private String password;
    private LocalDateTime createTime; //新增 createTime 欄位用於記錄使用者創建時間


    public User(){} //JPA框架必要的無參數建構子  一定要加

    public User(String name,String email,String account,String password,LocalDateTime createTime){  //有參數建構子讓建立物件時可以直接傳入值
        this.name = name;
        this.email = email;
        this.account = account;
        this.password = password;
        this.createTime = createTime;
    }

    //以下是getter和setter方法，讓外部可以訪問和修改私有屬性
    //getter
    public Long getId(){  //取得 id因為欄位是 private，外部不能直接存取：
        return id;
    }

    public String getName(){  //取得 name
        return name;
    }

    public String getEmail(){  //取得 email
        return email;
    }

    public String getAccount(){  //取得 account
        return account;
    }

    public String getPassword(){  //取得 password
        return password;
    }

    public LocalDateTime getCreateTime(){  //取得 createTime
        return createTime;
    }

    //setter  Id不能修改所以不設

    public void setName(String name){  //設定 name
        this.name = name;
    }

    public void setEmail(String email){  //設定 email
        this.email = email;
    }

    public void setAccount(String account){  //設定 account
        this.account = account;
    }

    public void setPassword(String password){  //設定 password
        this.password = password;
    }



    
     
}
