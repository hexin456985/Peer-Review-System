package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.micro.conferenceserve.proto.*;
import com.micro.conferencesserve.sys.entity.PaperPCMember;
import com.micro.conferencesserve.sys.entity.pcmembers;
import com.micro.conferencesserve.sys.mapper.PCMembersMapper;
import com.micro.conferencesserve.sys.mapper.PaperPCMemberMapper;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import javax.annotation.Resource;

@GrpcService
public class PcmemberGrpcServiceImpl extends PcmemberServiceGrpc.PcmemberServiceImplBase {
    @Resource
    private PCMembersMapper pcMembersMapper;

    @Resource
    private PaperPCMemberMapper paperPCMemberMapper;

    @Resource
    private PaperPCMemberServiceImpl paperPCMemberService;

    @Override
    public void setAttributeValue(setPaperPcMemberState request, StreamObserver<setResult> responseObserver) {
        Integer userId = request.getUserId(), paperId = request.getPaperId(), conferenceId = request.getConferenceId();

        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pcmembers::getUserId, userId).eq(pcmembers::getConferenceID, conferenceId);
        pcmembers pcmembers = pcMembersMapper.selectOne(queryWrapper);
        Integer pcmemberId = pcmembers.getpCMemberID();

        UpdateWrapper<PaperPCMember> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("paperId", paperId).eq("pc_member_id", pcmemberId);

        PaperPCMember paperPCMember = new PaperPCMember();
        paperPCMember.setState(1);
        boolean updateResult = paperPCMemberMapper.update(paperPCMember, updateWrapper) > 0;

        if (updateResult) {
            paperPCMemberService.checkPaperState(paperId);
        }

        setResult response = setResult.newBuilder().setSuccess(updateResult).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPcmemberIdByUserId(getPcmemberIdByUserIdRequest request,
                                      StreamObserver<getPcmemberIdByUserIdResponse> responseObserver) {
        Integer userId = request.getUserId(), conferenceId = request.getConferenecId();
        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pcmembers::getUserId, userId).eq(pcmembers::getConferenceID, conferenceId);
        pcmembers pcmembers = pcMembersMapper.selectOne(queryWrapper);

        getPcmemberIdByUserIdResponse response = getPcmemberIdByUserIdResponse.newBuilder()
                .setPcmemberId(pcmembers != null ? pcmembers.getpCMemberID() : 0)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
