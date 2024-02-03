package com.micro.paperserve.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2023-11-21
 */
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "review_id", type = IdType.AUTO)
    private Integer reviewId;

    private Integer paperId;

    private Integer reviewerId;

    private Integer reviewScore;

    private String reviewComment;

    private String reviewConfidence;

    private String rebuttal;

    private Integer checked;

    public Integer getCheck() {
        return checked;
    }

    public void setCheck(Integer checked) {
        this.checked = checked;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }
    public Integer getPaperId() {
        return paperId;
    }

    public void setPaperId(Integer paperId) {
        this.paperId = paperId;
    }
    public Integer getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Integer reviewerId) {
        this.reviewerId = reviewerId;
    }
    public Integer getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(Integer reviewScore) {
        this.reviewScore = reviewScore;
    }
    public String getReviewComment() {
        return reviewComment;
    }

    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }
    public String getReviewConfidence() {
        return reviewConfidence;
    }

    public void setReviewConfidence(String reviewConfidence) {
        this.reviewConfidence = reviewConfidence;
    }
    public String getRebuttal() {
        return rebuttal;
    }

    public void setRebuttal(String rebuttal) {
        this.rebuttal = rebuttal;
    }

    @Override
    public String toString() {
        return "Review{" +
            "reviewId=" + reviewId +
            ", paperId=" + paperId +
            ", reviewerId=" + reviewerId +
            ", reviewScore=" + reviewScore +
            ", reviewComment=" + reviewComment +
            ", reviewConfidence=" + reviewConfidence +
            ", rebuttal=" + rebuttal +
        "}";
    }
}
