package com.micro.userserve.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.userserve.sys.entity.UserRole;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
public interface IUserRoleService extends IService<UserRole> {
    public void insert(UserRole userRole);

    public void delete(LambdaQueryWrapper<UserRole> wrapper);

    public List<UserRole> selectList(LambdaQueryWrapper<UserRole> wrapper);

}
