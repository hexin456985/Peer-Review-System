package com.micro.userserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.userserve.sys.entity.User;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2023-09-20
 */
public interface IUserService extends IService<User> {


    Map<String, Object> login(User user);

    Map<String, Object> getUserInfo(String token);

    void logout(String token);

    void addUser(User user);

    void register(User user);

    User getUserById(Integer id);

    void updateUser(User user);

    void deleteUserById(Integer id);

    List<String> getNameByUidList(List<Integer> uidList);

//    public void getNameByUid(UserServiceOuterClass.GetNameByUidRequest request, StreamObserver<UserServiceOuterClass.GetNameByUidResponse> responseObserver);
}
