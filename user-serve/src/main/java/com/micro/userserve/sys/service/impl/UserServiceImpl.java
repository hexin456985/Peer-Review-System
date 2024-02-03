package com.micro.userserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.roleserve.proto.GetRoleIdByNameRequest;
import com.micro.roleserve.proto.GetRoleIdByNameResponse;
import com.micro.roleserve.proto.RoleServiceGrpc;
import com.micro.common.utils.JwtUtil;
import com.micro.userserve.sys.entity.User;
import com.micro.userserve.sys.entity.UserRole;
import com.micro.userserve.sys.mapper.UserMapper;
import com.micro.userserve.sys.service.IMenuService;
import com.micro.userserve.sys.service.IUserRoleService;
import com.micro.userserve.sys.service.IUserService;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-09-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private JwtUtil jwtUtil;

//    @Autowired
//    private RoleMapper roleMapper;

//    @Autowired
//    private MenuServiceImpl menuService;

    @GrpcClient("role-serve")
    RoleServiceGrpc.RoleServiceBlockingStub synchronousRoleClient;

//    @GrpcClient("menu-serve")
//    MenuServiceGrpc.MenuServiceBlockingStub synchronousMenuClient;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IUserRoleService userRoleService;


    @Override
    public Map<String, Object> login(User user) {
        // 根据用户名查询
//        log.debug("aaaaaaaaaauser:"+user.getName());
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getName,user.getName());
        User loginUser = userMapper.selectOne(wrapper);
        // 结果不为空，并且密码和传入密码匹配，则生成token，并将用户信息存入redis
        //loginUser != null && passwordEncoder.matches(user.getPassword(),loginUser.getPassword())
        if(loginUser != null){
            loginUser.setPassword(null);

            // 创建jwt
            String jwt = jwtUtil.createToken(loginUser);

            // 返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", jwt);
            return data;
        }
        return null;
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        // 根据token获取用户信息
        User loginUser = null;
        log.debug(token);
        try {
            loginUser = jwtUtil.parseToken(token, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(loginUser != null){
            Map<String, Object> data = new HashMap<>();
            data.put("name",loginUser.getName());

//            data.put("avatar", loginUser.getAvatar());

            // 角色列表
            List<String> roleList = userMapper.getRoleNameByUserId(loginUser.getUid());
            data.put("roles", roleList);

            // 权限列表
//            MenuRequest request = MenuRequest.newBuilder().setUserId(loginUser.getUid()).build();
//            MenuResponse response = synchronousMenuClient.getMenuListByUserId(request);

            data.put("menuList", menuService.getMenuListByUserId(loginUser.getUid()));

            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
    }

    @Override
    public void addUser(User user) {
        // 写入用户表
        userMapper.insert(user);
        // 写入用户角色表
        List<Integer> roleIdList = user.getRoleUidList();
        if(roleIdList != null) {
            for (Integer roleId: roleIdList) {
                //userRoleMapper.insert(new UserRole(null, user.getUid(), roleId));
                userRoleService.insert(new UserRole(null, user.getUid(), roleId));

            }
        }
    }

    @Override
    @Transactional
    public void register(User user) {
        // 写入用户表
        userMapper.insert(user);
        //Integer roleId = roleMapper.getRoleIdByName("normal");
        GetRoleIdByNameRequest request = GetRoleIdByNameRequest.newBuilder()
                .setRoleName("normal")
                .build();
        GetRoleIdByNameResponse response = synchronousRoleClient.getRoleIdByName(request);
        Integer roleId = response.getRoleId();
        // 写入用户角色表
        userRoleService.insert(new UserRole(null, user.getUid(), roleId));
    }

    @Override
    public User getUserById(Integer id) {
        User user = userMapper.selectById(id);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        //List<UserRole> userRoleList = userRoleMapper.selectList(wrapper);
        List<UserRole> userRoleList = userRoleService.selectList(wrapper);


        List<Integer> roleIdList = userRoleList.stream()
                .map(userRole -> {return userRole.getRoleId();})
                .collect(Collectors.toList());
        user.setRoleUidList(roleIdList);

        return user;
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        // 更新用户表
        userMapper.updateById(user);

        // 清楚原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, user.getUid());
        //userRoleMapper.delete(wrapper);
        userRoleService.delete(wrapper);

        // 设置新的角色
        List<Integer> roleIdList = user.getRoleUidList();
        if(roleIdList != null) {
            for (Integer roleId: roleIdList) {
                //userRoleMapper.insert(new UserRole(null, user.getUid(), roleId));
                userRoleService.insert(new UserRole(null, user.getUid(), roleId));
            }
        }
    }

    @Override
    public void deleteUserById(Integer id) {
        // 清除原有角色
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, id);
        //userRoleMapper.delete(wrapper);
        userRoleService.delete(wrapper);
        // 清除用户表
        userMapper.deleteById(id);
    }

    @Override
    public List<String> getNameByUidList(List<Integer> uidList) {
        if (uidList.isEmpty()) {
            return Collections.emptyList();
        }

        QueryWrapper<User> queryWrapper = Wrappers.query();
        queryWrapper.in("UID", uidList); // 使用in条件查询

        List<User> userList = list(queryWrapper);

        List<String> nameList = userList.stream()
                .map(User::getName)
                .collect(Collectors.toList());

        return nameList;
    }

//    @Override
//    public String getNameByUid(Integer uid) {
//        User user = userMapper.selectById(uid);
//        String name = user.getName();
//        return name;
//    }



}








