package com.micro.paperserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.micro.paperserve.sys.entity.Papers;
import com.micro.paperserve.sys.entity.Review;
import com.micro.paperserve.sys.mapper.PapersMapper;
import com.micro.paperserve.sys.service.IPapersService;
import com.micro.paperserve.sys.service.IReviewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-11-06
 */
@Service
public class PapersServiceImpl extends ServiceImpl<PapersMapper, Papers> implements IPapersService {

    @Resource
    private IReviewService reviewService;

    public List<Integer> getAllPapersIdByConferenceId(Integer conferenceId) {
        LambdaQueryWrapper<Papers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Papers::getConferenceId, conferenceId);
        return this.list(queryWrapper).stream().map(Papers::getPaperId).collect(Collectors.toList());
    }

    public List<String> getAllPapersStatusByConferenceId(Integer conferenceId) {
        LambdaQueryWrapper<Papers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Papers::getConferenceId, conferenceId);
        return this.list(queryWrapper).stream().map(Papers::getStatus).collect(Collectors.toList());
    }

//    public void updatePaperStateToQualified(Integer paperId){
//        LambdaUpdateWrapper<Papers> updateWrapper = new LambdaUpdateWrapper<>();
//        updateWrapper.eq(Papers::getPaperId, paperId);
//        updateWrapper.set(Papers::getStatus, "Qualified");
//        this.update(updateWrapper);
//    }

    public void updatePaperStateToQualified(Integer paperId) {
        LambdaUpdateWrapper<Papers> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Papers::getPaperId, paperId);

        // 这里需要根据实际情况修改查询 review 表的 SQL 语句

        boolean allReviewScoresGreaterThanZero = reviewService.
                getAllReviewScoresByPaperId(paperId);

        if (allReviewScoresGreaterThanZero) {
            updateWrapper.set(Papers::getStatus, "Qualified");
        } else {
            updateWrapper.set(Papers::getStatus, "Rejected");
        }

        this.update(updateWrapper);
    }

//    public void updatePaperStateToRejected(Integer paperId){
//        LambdaUpdateWrapper<Papers> updateWrapper = new LambdaUpdateWrapper<>();
//        updateWrapper.eq(Papers::getPaperId, paperId);
//        updateWrapper.set(Papers::getStatus, "Rejected");
//        this.update(updateWrapper);
//    }

//    public List<Integer> getAllPapersReviewByConferenceId(Integer conferenceId) {
//
//        LambdaQueryWrapper<Papers> paperQueryWrapper = new LambdaQueryWrapper<>();
//        paperQueryWrapper.eq(Papers::getConferenceId, conferenceId);
//        List<Papers> papers = this.list(paperQueryWrapper);
//        ReviewServiceImpl reviewService;
//
//        List<Integer> allReviewScores = new ArrayList<>();
//        for (Papers paper : papers) {
//
//              Integer paperid = paper.getPaperId();
//              List<Integer> review_scores =  reviewService.getAllReviewScoresByPaperId(paperid);
//
////            LambdaQueryWrapper<Review> reviewQueryWrapper = new LambdaQueryWrapper<>();
////            reviewQueryWrapper.eq(Review::getPaperId, paper.getPaperId());
//            List<Review> reviews = this.list(reviewQueryWrapper);
//
//
//
//            for (Review review : reviews) {
//                allReviewScores.add(review.getReviewScore());
//            }
//        }
//        return allReviewScores;
//    }

}
