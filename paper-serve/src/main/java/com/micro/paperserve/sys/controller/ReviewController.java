package com.micro.paperserve.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.common.Result;
import com.micro.common.utils.JwtUtil;
import com.micro.conferenceserve.proto.*;
import com.micro.paperServe.proto.PaperServiceGrpc;
import com.micro.paperTopicsServe.proto.GetAllPapersIdByTopicIdRequest;
import com.micro.paperTopicsServe.proto.GetAllPapersIdByTopicIdResponse;
import com.micro.paperserve.sys.entity.Papers;
import com.micro.paperserve.sys.entity.Review;
import com.micro.paperserve.sys.mapper.PapersMapper;
import com.micro.paperserve.sys.mapper.ReviewMapper;
import com.micro.paperserve.sys.service.IPapersService;
import com.micro.paperserve.sys.service.IReviewService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.awt.print.Paper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2023-11-21
 */
@Slf4j
@Api(tags = {"审稿接口列表"})
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    @GrpcClient("conference-serve")
    PcmemberServiceGrpc.PcmemberServiceBlockingStub pcmemberServiceBlockingStub;

    @Resource
    private ReviewMapper reviewMapper;

    @Resource
    private PapersMapper papersMapper;

    @Resource
    private JwtUtil jwtUtil;

    /*
    * 提交审稿信息接口
    * */
    @PostMapping("/submit")
    public Result<Map<String,Object>> submitReview(@RequestBody Review review, @RequestParam ("token") String token) {


        Integer paperId = review.getPaperId();
        Integer userId = jwtUtil.getUserIdFromToken(token);
        Papers paper = papersMapper.getPaperWithId(paperId);
        Integer conferenceId = paper.getConferenceId();

        // 改成paper_pcmember的status = 1
        getPcmemberIdByUserIdRequest setRequest = getPcmemberIdByUserIdRequest.newBuilder()
                .setUserId(userId)
                .setConferenecId(conferenceId)
                .build();
        getPcmemberIdByUserIdResponse setResponse = pcmemberServiceBlockingStub.getPcmemberIdByUserId(setRequest);
        Integer reviewerId = setResponse.getPcmemberId();

        review.setReviewerId(reviewerId);
        log.debug("paperId: " + paperId.toString() + "reviewId: " + reviewerId);
        // 要验证 “PC member不能审自己提交的稿件和自己作为作者的稿”
        reviewService.submitReview(review);

        // 改成paper_pcmember的status = 1
        setPaperPcMemberState request = setPaperPcMemberState.newBuilder()
                .setPaperId(paperId)
                .setUserId(userId)
                .setConferenceId(conferenceId)
                .build();
        setResult response = pcmemberServiceBlockingStub.setAttributeValue(request);

        if (response.getSuccess()) {
            return Result.success("审稿信息提交成功");
        }
        else {
            return Result.fail("审稿信息提交失败");
        }
//        UpdateWrapper<Papers> updateWrapper = new UpdateWrapper<>();
//        updateWrapper.eq("paperId", paperId).set("status", "已审稿");
//        papersMapper.update(null, updateWrapper);
    }

    /*
    * reviewer获取review的接口
    * */
//    @GetMapping("/reviewer")
//    public Result<Map<String,Object>> getReviewByReviewer(@RequestParam(value = "pageNo") Long pageNo,
//                                                          @RequestParam(value = "pageSize") Long pageSize,
//                                                          String token){
//        Integer userId = jwtUtil.getUserIdFromToken(token);
//        getPcmemberIdByUserIdRequest setRequest = getPcmemberIdByUserIdRequest.newBuilder()
//                .setUserId(userId)
//                .build();
//        getPcmemberIdByUserIdResponse setResponse = pcmemberServiceBlockingStub.getPcmemberIdByUserId(setRequest);
//        Integer reviewerId = setResponse.getPcmemberId();
//        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
//        wrapper.ne(Review::getReviewerId, reviewerId);
//        wrapper.orderByDesc(Review::getReviewerId);
//
//        Page<Review> page = new Page<>(pageNo,pageSize);
//        reviewService.page(page,wrapper);
//
//        Map<String,Object> data = new HashMap<>();
//        data.put("total",page.getTotal());
//        data.put("rows",page.getRecords());
//
//        return Result.success(data);
//    }

    /*
     * reviewer获取先前review的接口
     * */
    @GetMapping("/rebuttal-reviewer")
    public Result<Map<String,Object>> getRebuttalReviewByReviewer(@RequestParam("paperId") Integer paperId,
                                                                  @RequestParam String token){
        Integer userId = jwtUtil.getUserIdFromToken(token);
        Papers paper = papersMapper.getPaperWithId(paperId);
        Integer conferenceId = paper.getConferenceId();
        getPcmemberIdByUserIdRequest setRequest = getPcmemberIdByUserIdRequest.newBuilder()
                .setUserId(userId)
                .setConferenecId(conferenceId)
                .build();
        getPcmemberIdByUserIdResponse setResponse = pcmemberServiceBlockingStub.getPcmemberIdByUserId(setRequest);
        Integer reviewerId = setResponse.getPcmemberId();

        LambdaQueryWrapper<Review> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Review::getPaperId, paperId).eq(Review::getReviewerId, reviewerId);
        Review review = reviewMapper.selectOne(wrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("review", review);
        return Result.success(data);
    }

    /*
     * author获取review的接口
     * */
    @GetMapping("/author")
    public Result<Map<String,Object>> getReviewByAuthor(@RequestParam(value = "pageNo") Long pageNo,
                                                        @RequestParam(value = "pageSize") Long pageSize,
                                                        @RequestParam(value = "paperId") Integer paperId){
//        Integer authorId = jwtUtil.getUserIdFromToken(token);

        // 依据paperId获取其paperIdList
//        LambdaQueryWrapper<Papers> papersWrapper = new LambdaQueryWrapper<>();
//        papersWrapper.eq(Papers::getPaperId, paperId);
//        List<Papers> paperList = papersMapper.selectList(papersWrapper);
//        List<Integer> paperIds = paperList.stream().map(Papers::getPaperId).collect(Collectors.toList());

        // 从paperIdList获取其reviewList
        LambdaQueryWrapper<Review> reviewWrapper = new LambdaQueryWrapper<>();
        reviewWrapper.in(Review::getPaperId, paperId);

        Page<Review> page = new Page<>(pageNo,pageSize);
        reviewService.page(page, reviewWrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());

        return Result.success(data);
    }

    /*
    * 更新审稿得分接口
    * */
    @PutMapping("/update")
    public Result<Map<String,Object>> updateReview(@RequestBody Review review) {
        review.setCheck(1);
        reviewService.updateReview(review);

        // 檢查是不是paper所有稿件都过了
        Integer paperId = review.getPaperId();
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getPaperId, paperId);
        List<Review> reviewList = reviewMapper.selectList(queryWrapper);
        boolean allChecked = true;
        for (Review checkReview: reviewList) {
            if (checkReview.getCheck() != 1) {
                allChecked = false;
                break;
            }
        }
        if (allChecked) {
            Papers paper = papersMapper.getPaperWithId(paperId);
            paper.setStatus("review-complete");
            papersMapper.updateById(paper);
        }

        return Result.success("审稿信息更新成功");
    }

    /*
     * 提交辩驳信息接口
     * */
    @PutMapping("/rebuttal")
    public Result<Map<String,Object>> submitRebuttal(@RequestParam(value = "paperId") Integer paperId,
                                                     @RequestParam(value = "rebuttal") String rebuttal) {
        // 检查之前有没有提交过rebuttal
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getPaperId, paperId);
        List<Review> reviews = reviewMapper.selectList(queryWrapper);
        boolean neadRebuttal = false;

        // 检查是否需要辩驳或有没有提交过
        for (Review review: reviews) {
            if (review.getReviewScore() < 1) {
                neadRebuttal = true;
            }
            if (review.getRebuttal() != null) {
                return Result.fail("辩驳只能提交一次");
            }
        }

        if (!neadRebuttal) {
            return Result.fail("所有评分不低于1，无需进行辩驳");
        }

        LambdaQueryWrapper<Papers> paperQueryWrapper = new LambdaQueryWrapper<>();
        paperQueryWrapper.eq(Papers::getPaperId, paperId);
        Papers paper = papersMapper.selectOne(paperQueryWrapper);

        // 更新所有的review
        for (Review review: reviews) {
            review.setRebuttal(rebuttal);
            reviewService.submitRebuttal(review);
        }

        paper.setStatus("complete-rebuttal");
        papersMapper.updateById(paper);

        return Result.success("辩驳信息提交成功");
    }
}
