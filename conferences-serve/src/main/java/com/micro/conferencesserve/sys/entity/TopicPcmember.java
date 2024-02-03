package com.micro.conferencesserve.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;


public class TopicPcmember {
    private static final long serialVersionUID = 1L;

    @TableId(value = "topicPcmemberId", type = IdType.AUTO)
    private Integer topicPcmemberId;

    private Integer topicId;

    private Integer pcMemberId;

    public Integer getTopicPcmemberId() {
        return topicPcmemberId;
    }

    public void setTopicPcmemberId(Integer topicPcmemberId) {
        this.topicPcmemberId = topicPcmemberId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public Integer getPcMemberId() {
        return pcMemberId;
    }

    public void setPcMemberId(Integer pcMemberId) {
        this.pcMemberId = pcMemberId;
    }

    @Override
    public String toString() {
        return "TopicsPcmembers{" +
                "topicPcmemberId=" + topicPcmemberId +
                ", topicId=" + topicId +
                ", pcMemberId=" + pcMemberId +
                '}';
    }


}
