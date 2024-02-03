package com.micro.paperserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.paperserve.sys.entity.PaperTopics;

import java.util.List;

public interface IPaperTopicsService extends IService<PaperTopics> {
    public List<Integer> getAllPapersIdByTopicId(Integer topicId);
}
