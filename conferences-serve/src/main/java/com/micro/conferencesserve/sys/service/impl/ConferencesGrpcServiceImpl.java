package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.micro.conferenceserve.proto.*;
import com.micro.conferencesserve.sys.entity.Conferences;
import com.micro.conferencesserve.sys.mapper.ConferencesMapper;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class ConferencesGrpcServiceImpl extends ConferenceServiceGrpc.ConferenceServiceImplBase {

    @Autowired
    private ConferencesMapper conferencesMapper;

    @Override
    public void getConferenceById(GetConferenceRequest request, StreamObserver<Conference> responseObserver) {
        int conferenceId = request.getConferenceId();
        Conferences conferences = conferencesMapper.selectById(conferenceId);

        if (conferences != null) {
            Conference conference = Conference.newBuilder()
                    .setConferenceId(conferences.getConferenceId())
                    .setChairId(conferences.getChairId())
                    .setShortName(conferences.getShortName())
                    .setFullName(conferences.getFullName())
                    .setEventDate(conferences.getEventDate().toString())  // 注意日期的转换
                    .setEventLocation(conferences.getEventLocation())
                    .setSubmissionDeadline(conferences.getSubmissionDeadline().toString())
                    .setReviewresultDate(conferences.getReviewresultDate().toString())
                    .setStatus(conferences.getStatus())
                    .build();

            responseObserver.onNext(conference);
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Conference not found").asRuntimeException());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getChairIdByConferenceId(com.micro.conferenceserve.proto.getChairIdByConferenceIdRequest request,
                                         io.grpc.stub.StreamObserver<com.micro.conferenceserve.proto.getChairIdByConferenceIdResponse> responseObserver) {
        int conferenceId = request.getConferenceId();
        LambdaQueryWrapper<Conferences> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conferences::getConferenceId, conferenceId);
        Conferences conference = conferencesMapper.selectOne(queryWrapper);
        Integer chairId = conference.getChairId();

        getChairIdByConferenceIdResponse response = getChairIdByConferenceIdResponse.newBuilder().setChairId(chairId).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
