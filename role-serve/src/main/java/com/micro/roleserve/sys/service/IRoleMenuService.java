

package com.micro.roleserve.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.roleserve.sys.entity.RoleMenu;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
public interface IRoleMenuService extends IService<RoleMenu> {
    public void insert(RoleMenu roleMenu);
    public List<Integer> getMenuIdListByRoleId(Integer id);
    public void delete(LambdaQueryWrapper<RoleMenu> wrapper);
}
