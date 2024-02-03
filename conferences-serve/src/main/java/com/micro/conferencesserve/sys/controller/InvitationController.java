package com.micro.conferencesserve.sys.controller;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.common.Result;
import com.micro.common.utils.JwtUtil;
import com.micro.conferencesserve.sys.entity.*;
import com.micro.conferencesserve.sys.mapper.PCMembersMapper;
import com.micro.conferencesserve.sys.mapper.TopicPcmemberMapper;
import com.micro.conferencesserve.sys.service.IConferencesService;
import com.micro.conferencesserve.sys.service.IInvitationService;
import com.micro.conferencesserve.sys.service.IPCMembersService;
import com.micro.conferencesserve.sys.service.ITopicsService;
import com.micro.conferencesserve.sys.service.impl.PCMembersServiceImpl;
import com.micro.conferencesserve.sys.service.impl.TopicPCMServiceImpl;
import com.micro.userroleserve.proto.UserRoleServiceGrpc;
import com.micro.userserve.proto.GetNameByUidRequest;
import com.micro.userserve.proto.UserServiceGrpc;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  会议邀请记录前端控制器
 * </p>
 *
 * @author x
 * @since 2023-10-28
 */
@Api(tags = {"审稿人邀请接口列表"})
@Slf4j
@RestController
@RequestMapping("/invite")
public class InvitationController {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private IInvitationService iInvitationService;

    @Resource
    private IConferencesService conferencesService;

    @Resource
    private ITopicsService topicsService;

    @Resource
    private IPCMembersService pcMembersService;

    @Resource
    private PCMembersMapper pcMembersMapper;

    @Resource
    private TopicPcmemberMapper topicsPcmembersMapper;

    @Autowired
    private PCMembersServiceImpl pcMemberService;

    @Autowired
    private TopicPCMServiceImpl topicPCMService;
    
    @GrpcClient("user-serve")
    UserRoleServiceGrpc.UserRoleServiceBlockingStub synchronousUserRoleClient;

    @GrpcClient("user-serve")
    UserServiceGrpc.UserServiceBlockingStub synchronousUserClient;

    /*
     *  会议邀请
     */
    @PostMapping("/{conferenceId}")
    public Result<?> inviteMembers(@PathVariable("conferenceId") Integer id, @RequestBody List<Integer> userIds) {
        log.debug("ids: " + userIds.toString());
        List<Invitation> addInvitations = new ArrayList<>();
        List<Invitation> updInvitations = new ArrayList<>();
        Invitation invitation = null;
        for (Integer userId: userIds) {
            invitation = new Invitation();
            invitation.setConferenceId(id);
            invitation.setUserId(userId);
            invitation.setStatus(1);//1-为邀请待接受状态
            QueryWrapper<Invitation> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(Invitation::getConferenceId, id).eq(Invitation::getUserId, userId);
            Invitation queryInvitation = iInvitationService.getOne(queryWrapper);
            if(queryInvitation != null){
                updInvitations.add(queryInvitation);
            }else {
                addInvitations.add(invitation);
            }

        }
        if(addInvitations.size() > 0){
            iInvitationService.saveBatch(addInvitations);
        }
        if(updInvitations.size() > 0){
            updInvitations.stream().forEach(i-> i.setStatus(1));
            iInvitationService.updateBatchById(updInvitations);
        }
        return Result.success("success");
    }

    /*
     * 邀请会议状态查询
     */
    @GetMapping("/state")
    public Result<Page<JSONObject>> inviteStatus(
            @RequestParam(value = "pageNo") Long pageNo,
            @RequestParam(value = "pageSize") Long pageSize,
            String token
    ) {
        Integer chairId = jwtUtil.getUserIdFromToken(token);
        List<Invitation> invitations = iInvitationService.getInvitationIdByUserId(chairId);

        List<JSONObject> retList = new ArrayList<>();
        if (invitations.size() > 0) {
            for (Invitation invitation : invitations) {
                JSONObject retObject = new JSONObject();

                Integer conferenceId = invitation.getConferenceId();
                Conferences conference = conferencesService.getConferenceById(conferenceId);
                List <Topics> topics = topicsService.getTopicsByConferenceId(conferenceId);

                if (conference != null) {
                    retObject.put("fullName", conference.getFullName());
                    retObject.put("shortName", conference.getShortName());
                }
                retObject.put("invitationId", invitation.getInvitationId());
                retObject.put("conferenceId", invitation.getConferenceId());

                retObject.put("topics", topicsService.getTopicsByConferenceId(conferenceId));

                // Set chairName from the userService
                Integer invitedId = invitation.getUserId();

                GetNameByUidRequest userRequest = GetNameByUidRequest.newBuilder().setUid(invitedId).build();
                String invitedName = synchronousUserClient.getUser(userRequest).getName();

                retObject.put("invitedName", invitedName);
                switch (invitation.getStatus()) {
                    case 1:
                        retObject.put("status", "待确认");
                        break;
                    case 2:
                        retObject.put("status", "已同意");
                        break;
                    case 3:
                        retObject.put("status", "已拒绝");
                        break;
                }
                retList.add(retObject);
            }
        }

        // Create a custom Page
        Page<JSONObject> customPage = new Page<>(pageNo, pageSize, invitations.size());
        customPage.setRecords(retList);

        return Result.success(customPage);
    }



     
    @GetMapping("/view_topics")
    public Result<?> inviteTopics(
            @RequestParam("conferenceId") Integer conferenceId
    ) {
//        Integer pcMemberId = jwtUtil.getUserIdFromToken(token);
        List<Topics> topics = topicsService.getTopicsByConferenceId(conferenceId);
        List<String> topicsnames = new ArrayList<>();
        for (Topics topic : topics){
            String topicsname = topic.getTopicName();
            topicsnames.add(topicsname);
        }
        return Result.success(topics);
    }


    
    @PostMapping("/topicPCMember")
    public Result<?> insertTopicPCMember(@RequestBody Map<String, Object> request)
    {
        Integer invitationId = (Integer) request.get("invitationId");
        List<Integer> topicIds = (List<Integer>) request.get("topicIds");
        String token = (String) request.get("token");
        log.debug("InvitationId: {}, TopicIds: {}, Token: {}", invitationId, topicIds, token);

        Invitation invitation = iInvitationService.getById(invitationId);
        Integer conferenceId = invitation.getConferenceId();

        // 先把邀请设为接受
        iInvitationService.replyInvitation(invitationId, conferenceId, "accept");

        // 处理topic与pcmember
//        // 1. 查询所有 topic 信息
//        List<Topics> topics = topicsService.getTopicsByConferenceId(conferenceId);
//
//        //2. 遍历 topic 列表，修改 PC_member 列
        Integer userId = invitation.getUserId();

        pcmembers pcMember = new pcmembers();
        pcMember.setUserId(userId);
        pcMember.setConferenceID(conferenceId);
        pcMember.setInvitationStatus("accept");
        pcMembersMapper.insert(pcMember);

        Integer pcmemberId = pcMembersService.getLastInsertId();
        for (Integer topicId : topicIds) {
            TopicPcmember topicsPcmember = new TopicPcmember();
            topicsPcmember.setTopicId(topicId);
            topicsPcmember.setPcMemberId(pcmemberId);
            topicsPcmembersMapper.insert(topicsPcmember);
        }


        // LHT版本，暂保留
//        Integer uid = jwtUtil.getUserIdFromToken(token);
//        LambdaQueryWrapper<PCMembers> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(PCMembers::getConferenceID, conferenceId);
//        queryWrapper.eq(PCMembers::getUserId, uid);
//        Integer pcMemberId = pcMemberService.getOne(queryWrapper).getpCMemberID();
//        List<Topics> topics = topicsService.getTopicsByConferenceId(conferenceId);
//        for(Topics topic : topics) {
//            if (topicNames.contains(topic.getTopicName())) {
//                topicPCMService.save(new TopicPCM(null, topic.getId(), pcMemberId));
//            }
//        }

        // 4. 返回成功信息
        return Result.success("成功插入pc_member列");
    }


    /*
     *  接受/拒绝会议邀请
     */
    @PostMapping("/reply/{invitationId}")
    public Result<?> inviteReply(@PathVariable("invitationId") Integer id, @RequestParam("action") String action) {
        Invitation invitation = iInvitationService.getById(id);
        Integer conferenceId = invitation.getConferenceId();

        // 1.查询会议邀请是否存在
        iInvitationService.replyInvitation(id, conferenceId, action);
        return Result.success("success");
    }

    /*
     * 查询邀请会议状态待处理的记录
     */
    @GetMapping("/todo")
    public Result<Page<JSONObject>> inviteToDoStatus(
            @RequestParam(value = "pageNo") Long pageNo,
            @RequestParam(value = "pageSize") Long pageSize,
            String token
    ) {
        Integer id = jwtUtil.getUserIdFromToken(token);
        QueryWrapper<Invitation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Invitation::getUserId, id).eq(Invitation::getStatus, 1);

        Page<Invitation> page = new Page<>(pageNo, pageSize);
        iInvitationService.page(page, queryWrapper);

        List<JSONObject> retList = new ArrayList<>();
        if (page.getRecords().size() > 0) {
            for (Invitation invitation : page.getRecords()) {
                JSONObject retObject = new JSONObject();

                Integer conferenceId = invitation.getConferenceId();
                Conferences conference = conferencesService.getConferenceById(conferenceId);

                if (conference != null) {
                    retObject.put("fullName", conference.getFullName());
                    retObject.put("shortName", conference.getShortName());
                    retObject.put("eventDate", conference.getEventDate());
                }
                retObject.put("invitationId", invitation.getInvitationId());
                retObject.put("conferenceId", invitation.getConferenceId());
                Integer chairId = conference.getChairId();

                GetNameByUidRequest userRequest = GetNameByUidRequest.newBuilder().setUid(chairId).build();
                String chairName = synchronousUserClient.getUser(userRequest).getName();

                retObject.put("chairName", chairName);
                retObject.put("status", invitation.getStatus());
                retList.add(retObject);
            }
        }

        return Result.success(new Page<JSONObject>(page.getCurrent(), page.getSize(), page.getTotal()).setRecords(retList));
    }

}
