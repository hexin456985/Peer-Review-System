package com.micro.paperserve.sys.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.paperserve.sys.entity.Topics;

public interface TopicMapper extends BaseMapper<Topics>{

    @Insert("INSERT INTO Topics (topic_name) VALUES (#{topicName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertTopic(Topics topic);

    @Select("SELECT id FROM Topics WHERE conference_id = #{ConferenceId} AND topic_name = #{topicName}")
    Integer findTopicIdByConferenceIdAndTopicName(@Param("ConferenceId") Integer ConferenceId, @Param("topicName") String topicName);



}
