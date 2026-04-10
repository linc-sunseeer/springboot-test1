package com.example.demo.repository;

import com.example.demo.entity.User; //把自己定義的user導入
import org.springframework.data.jpa.repository.JpaRepository; //導入 JpaRepository，這是一個 Spring Data JPA 提供的接口，提供了基本的 CRUD 操
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> { //定義 UserRepository 介面，繼承 JpaRepository，指定實體類別為 User，主鍵類型為 Long
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email); //定義根據 email 查找使用者的方法，找不到時回傳 Optional.empty
}
