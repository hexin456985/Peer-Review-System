package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.conferencesserve.sys.entity.TopicPcmember;
import com.micro.conferencesserve.sys.mapper.TopicPcmemberMapper;
import com.micro.conferencesserve.sys.service.ITopicPcmemberService;
import org.springframework.stereotype.Service;

@Service
public class TopicPcmemberServiceImpl extends ServiceImpl<TopicPcmemberMapper, TopicPcmember> implements ITopicPcmemberService {
}
