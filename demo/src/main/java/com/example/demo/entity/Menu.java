package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime; //引入 LocalDateTime 類別用於表示日期和時間

@Entity
@Table(name = "menu") // 指定資料表名稱
public class Menu {

    @Id  // 主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自動生成ID不用你自己手動設定，讓資料庫自動幫你產生
    private Long id;

    private String name;

    private int price;

    private String description; //菜單詳細說明

    private String imageUrl; //菜單圖片URL

    private LocalDateTime provideDate; //提供日期


    public Menu(){}  //JPA框架必要的無參數建構子  一定要加


    public Menu(String name,int price,String description,String imageUrl,LocalDateTime provideDate){  //有參數建構子讓建立物件時可以直接傳入值
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.provideDate = provideDate;
    }

    //以下是getter和setter方法，讓外部可以訪問和修改私有屬性
    //getter

    public Long getId(){  //取得 id因為欄位是 private，外部不能直接存取：
        return id;
    }

    public String getName(){  //取得 name
        return name;
    }

    public int getPrice(){  //取得 price
        return price;
    }

    public String getDescription(){  //取得 description
        return description;
    }

    public String getImageUrl(){  //取得 imageUrl
        return imageUrl;
    }

    public LocalDateTime getProvideDate(){  //取得 provideDate
        return provideDate;
    }

    //setter  Id不能修改所以不設

    public void setName(String name){  //設定 name
        this.name = name;
    }

    public void setPrice(int price){  //設定 price
        this.price = price;
    }

    public void setDescription(String description){  //設定 description
        this.description = description;
    }


    public void setImageUrl(String imageUrl){  //設定 imageUrl
        this.imageUrl = imageUrl;
    }

    public void setProvideDate(LocalDateTime provideDate){  //設定 provideDate
        this.provideDate = provideDate;
    }


    
    
}
