package com.micro.conferencesserve.sys.service.impl;

import com.micro.paperPCMemberServe.proto.PaperPCMemberService;
import net.devh.boot.grpc.server.service.GrpcService;

import com.micro.paperPCMemberServe.proto.GetPapersInfoRequest;
import com.micro.paperPCMemberServe.proto.GetPapersInfoResponse;
import com.micro.paperPCMemberServe.proto.PaperPCMemberServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class PaperPCMemberGrpcServiceImpl extends PaperPCMemberServiceGrpc.PaperPCMemberServiceImplBase{
    @Autowired
    private PaperPCMemberServiceImpl paperPCMemberService;

    @Override
    public void getPapersInfo(GetPapersInfoRequest request,
                              io.grpc.stub.StreamObserver<GetPapersInfoResponse> responseObserver) {
        Integer uid = request.getUserId();
        Integer conferenceId = request.getConferenceId();
        List<Integer> paperIds = paperPCMemberService.getPapersId(uid, conferenceId);
        List<Integer> paperStates = paperPCMemberService.getPapersState(uid, conferenceId);


        GetPapersInfoResponse response = GetPapersInfoResponse.newBuilder()
                .addAllPaperId(paperIds)
                .addAllState(paperStates)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
