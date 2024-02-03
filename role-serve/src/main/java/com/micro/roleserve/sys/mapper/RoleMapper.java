package com.micro.roleserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.roleserve.sys.entity.Role;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-02-07
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    public Integer getRoleIdByName(String name);
}
