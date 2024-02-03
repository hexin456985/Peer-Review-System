package com.micro.paperserve.sys.service.impl;


import com.micro.paperTopicsServe.proto.PaperTopicsServiceGrpc;
import com.micro.paperTopicsServe.proto.GetAllPapersIdByTopicIdResponse;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@GrpcService
public class PaperTopicsGrpcServiceImpl extends PaperTopicsServiceGrpc.PaperTopicsServiceImplBase{
    @Autowired
    private PaperTopicsServiceImpl paperTopicsService;

    @Override
    public void getAllPapersIdByTopicId(com.micro.paperTopicsServe.proto.GetAllPapersIdByTopicIdRequest request,
                                        io.grpc.stub.StreamObserver<com.micro.paperTopicsServe.proto.GetAllPapersIdByTopicIdResponse> responseObserver) {
        Integer topicId = request.getTopicId();
        List<Integer> papers =  paperTopicsService.getAllPapersIdByTopicId(topicId);

        GetAllPapersIdByTopicIdResponse response = GetAllPapersIdByTopicIdResponse.newBuilder()
                .addAllPaperId(papers)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
