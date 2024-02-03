package com.micro.paperserve.sys.entity;

public class PaperTopics {
    private Integer paperTopicId; // 对应 paper_topic_id
    private Integer paperId;      // 对应 paper_id
    private Integer topicId;      // 对应 topic_id

    // 构造函数
    public PaperTopics() {}

    // getter 和 setter 方法
    public Integer getPaperTopicId() {
        return this.paperTopicId;
    }

    public void setPaperTopicId(Integer paperTopicId) {
        this.paperTopicId = paperTopicId;
    }

    public Integer getPaperId() {
        return this.paperId;
    }

    public void setPaperId(Integer paperId) {
        this.paperId = paperId;
    }

    public Integer getTopicId() {
        return this.topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    // 重写toString方法，便于打印和调试
    @Override
    public String toString() {
        return "PaperTopic{" +
               "paperTopicId=" + paperTopicId +
               ", paperId=" + paperId +
               ", topicId=" + topicId +
               '}';
    }
}
