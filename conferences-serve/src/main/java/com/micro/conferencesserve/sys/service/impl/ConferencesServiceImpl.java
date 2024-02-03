package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.conferencesserve.sys.entity.Conferences;
import com.micro.conferencesserve.sys.entity.TopicPcmember;
import com.micro.conferencesserve.sys.entity.Topics;
import com.micro.conferencesserve.sys.entity.pcmembers;
import com.micro.conferencesserve.sys.mapper.PCMembersMapper;
import com.micro.conferencesserve.sys.mapper.TopicPcmemberMapper;
import com.micro.conferencesserve.sys.mapper.TopicsMapper;
import com.micro.conferencesserve.sys.mapper.ConferencesMapper;
import com.micro.conferencesserve.sys.service.IConferencesService;
import com.micro.conferencesserve.sys.service.ITopicsService;
import com.micro.userroleserve.proto.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-10-24
 */
@Service
@Slf4j
public class ConferencesServiceImpl extends ServiceImpl<ConferencesMapper, Conferences> implements IConferencesService {
    // 这里注入ConferenceMapper
    @Autowired
    private ConferencesMapper conferencesMapper;

    @Resource
    private ITopicsService topicsService;

    @Resource
    private PCMembersMapper pcMembersMapper;

    @Resource
    private TopicPcmemberMapper topicPcmemberMapper;

    @GrpcClient("user-serve")
    UserRoleServiceGrpc.UserRoleServiceBlockingStub synchronousUserRoleClient;


    // 这里实现验证会议名字是否重复的逻辑
    public boolean isNameAvailable(@Param("name") String Fullname) {
        LambdaQueryWrapper<Conferences> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conferences::getFullName, Fullname);
        Conferences conference = this.list(queryWrapper).get(0);
        return conference == null;
    }


    // 这里实现保存会议申请的逻辑
    public void saveConference(Conferences conferences) {
        // 设置会议的status=待审核
        conferences.setStatus("待审核");
        conferencesMapper.insert(conferences);
    }

    // 这里实现获取所有status=待审核的会议的逻辑
    public List<Conferences> getConferencesByStatus() {
        LambdaQueryWrapper<Conferences> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conferences::getStatus, "待审核");
        return this.list(queryWrapper);
    }

    // 依据ChairId获取会议列表
    public List<Conferences> getConferencesByChairId(Integer chairId) {
        LambdaQueryWrapper<Conferences> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conferences::getChairId, chairId);
        return this.list(queryWrapper);
    }

    // 这里实现管理员同意/拒绝会议的逻辑
    public void updateConferenceStatus(Integer conferenceId, String action) {
        // 更新会议状态
        Conferences conferences = conferencesMapper.selectById(conferenceId);
        //conferences.setStatus(action);

        // 如果通过，增加用户为chair的权限
        if ("accept".equals(action)) {
            conferences.setStatus("通过");
            conferencesMapper.updateById(conferences);

            // 写入用户角色表
            Integer chairId = conferences.getChairId();

            // 将chair写入pcmember，并选取所有topics
            setChairAsPcmember(conferenceId, chairId);

            // 调用 gRPC 服务来添加用户角色 chair
            UserRoleInsertRequest userRoleInsertRequest = UserRoleInsertRequest.newBuilder()
                    .setUserId(chairId)
                    .setRoleId(3)
                    .build();
            UserRoleInsertResponse userRoleInsertResponse = synchronousUserRoleClient.insertUserRole(userRoleInsertRequest);

            // 调用 gRPC 服务来添加用户角色 pc_mem
            UserRoleInsertRequest userRoleInsertRequest2 = UserRoleInsertRequest.newBuilder()
                    .setUserId(chairId)
                    .setRoleId(4)
                    .build();
            UserRoleInsertResponse userRoleInsertResponse2 = synchronousUserRoleClient.insertUserRole(userRoleInsertRequest);

        } else if ("reject".equals(action)) {
            conferences.setStatus("拒绝");
            conferencesMapper.updateById(conferences);
        }

    }

    private void setChairAsPcmember(Integer conferenceId, Integer chairId) {
        pcmembers pcMember = new pcmembers();
        pcMember.setUserId(chairId);
        pcMember.setConferenceID(conferenceId);
        pcMember.setInvitationStatus("accept");
        pcMembersMapper.insert(pcMember);
        Integer pcmembersId = pcMembersMapper.getLastInsertPcId();

        List<Topics> topics = topicsService.getTopicsByConferenceId(conferenceId);

        for (Topics topic: topics) {
            TopicPcmember topicPcmember = new TopicPcmember();
            topicPcmember.setTopicId(topic.getId());
            topicPcmember.setPcMemberId(pcmembersId);
            topicPcmemberMapper.insert(topicPcmember);
        }
    }

    // 这里实现获取指定id的会议的逻辑
    public Conferences getConferenceById(Integer conferenceId) {
        Conferences conferences = conferencesMapper.selectById(conferenceId);
        return conferences;
    }


    // 这里实现获取所有会议的逻辑
    public List<Conferences> getAllConferences() {
        LambdaQueryWrapper<Conferences> queryWrapper = new LambdaQueryWrapper<>();
        return this.list(queryWrapper);
    }

    public Integer getLastInsertId() {
        return conferencesMapper.getLastInsertId();
    }
}


