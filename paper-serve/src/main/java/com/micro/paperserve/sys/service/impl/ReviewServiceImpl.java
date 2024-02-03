package com.micro.paperserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.micro.paperserve.sys.entity.Review;
import com.micro.paperserve.sys.mapper.ReviewMapper;
import com.micro.paperserve.sys.service.IReviewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-11-21
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements IReviewService {

    @Resource
    private ReviewMapper reviewMapper;

    @Override
    public void submitReview(Review review) {
        reviewMapper.insert(review);
    }

    @Override
    public void submitRebuttal(Review review) {
        reviewMapper.updateById(review);
    }

    @Override
    public void updateReview(Review review) {
        reviewMapper.updateById(review);
    }

    @Override

    public boolean getAllReviewScoresByPaperId(Integer paperId){
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getPaperId, paperId);
        queryWrapper.le(Review::getReviewScore, 0);
        List<Review> reviews = this.list(queryWrapper);
        return reviews.size() < 1;
    }

}
