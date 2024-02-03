package com.micro.paperserve.sys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.micro.paperServe.proto.*;
import com.micro.paperserve.sys.entity.Papers;
import com.micro.paperserve.sys.entity.Review;
import com.micro.paperserve.sys.mapper.PapersMapper;
import com.micro.paperserve.sys.mapper.ReviewMapper;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.print.Paper;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Slf4j
@GrpcService
public class PapersGrpcServiceImpl extends PaperServiceGrpc.PaperServiceImplBase{
    @Autowired
    private PapersServiceImpl paperService;

    @Resource
    private PapersMapper papersMapper;

    @Resource
    private ReviewMapper reviewMapper;

    @Override
    public void getAllPapersIdByConferenceId(com.micro.paperServe.proto.GetAllPapersIdByConferenceIdRequest request,
                                             io.grpc.stub.StreamObserver<GetAllPapersIdByConferenceIdResponse> responseObserver) {
        Integer conferenceId = request.getConferenceId();
        List<Integer> papers =  paperService.getAllPapersIdByConferenceId(conferenceId);

        GetAllPapersIdByConferenceIdResponse response = GetAllPapersIdByConferenceIdResponse.newBuilder()
                .addAllPaperId(papers)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllPapersStatusByConferenceId(com.micro.paperServe.proto.GetAllPapersStatusByConferenceIdRequest request,
                                             io.grpc.stub.StreamObserver<GetAllPapersStatusByConferenceIdResponse> responseObserver) {
        Integer conferenceId = request.getConferenceId();
        List<String> papers =  paperService.getAllPapersStatusByConferenceId(conferenceId);

        GetAllPapersStatusByConferenceIdResponse response = GetAllPapersStatusByConferenceIdResponse.newBuilder()
                .addAllStatus(papers)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


//    @Override
//    public void getAllPapersReviewByConferenceId(com.micro.paperServe.proto.GetAllPapersReviewByConferenceIdRequest request,
//                                                 io.grpc.stub.StreamObserver<GetAllPapersReviewByConferenceIdResponse> responseObserver) {
//        Integer conferenceId = request.getConferenceId();
//        List<Integer> reviews =  paperService.getAllPapersReviewByConferenceId(conferenceId);
//
//        GetAllPapersReviewByConferenceIdResponse response = GetAllPapersReviewByConferenceIdResponse.newBuilder()
//                .addAllReviewScore(reviews)
//                .build();
//
//        responseObserver.onNext(response);
//        responseObserver.onCompleted();
//    }


    @Override
    public void updatePaperStateToQualified(com.micro.paperServe.proto.UpdatePaperStateToQualifiedRequest request,
                                           io.grpc.stub.StreamObserver<UpdatePaperStateToQualifiedResponse> responseObserver) {
        // 更新paper状态
        Integer conferenceId = request.getConferenceId();
        LambdaQueryWrapper<Papers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Papers::getConferenceId, conferenceId);
        List<Papers> papers = papersMapper.selectList(queryWrapper);

        for (Papers paper : papers) {
            Integer paperId = paper.getPaperId();
            paperService.updatePaperStateToQualified(paperId);

        }

        UpdatePaperStateToQualifiedResponse response = UpdatePaperStateToQualifiedResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updatePaperStateToComplete(com.micro.paperServe.proto.UpdatePaperStateToCompleteRequest request,
                                           io.grpc.stub.StreamObserver<UpdatePaperStateToCompleteResponse> responseObserver) {
        // 更新paper状态
        Integer paperId = request.getPaperId();
        Papers paper = new Papers();
        paper.setPaperId(paperId);
        paper.setStatus("review-complete");
        papersMapper.updateById(paper);

        UpdatePaperStateToCompleteResponse response = UpdatePaperStateToCompleteResponse.newBuilder()
                .setSuccess(true)
                .build();

        // 更新review的check为1
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getPaperId, paperId);
        List<Review> reviews = reviewMapper.selectList(queryWrapper);
        for (Review review: reviews) {
            review.setCheck(1);
            reviewMapper.updateById(review);
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updatePaperStateToReviewing(com.micro.paperServe.proto.UpdatePaperStateToReviewingRequest request,
              io.grpc.stub.StreamObserver<com.micro.paperServe.proto.UpdatePaperStateToReviewingResponse> responseObserver){
        Integer conferenceId = request.getConferenceId();
        LambdaQueryWrapper<Papers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Papers::getConferenceId, conferenceId);
        List<Papers> papers = papersMapper.selectList(queryWrapper);

        log.debug("grpc set paper state to reviewing.");
        for(Papers updatepaper: papers) {
            updatepaper.setStatus("reviewing");
            papersMapper.updateById(updatepaper);
        }

        UpdatePaperStateToReviewingResponse response = UpdatePaperStateToReviewingResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
