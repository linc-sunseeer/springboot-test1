package com.example.demo.mapper;

// 匯入 BaseMapper，讓 MyBatis-Plus 幫忙提供基本操作
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 匯入 Admin 實體，對應 admins 表
import com.example.demo.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

// 這個 mapper 是操作管理者資料的
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
