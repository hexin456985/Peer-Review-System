package com.micro.roleserve.sys.service.impl;

import com.micro.roleserve.proto.GetRoleIdByNameRequest;
import com.micro.roleserve.proto.GetRoleIdByNameResponse;
import com.micro.roleserve.sys.mapper.RoleMapper;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.micro.roleserve.proto.RoleServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RoleGrpcServiceImpl extends RoleServiceGrpc.RoleServiceImplBase {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public void getRoleIdByName(GetRoleIdByNameRequest request, StreamObserver<GetRoleIdByNameResponse> responseObserver) {
        String roleName = request.getRoleName();

        // 在这里实现根据 roleName 查询 roleId 的逻辑
        int roleId = roleMapper.getRoleIdByName(roleName);

        GetRoleIdByNameResponse response = GetRoleIdByNameResponse.newBuilder()
                .setRoleId(roleId)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
