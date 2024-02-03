package com.micro.paperserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.paperserve.sys.entity.PaperTopics;
import com.micro.paperserve.sys.mapper.PaperTopicsMapper;
import com.micro.paperserve.sys.service.IPaperTopicsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaperTopicsServiceImpl extends ServiceImpl<PaperTopicsMapper, PaperTopics> implements IPaperTopicsService {
    public List<Integer> getAllPapersIdByTopicId(Integer topicId){
        LambdaQueryWrapper<PaperTopics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperTopics::getTopicId, topicId);
        return this.list(queryWrapper).stream().map(PaperTopics::getPaperId).collect(Collectors.toList());
    }

}
