package com.micro.userserve.sys.service.impl;

import com.micro.userserve.proto.GetNameByUidResponse;
import com.micro.userserve.proto.UserServiceGrpc;
import com.micro.userserve.sys.entity.User;
import com.micro.userserve.sys.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@GrpcService
public class UserGrpcServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void getUser(com.micro.userserve.proto.GetNameByUidRequest request,
                        io.grpc.stub.StreamObserver<com.micro.userserve.proto.GetNameByUidResponse> responseObserver) {
        int uid = request.getUid();
        // 調用現有的方法獲取用戶名稱
        User user = userMapper.selectById(uid);
        String name = user.getName();

        // 將結果轉換為 Protobuf 消息
        GetNameByUidResponse response = GetNameByUidResponse.newBuilder()
                .setName(name)
                .build();
        // 使用 responseObserver 向 gRPC 客户端发送响应
        responseObserver.onNext(response);
        responseObserver.onCompleted(); // 表示调用已完成
    }
}
