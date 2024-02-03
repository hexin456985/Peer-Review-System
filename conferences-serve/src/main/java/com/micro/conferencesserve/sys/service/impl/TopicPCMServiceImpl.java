package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.conferencesserve.sys.entity.TopicPCM;
import com.micro.conferencesserve.sys.mapper.TopicPCMMapper;
import com.micro.conferencesserve.sys.service.ITopicPCMService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicPCMServiceImpl extends ServiceImpl<TopicPCMMapper, TopicPCM> implements ITopicPCMService {
    @Resource
    private TopicPCMMapper topicPCMMapper;

    public List<Integer> getAllPCMemberIdByTopicId(Integer topicId){
        LambdaQueryWrapper<TopicPCM> queryWrapper = new LambdaQueryWrapper<>();
        log.debug("getTopicId..." + topicId);
        queryWrapper.eq(TopicPCM::getTopicId, topicId);
        List<TopicPCM> topicPCMS = topicPCMMapper.selectList(queryWrapper);
        log.debug("topicpcmembers: " + topicPCMS.toString());
        return this.list(queryWrapper).stream().map(TopicPCM::getPcMemberId).collect(Collectors.toList());
    }

}
