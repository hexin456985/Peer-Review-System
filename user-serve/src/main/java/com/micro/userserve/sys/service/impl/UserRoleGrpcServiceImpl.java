package com.micro.userserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.micro.userroleserve.proto.UserRoleInsertResponse;
import com.micro.userroleserve.proto.UserRoleRequest;
import com.micro.userroleserve.proto.UserRoleResponse;
import com.micro.userserve.sys.entity.UserRole;
import com.micro.userroleserve.proto.UserRoleServiceGrpc;
import com.micro.userserve.sys.mapper.UserRoleMapper;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;


@GrpcService
public class UserRoleGrpcServiceImpl extends UserRoleServiceGrpc.UserRoleServiceImplBase {
    @Autowired
    private UserRoleServiceImpl userRoleService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public void checkUserRole(UserRoleRequest request, StreamObserver<UserRoleResponse> responseObserver) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(4); // PC member
        userRole.setUserId(request.getUserId());

        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserRole::getUserId, request.getUserId()).eq(UserRole::getRoleId, 4);
        long count = userRoleService.count(queryWrapper);

        if (count == 0) {
            userRoleService.save(userRole);
        }

        UserRoleResponse response = UserRoleResponse.newBuilder()
                .setUserHasRole(count == 0) // Return true if count is 0
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void insertUserRole(com.micro.userroleserve.proto.UserRoleInsertRequest request,
                               io.grpc.stub.StreamObserver<com.micro.userroleserve.proto.UserRoleInsertResponse> responseObserver) {
        UserRole userRole = new UserRole();
        userRole.setUserId(request.getUserId());
        userRole.setRoleId(request.getRoleId());
        userRoleMapper.insert(userRole);
        UserRoleInsertResponse response = UserRoleInsertResponse.newBuilder().build();
        // 发送响应给客户端
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
