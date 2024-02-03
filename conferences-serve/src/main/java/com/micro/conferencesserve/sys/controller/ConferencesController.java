package com.micro.conferencesserve.sys.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.common.Result;
import com.micro.common.utils.JwtUtil;
import com.micro.conferencesserve.sys.entity.Conferences;
import com.micro.conferencesserve.sys.mapper.ConferencesMapper;
import com.micro.conferencesserve.sys.entity.Topics;
import com.micro.conferencesserve.sys.mapper.TopicsMapper;
import com.micro.conferencesserve.sys.service.impl.*;

import com.micro.paperServe.proto.*;
import com.micro.userserve.proto.GetNameByUidRequest;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2023-10-24
 */

@Api(tags = {"会议接口列表"})
@RestController
@RequestMapping("/conferences")
@Slf4j
public class ConferencesController {
    
    @Autowired
    private ConferencesServiceImpl conferenceService;

    @Autowired
    private ConferencesMapper conferenceMapper;

    @Autowired
    private TopicsServiceImpl topicsService;

    @Autowired
    private PCMembersServiceImpl pcMemberService;

    @Autowired
    private TopicPCMServiceImpl topicPCMService;

    @Autowired
    private PaperPCMemberServiceImpl paperPCMemberService;

//    @Autowired
//    private PaperConferenceServiceImpl paperConferenceService;

    @GrpcClient("paper-serve")
    PaperServiceGrpc.PaperServiceBlockingStub paperServiceBlockingStub;

    @GrpcClient("paper-serve")
    PaperServiceGrpc.PaperServiceBlockingStub synchronousPaperClient;

    @Resource
    private JwtUtil jwtUtil;

    //会议申请提交，填写相关属性
    @PostMapping("/apply")
    public Result<Conferences> applyConference(@RequestBody Conferences conferences,  @RequestParam("topics") String topics, @RequestParam("token") String token) {
        // 从JWT Token中获取用户ID
        Integer uid = jwtUtil.getUserIdFromToken(token);

        QueryWrapper<Conferences> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("full_name", conferences.getFullName());
        // 验证会议名字是否跟数据库的内容重复
        if (conferenceMapper.selectOne(queryWrapper) != null) {
            return Result.fail(20006, "会议名字重复");
        }

        // 验证会议topic是否至少有一个
        if (topics == null){
            return Result.fail(20007, "会议topic至少需要一个");
        }

        if (!(conferences.getEventDate().isAfter(conferences.getReviewresultDate()) &&
                conferences.getReviewresultDate().isAfter(conferences.getSubmissionDeadline()))) {
            return Result.fail("会议日期不合法");
        }

        List<String> topicList = new ArrayList<>();
        // 使用逗号分割字符串
        String[] topicArray = topics.split(",");

        // 遍历数组并将每个字符串添加到list中
        for (String topic : topicArray) {
            topicList.add(topic);
        }
        conferences.setStatus("待审核");
        conferences.setChairId(uid); // 设置会议的用户 ID
        // 保存会议申请到数据库
        conferenceService.save(conferences);

        Integer conferenceId = conferenceService.getLastInsertId();
        // 把每个topic名字存到Topics表里的topic_name
        topicsService.saveTopicNamesToTopics(conferenceId, topicList);
        return Result.success("会议保存成功");
    }



    // 获取所有status为“待审核”的会议
    @GetMapping("/apply")
    public Result<List<Conferences>> getConferencesByStatus() {
        List<Conferences> conferences =conferenceService.getConferencesByStatus();
        return Result.success(conferences);
    }

    //管理员同意/拒绝会议
    @PostMapping("/{conferenceID}")
    public Result<Conferences> updateConferenceStatus(@PathVariable("conferenceID") Integer conferenceID, @RequestParam("action") String action) {
        conferenceService.updateConferenceStatus(conferenceID, action);
        if ("accept".equals(action)) {
            return Result.success("通过成功");
        }
        else {
            return Result.success("拒绝成功");
        }
    }

    // 获取指定id的会议
    @GetMapping("/{conferenceID}")
    public Result<Conferences> getConferenceById(@PathVariable("conferenceID") Integer conferenceID) {
        Conferences conferences = conferenceService.getConferenceById(conferenceID);
        return Result.success(conferences);
    }

    // 获取所有会议
    @GetMapping("/")
    public Result<List<Conferences>> getAllConferences() {
        List<Conferences> conferences =conferenceService.getAllConferences();
        return Result.success(conferences);
    }

    @GetMapping("/list-passed")
    public Result<Map<String, Object>> getConferencesListPassed(
            @RequestParam(value = "shortName", required = false) String shortName,
            @RequestParam(value = "pageNo") Long pageNo,
            @RequestParam(value = "pageSize") Long pageSize
    ) {
        return getConferencesListByState("开放投稿", shortName, pageNo, pageSize);
    }

    @GetMapping("/list-pending")
    public Result<Map<String, Object>> getConferencesListPending(
            @RequestParam(value = "shortName", required = false) String shortName,
            @RequestParam(value = "pageNo") Long pageNo,
            @RequestParam(value = "pageSize") Long pageSize
    ) {
        return getConferencesListByState("待审核", shortName, pageNo, pageSize);
    }

    private Result<Map<String, Object>> getConferencesListByState(
            @RequestParam(value = "state") String state,
            @RequestParam(value = "shortName", required = false) String shortName,
            @RequestParam(value = "pageNo") Long pageNo,
            @RequestParam(value = "pageSize") Long pageSize
    ) {
        LambdaQueryWrapper<Conferences> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conferences::getStatus, state); // 根据状态筛选

        if (StringUtils.hasLength(shortName)) {
            wrapper.eq(Conferences::getShortName, shortName);
        }

        wrapper.orderByDesc(Conferences::getConferenceId);

        Page<Conferences> page = new Page<>(pageNo, pageSize);
        conferenceService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }

    // 依据ChairId获取会议
    @GetMapping("/chair")
    public Result<Map<String, Object>> getConferencesListByChair(String token,
        @RequestParam(value = "shortName", required = false) String shortName,
        @RequestParam(value = "pageNo") Long pageNo,
        @RequestParam(value = "pageSize") Long pageSize
    ) {
        Integer chairId = jwtUtil.getUserIdFromToken(token);
        LambdaQueryWrapper<Conferences> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conferences::getChairId, chairId); // 根据状态筛选

        if (StringUtils.hasLength(shortName)) {
            wrapper.eq(Conferences::getShortName, shortName);
        }

        wrapper.orderByDesc(Conferences::getConferenceId);

        Page<Conferences> page = new Page<>(pageNo, pageSize);
        conferenceService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }

    @PostMapping("/start-submission/{conferenceId}")
    public Result<Map<String, Object>> startSubmission(@PathVariable Integer conferenceId) {
        Conferences conferences = new Conferences();
        conferences.setConferenceId(conferenceId);
        conferences.setStatus("开放投稿");
        conferenceMapper.updateById(conferences);
        return Result.success("开放投稿");
    }

    @PostMapping ("/start-review/{conferenceId}")
    public Result<Map<String, Object>> startReview(@PathVariable Integer conferenceId, @RequestParam("strategy") Integer strategy) {
        // 开启审稿状态
        List<Conferences> conference_list = conferenceService.getAllConferences();
        for (Conferences conference : conference_list) {
            if (conference.getConferenceId().toString().equals(conferenceId.toString())) {

                Integer pcMemberCount = pcMemberService.getPCMemberCount(conferenceId);
                if (pcMemberCount < 2) {
                    return Result.fail("PC Member不足开启审稿");
                }
            }
        }

        if(strategy == 1){
            log.debug("策略1");
            List<Topics> topics = topicsService.getTopicsByConferenceId(conferenceId);
            log.debug("topics: " + topics.toString());
            for(Topics topic : topics){
                List<Integer> PCMIds = topicPCMService.getAllPCMemberIdByTopicId(topic.getId());
                log.debug("PCMIds: " + PCMIds.toString());
                for(Integer PCMId : PCMIds){
                    paperPCMemberService.assignPapersByTopic(topic.getId(), PCMId);
                    log.debug("assign: " + topic.getId().toString() +  " / " + PCMId.toString());
                }
            }
        }
        else{
            log.debug("策略2");
            paperPCMemberService.assignPapers(conferenceId);
        }

        UpdateWrapper<Conferences> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("status", "开放投稿").eq("conferenceId",conferenceId).set("status","审稿中");
        conferenceMapper.update(null, updateWrapper);

        UpdatePaperStateToReviewingRequest request = UpdatePaperStateToReviewingRequest.newBuilder().setConferenceId(conferenceId).build();
        boolean updateResult = paperServiceBlockingStub.updatePaperStateToReviewing(request).getSuccess();
        if (updateResult) {
            return Result.success("成功开启审稿并分配稿件");
        }
        return Result.fail("更新论文状态失败");

    }

    // 发布评审结果
    @PostMapping("/publish-review/{conferenceId}")
     public Result<Map<String, Object>> publishReview(@PathVariable Integer conferenceId, @RequestParam("token") String token) {
         // 获取当前chair ID
         Integer chairId = jwtUtil.getUserIdFromToken(token);
         Conferences conferences = conferenceService.getConferenceById(conferenceId);


         GetAllPapersStatusByConferenceIdRequest request = GetAllPapersStatusByConferenceIdRequest.newBuilder()
                .setConferenceId(conferenceId)
                .build();
        GetAllPapersStatusByConferenceIdResponse response = synchronousPaperClient.getAllPapersStatusByConferenceId(request);
        List<String> all_status = response.getStatusList();

        boolean statusCorrect = true;
        Iterator<String> iterator = all_status.iterator();
        for (String allStatus : all_status) {
            if (!(Objects.equals(allStatus, "review-complete"))) {
                statusCorrect = false;
            }
        }

        if (statusCorrect) {
            conferences.setStatus("完成审稿");
            conferenceMapper.updateById(conferences);
            return Result.success("成功发布评审结果");
        } else {
            return Result.fail("评审未完成");
        }

     }

    // 发布录用结果
    @PostMapping("/publish-employed/{conferenceId}")
    public Result<Map<String, Object>> publishEmployed(@PathVariable Integer conferenceId, @RequestParam("token") String token) {
        // 获取当前chair ID
        Integer chairId = jwtUtil.getUserIdFromToken(token);
        Conferences conferences = conferenceService.getConferenceById(conferenceId);


        GetAllPapersStatusByConferenceIdRequest request = GetAllPapersStatusByConferenceIdRequest.newBuilder()
                .setConferenceId(conferenceId)
                .build();
        GetAllPapersStatusByConferenceIdResponse response = synchronousPaperClient.getAllPapersStatusByConferenceId(request);
        List<String> all_status = response.getStatusList();

        if (!all_status.containsAll(Collections.singleton("review-complete")) && !all_status.containsAll(Collections.singleton("complete-rebuttal"))) {
            return Result.fail("评审失败，状态不合法");
        } else {

            UpdatePaperStateToQualifiedRequest request2 = UpdatePaperStateToQualifiedRequest.newBuilder().setConferenceId(conferenceId).build();
            boolean updateResult = paperServiceBlockingStub.updatePaperStateToQualified(request2).getSuccess();
            if (updateResult) {
                conferences.setStatus("评审结果发布");
                conferenceMapper.updateById(conferences);

                GetAllPapersStatusByConferenceIdRequest request3 = GetAllPapersStatusByConferenceIdRequest.newBuilder()
                        .setConferenceId(conferenceId)
                        .build();
                GetAllPapersStatusByConferenceIdResponse response3 = synchronousPaperClient.getAllPapersStatusByConferenceId(request);
                List<String> all_review_status = response3.getStatusList();

                if (!all_review_status.containsAll(Collections.singleton("Rejected"))) {
                    LambdaUpdateWrapper<Conferences> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(Conferences::getConferenceId, conferenceId);
                    updateWrapper.set(Conferences::getStatus, "All_Papers_Qualified");
                    conferenceMapper.update(null, updateWrapper);

                    return Result.success("评审成功，会议相关论文都合格");
                } else {
                    LambdaUpdateWrapper<Conferences> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(Conferences::getConferenceId, conferenceId);
                    updateWrapper.set(Conferences::getStatus, "Have_Paper_Rejected");
                    conferenceMapper.update(null, updateWrapper);

                    return Result.success("评审成功，会议尚有不合格论文");
                }

            }
            return Result.fail("更新论文状态失败");
        }

    }



    @PostMapping("/papers-assignment/{conferenceId}")
    public Result<Map<String, Object>> assignPapers(@PathVariable Integer conferenceId, @RequestParam("strategy") Integer strategy) {
        if(strategy == 1){
            List<Topics> topics = topicsService.getTopicsByConferenceId(conferenceId);
            for(Topics topic : topics){
                List<Integer> PCMIds = topicPCMService.getAllPCMemberIdByTopicId(topic.getId());
                for(Integer PCMId : PCMIds){
                    paperPCMemberService.assignPapersByTopic(topic.getId(), PCMId);
                }
            }
        }
        else{
            paperPCMemberService.assignPapers(conferenceId);
        }
        return Result.success("成功分配论文");
    }

}
