package com.micro.userserve.sys.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.userserve.sys.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    public List<Menu> getMenuListByUserId(@Param("userId") Integer userId, @Param("parentId")Integer parentId);
}
