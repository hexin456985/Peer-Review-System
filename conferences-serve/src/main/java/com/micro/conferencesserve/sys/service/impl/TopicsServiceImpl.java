package com.micro.conferencesserve.sys.service.impl;

import com.micro.conferencesserve.sys.entity.Conferences;
//import com.micro.conferencesserve.sys.entity.PCMembers;
import com.micro.conferencesserve.sys.entity.Topics;
import com.micro.conferencesserve.sys.mapper.TopicsMapper;
import com.micro.conferencesserve.sys.service.ITopicsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-11-27
 */
@Service
public class TopicsServiceImpl extends ServiceImpl<TopicsMapper, Topics> implements ITopicsService {

        @Autowired
        private TopicsMapper topicsMapper;

        public void saveTopicNamesToTopics(Integer conferenceId, List<String> topicNames) {
        // 遍历topicNames，将每个topic_name存到topics表中
            log.debug("cid: " + conferenceId.toString());
            for (String topicName : topicNames) {
                Topics topic = new Topics();
                topic.setTopicName(topicName);
                topic.setConferenceId(conferenceId);
                topicsMapper.insert(topic);
            }
        }

        public List<Topics> getTopicsByConferenceId(Integer conferenceId) {
            LambdaQueryWrapper<Topics> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Topics::getConferenceId, conferenceId);
            return this.list(queryWrapper);
        }

        public Integer getPCMemberCountAtTopic(Integer conferenceId, Integer TopicId) {
            LambdaQueryWrapper<Topics> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Topics::getConferenceId, conferenceId);
            queryWrapper.eq(Topics::getId, TopicId);
            return this.list(queryWrapper).size();
        }
}
