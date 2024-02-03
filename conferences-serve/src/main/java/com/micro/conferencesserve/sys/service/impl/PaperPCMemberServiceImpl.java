package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.conferencesserve.sys.entity.pcmembers;
import com.micro.conferencesserve.sys.entity.PaperPCMember;
import com.micro.conferencesserve.sys.mapper.PCMembersMapper;
import com.micro.conferencesserve.sys.mapper.PaperPCMemberMapper;
import com.micro.conferencesserve.sys.service.IPaperPCMemberService;
import com.micro.paperServe.proto.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.micro.paperTopicsServe.proto.GetAllPapersIdByTopicIdRequest;
import com.micro.paperTopicsServe.proto.GetAllPapersIdByTopicIdResponse;
import com.micro.paperTopicsServe.proto.PaperTopicsServiceGrpc;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaperPCMemberServiceImpl extends ServiceImpl<PaperPCMemberMapper, PaperPCMember> implements IPaperPCMemberService {
    @GrpcClient("paper-serve")
    PaperTopicsServiceGrpc.PaperTopicsServiceBlockingStub synchronousPaperTopicsClient;

    @GrpcClient("paper-serve")
    PaperServiceGrpc.PaperServiceBlockingStub synchronousPaperClient;

    @Autowired
    private PaperPCMemberMapper paperPCMemberMapper;

    @Autowired
    private PCMembersMapper pcMembersMapper;

    public void assignPapersByTopic(Integer topicId, Integer PCMemberId){
        GetAllPapersIdByTopicIdRequest request = GetAllPapersIdByTopicIdRequest.newBuilder()
                .setTopicId(topicId)
                .build();
        GetAllPapersIdByTopicIdResponse response = synchronousPaperTopicsClient.getAllPapersIdByTopicId(request);
        List<Integer> paperIds = response.getPaperIdList();
        for(Integer paperId : paperIds){
            if(!paperPCMemberMapper.find(paperId, PCMemberId)) paperPCMemberMapper.insert(new PaperPCMember(null, paperId, PCMemberId, 0));
        }
    }

    public void assignPapers(Integer conferenceId){
        GetAllPapersIdByConferenceIdRequest request = GetAllPapersIdByConferenceIdRequest.newBuilder()
                .setConferenceId(conferenceId)
                .build();
        GetAllPapersIdByConferenceIdResponse response = synchronousPaperClient.getAllPapersIdByConferenceId(request);
        List<Integer> paperIds = response.getPaperIdList();

        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pcmembers::getConferenceID, conferenceId);
        queryWrapper.eq(pcmembers::getInvitationStatus, "accept");
        List<Integer> pcMemberIds = pcMembersMapper.selectList(queryWrapper).stream().map(pcmembers::getpCMemberID).collect(Collectors.toList());
        log.debug("accepted pcmembers: " + pcMemberIds.toString());
        for(Integer paperId : paperIds){
            for(Integer pcMemberId : pcMemberIds){
                paperPCMemberMapper.insert(new PaperPCMember(null, paperId, pcMemberId, 0));
            }
        }


    }

    public void checkPaperState(Integer paperId) {
        // 通过paperId 找到 其他paper_pcmember
        LambdaQueryWrapper<PaperPCMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperPCMember::getPaperid, paperId);
        List<Integer> paperReviewStates = paperPCMemberMapper.selectList(queryWrapper).stream().map(PaperPCMember::getState).collect(Collectors.toList());
        // 检查所有审核状态
        for (Integer paperReviewState: paperReviewStates) {
            if (paperReviewState == 0) {
                return;
            }
        }
        // 通知 paper-serve 更新状态
        UpdatePaperStateToCompleteRequest request = UpdatePaperStateToCompleteRequest.newBuilder()
                .setPaperId(paperId)
                .build();
        UpdatePaperStateToCompleteResponse response = synchronousPaperClient.updatePaperStateToComplete(request);
        log.debug("update paper result: " + response.getSuccess());
    }

    public List<Integer> getPapersId(Integer userId, Integer conferenceId){
        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pcmembers::getUserId, userId);
        queryWrapper.eq(pcmembers::getConferenceID, conferenceId);
        //queryWrapper.eq(PCMembers::getInvitationStatus, "已接受");
        Integer PCMemberId = pcMembersMapper.selectOne(queryWrapper).getpCMemberID();

        LambdaQueryWrapper<PaperPCMember> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(PaperPCMember::getPcMemberId, PCMemberId);
        return this.list(queryWrapper1).stream().map(PaperPCMember::getPaperid).collect(Collectors.toList());
    }

    public List<Integer> getPapersState(Integer userId, Integer conferenceId){
        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pcmembers::getUserId, userId);
        queryWrapper.eq(pcmembers::getConferenceID, conferenceId);
        //queryWrapper.eq(PCMembers::getInvitationStatus, "已接受");
        Integer PCMemberId = pcMembersMapper.selectOne(queryWrapper).getpCMemberID();

        LambdaQueryWrapper<PaperPCMember> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(PaperPCMember::getPcMemberId, PCMemberId);
        return this.list(queryWrapper1).stream().map(PaperPCMember::getState).collect(Collectors.toList());
    }


//    public List<String> getPapersStatus(Integer conferenceId){
//        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
////        queryWrapper.eq(pcmembers::getUserId, userId);
//        queryWrapper.eq(pcmembers::getConferenceID, conferenceId);
//        //queryWrapper.eq(PCMembers::getInvitationStatus, "已接受");
//        Integer PCMemberId = pcMembersMapper.selectOne(queryWrapper).getpCMemberID();
//
//        LambdaQueryWrapper<PaperPCMember> queryWrapper1 = new LambdaQueryWrapper<>();
//        queryWrapper1.eq(PaperPCMember::getPcMemberId, PCMemberId);
//        return this.list(queryWrapper1).stream().map(PaperPCMember::getState).collect(Collectors.toList());
//    }

}
