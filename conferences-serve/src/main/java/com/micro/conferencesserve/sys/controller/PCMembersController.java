package com.micro.conferencesserve.sys.controller;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.common.Result;
import com.micro.common.utils.JwtUtil;
import com.micro.conferencesserve.sys.mapper.PCMembersMapper;
import com.micro.conferencesserve.sys.service.impl.PCMembersServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2023-11-29
 */
@Api(tags = {"PCMember接口列表"})
@RestController
@Slf4j
@RequestMapping("/pcMembers")
public class PCMembersController {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private PCMembersServiceImpl pcMembersService;

    @GetMapping("/conferences")
    public Result<Map<String, Object>> getConferencesByPcMember(@RequestParam("token") String token) {
        Integer userId = jwtUtil.getUserIdFromToken(token);

        List<Integer> conferenceIds = pcMembersService.getConferenceListByPcmemberId(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("conferenceIds", conferenceIds);
        return Result.success(data);
    }
}
