package com.micro.userserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.userserve.sys.entity.UserRole;
import com.micro.userserve.sys.service.IUserRoleService;
import com.micro.userserve.sys.mapper.UserRoleMapper;
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
//@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

//    @GrpcClient("user-serve")
//    UserServiceGrpc.UserServiceBlockingStub synchronousClient;

    public void insert(UserRole userRole){
        userRoleMapper.insert(userRole);
    }

    public void delete(LambdaQueryWrapper<UserRole> wrapper){
        userRoleMapper.delete(wrapper);
    }

    public List<UserRole> selectList(LambdaQueryWrapper<UserRole> wrapper){
        return userRoleMapper.selectList(wrapper);
    }

//    public GetNameByUidResponse getUserById(Integer id) {
//        GetNameByUidRequest request = GetNameByUidRequest.newBuilder().setUid(id).build();
//        return synchronousClient.getUser(request);
//    }

}
