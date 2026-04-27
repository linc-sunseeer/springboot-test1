package com.example.demo.mapper;

// 匯入 BaseMapper，讓基本 CRUD 不用手刻
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 匯入 Reservation 實體，代表操作的是 reservations 表
import com.example.demo.entity.Reservation;
import org.apache.ibatis.annotations.Mapper;

// 這個 mapper 是操作預約資料的
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}
