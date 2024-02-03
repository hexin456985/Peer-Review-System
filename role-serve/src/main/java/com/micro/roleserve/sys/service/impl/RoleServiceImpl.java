package com.micro.roleserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.roleserve.sys.entity.Role;
import com.micro.roleserve.sys.entity.RoleMenu;
import com.micro.roleserve.sys.mapper.RoleMapper;
import com.micro.roleserve.sys.service.IRoleMenuService;
import com.micro.roleserve.sys.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    private IRoleMenuService roleMenuService;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional
    public void addRole(Role role) {
        // 写入角色表
        roleMapper.insert(role);
        // 写入角色与菜单关系表
        if (role.getMenuIdList() != null) {
            for (Integer menuId : role.getMenuIdList()) {
                roleMenuService.insert(new RoleMenu(null, role.getRoleId(), menuId));
                //roleMenuMapper.insert(new RoleMenu(null, role.getRoleId(), menuId));
            }
        }
    }

    @Override
    public Role getRoleById(Integer id) {
        Role role = roleMapper.selectById(id);
        //List<Integer> menuIdList = roleMenuMapper.getMenuIdListByRoleId(id);
        List<Integer> menuIdList = roleMenuService.getMenuIdListByRoleId(id);

        role.setMenuIdList(menuIdList);
        return role;
    }

    @Override
    @Transactional
    public void updateRole(Role role) {
        // 修改角色表
        roleMapper.updateById(role);
        // 删除原有权限
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, role.getRoleId());

        //roleMenuMapper.delete(wrapper);
        roleMenuService.delete(wrapper);
        // 新增权限
        if (role.getMenuIdList() != null) {
            for (Integer menuId : role.getMenuIdList()) {
                //roleMenuMapper.insert(new RoleMenu(null, role.getRoleId(), menuId));
                roleMenuService.insert(new RoleMenu(null, role.getRoleId(), menuId));
            }
        }
    }

    @Override
    @Transactional
    public void deleteRoleById(Integer id) {
        roleMapper.deleteById(id);
        // 删除权限
        LambdaQueryWrapper<RoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RoleMenu::getRoleId, id);
        roleMenuService.delete(wrapper);
        //roleMenuMapper.delete(wrapper);
    }

    public Integer getRoleIdByName(String string){
        return roleMapper.getRoleIdByName(string);
    }
}
