package com.micro.roleserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.roleserve.sys.entity.RoleMenu;
import com.micro.roleserve.sys.mapper.RoleMenuMapper;
import com.micro.roleserve.sys.service.IRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Autowired
    RoleMenuMapper roleMenuMapper;

    public void insert(RoleMenu roleMenu){
        roleMenuMapper.insert(roleMenu);
    }
    public List<Integer> getMenuIdListByRoleId(Integer id){
        return roleMenuMapper.getMenuIdListByRoleId(id);
    }
    public void delete(LambdaQueryWrapper<RoleMenu> wrapper){
        roleMenuMapper.delete(wrapper);
    }
}
