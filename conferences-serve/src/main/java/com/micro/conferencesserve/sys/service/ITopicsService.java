package com.micro.conferencesserve.sys.service;
import com.micro.conferencesserve.sys.entity.Topics;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2023-11-27
 */
public interface ITopicsService extends IService<Topics> {
    public void saveTopicNamesToTopics(Integer conferenceId, List<String> topicNames);
    public List<Topics> getTopicsByConferenceId(Integer conferenceId);
}
