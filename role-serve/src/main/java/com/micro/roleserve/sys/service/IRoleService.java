
package com.micro.roleserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.roleserve.sys.entity.Role;



/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
public interface IRoleService extends IService<Role> {
    void addRole(Role role);

    Role getRoleById(Integer id);

    void updateRole(Role role);

    void deleteRoleById(Integer id);

    public Integer getRoleIdByName(String string);
}
