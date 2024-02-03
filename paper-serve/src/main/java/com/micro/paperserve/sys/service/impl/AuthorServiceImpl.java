package com.micro.paperserve.sys.service.impl;


import org.springframework.stereotype.Service;
import com.micro.paperserve.sys.entity.Author;
import com.micro.paperserve.sys.service.IAuthorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.paperserve.sys.mapper.AuthorMapper;


@Service
public class AuthorServiceImpl extends ServiceImpl<AuthorMapper, Author> implements IAuthorService {

}