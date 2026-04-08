package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "menu") // 指定資料表名稱
public class Menu {

    @Id  // 主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自動生成ID不用你自己手動設定，讓資料庫自動幫你產生
    private Long id;

    private String name;

    private int price;

    private String description; //菜單詳細說明


    public Menu(){}  //JPA框架必要的無參數建構子  一定要加


    public Menu(String name,int price,String description){  //有參數建構子讓建立物件時可以直接傳入值
        this.name = name;
        this.price = price;
        this.description = description;
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



    
    
}
