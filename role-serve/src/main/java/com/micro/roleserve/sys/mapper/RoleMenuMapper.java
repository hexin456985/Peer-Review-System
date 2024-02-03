package com.micro.roleserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.roleserve.sys.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-02-07
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    public List<Integer> getMenuIdListByRoleId(Integer roleId);
}