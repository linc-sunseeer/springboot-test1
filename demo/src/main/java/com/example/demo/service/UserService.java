package com.example.demo.service;

import com.example.demo.entity.User; //把自己定義的user導入
import com.example.demo.repository.UserRepository; //把自己定義的repository導入
import org.springframework.stereotype.Service; //導入 Service

import java.util.List;


@Service //標註這是一個 Service 類別，讓 Spring 知道要管理它
public class UserService {

    private final UserRepository userRepository; //定義 UserRepository 屬性，這是我們用來操作資料庫的接口

    public UserService(UserRepository userRepository){ //使用建構子注入 UserRepository 的實例，讓我們可以使用它來操作資料庫
        this.userRepository = userRepository;
    }

    //查詢全部使用者
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    //新增使用者
    public User createUser(User user){  //建立新使用者的方法，參數是一個 User 物件
        if (user == null) {
            throw new IllegalArgumentException("空欄できません");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("このメールアドレスは既に使用されています");
        }

        return userRepository.save(user); //使用 JpaRepository 提供的 save 方法來保存使用者到資料庫
    }

    //登入
    public User login(String email, String password){  //登入方法，參數是 email 和 password
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("メールまたはパスワードが正しくありません");
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("パスワードが正しくありません");
        }

        return user;
    }
}