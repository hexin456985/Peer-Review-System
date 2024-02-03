package com.micro.conferencesserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.conferencesserve.sys.entity.TopicPCM;

import java.util.List;

public interface ITopicPCMService extends IService<TopicPCM> {
    public List<Integer> getAllPCMemberIdByTopicId(Integer topicId);
}
