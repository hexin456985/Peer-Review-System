package com.micro.paperserve.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.micro.paperserve.sys.entity.Papers;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2023-11-06
 */
public interface IPapersService extends IService<Papers> {
    //List<Papers> findAll();
    List<Integer> getAllPapersIdByConferenceId(Integer conferenceId);
    List<String> getAllPapersStatusByConferenceId(Integer conferenceId);
//    List<Integer> getAllPapersReviewByConferenceId(Integer conferenceId);
}
