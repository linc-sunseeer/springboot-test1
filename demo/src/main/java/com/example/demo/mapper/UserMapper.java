package com.example.demo.mapper;

// 匯入 BaseMapper，這樣 MyBatis-Plus 會幫我們提供基本 CRUD 能力
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 匯入 User 實體，代表這個 mapper 是操作 users 表
import com.example.demo.entity.User;
// 匯入 Mapper 註解，讓 Spring / MyBatis 知道這是一個 mapper 介面
import org.apache.ibatis.annotations.Mapper;

// 這個 mapper 專門操作 User 資料表
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
