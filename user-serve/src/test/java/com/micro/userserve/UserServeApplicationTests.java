package com.micro.userserve;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServeApplicationTests {

    private ManagedChannel channel;

    @BeforeEach
    public void setUp() {
        // 創建 gRPC 測試客戶端
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
    }

//    @Test
//    public void testGetUserNameForUid1() {
//        // 創建 gRPC 客戶端存根
//        UserServiceOuterClass. stub = UserServiceGrpc.newBlockingStub(channel);
//
//        // 調用 gRPC 服務方法，通過 uid=1 查找 name
//        int uid = 1;
//        UserRequest request = UserRequest.newBuilder().setUserId(uid).build();
//        UserResponse response = stub.getUserName(request);
//
//        // 驗證返回的結果是否符合預期
//        String expectedName = "John"; // 假設 uid=1 的名字是 "John"
//        String actualName = response.getName();
//        assertEquals(expectedName, actualName);
//    }



    // 關閉測試客戶端通道
    @AfterEach
    public void tearDown() {
        channel.shutdown();
    }

}
