package com.example.demo.mapper;

// 匯入 BaseMapper，省掉自己寫一堆基本 CRUD SQL
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
// 匯入 Menu 實體，表示這個 mapper 對應 menus 表
import com.example.demo.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

// 這個 mapper 是拿來操作菜單資料的
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
}
