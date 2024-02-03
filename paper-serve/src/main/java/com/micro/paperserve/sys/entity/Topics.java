package com.micro.paperserve.sys.entity;

public class Topics {
    private Integer id;       // 对应 topic_id
    private String topicName;      // 对应 topic_name
    private Integer ConferenceId;

    // 构造函数
    public Topics() {}

    // getter 和 setter 方法
    public Integer getTopicId() {
        return id;
    }

    public void setTopicId(Integer topicId) {
        this.id = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public Integer getConId() {
        return ConferenceId;
    }
    
    public void setConId(Integer ConferenceId){
        this.ConferenceId = ConferenceId;
    }

    // 重写toString方法，便于打印和调试
    @Override
    public String toString() {
        return "Topic{" +
               "topicId=" + id +
               ", topicName='" + topicName + '\'' +
               '}';
    }
}
