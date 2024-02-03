package com.micro.paperserve.sys.service;

import com.micro.paperserve.sys.entity.Review;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2023-11-21
 */
public interface IReviewService extends IService<Review> {

    void submitReview(Review review);

    void submitRebuttal(Review review);

    void updateReview(Review review);

    boolean getAllReviewScoresByPaperId(Integer paperId);
}
