package com.micro.paperserve.sys.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.paperserve.sys.entity.PaperAuthors;
import com.micro.paperserve.sys.mapper.PaperAuthorsMapper;
import com.micro.paperserve.sys.service.IPaperAuthorsService;

@Service
public class PaperAuthorsServiceImpl extends ServiceImpl<PaperAuthorsMapper, PaperAuthors> implements IPaperAuthorsService{
    
}
