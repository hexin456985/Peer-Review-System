package com.micro.userserve.sys.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.protobuf.InvalidProtocolBufferException;
import com.micro.userserve.sys.service.impl.UserRoleServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
@Controller
@RequestMapping("/userRole")
@Slf4j
public class UserRoleController {
//    @Autowired
//    private UserRoleServiceImpl userRoleService;

//    @GetMapping("/")
//    public String getUser() throws InvalidProtocolBufferException {
//
//        log.debug(userRoleService.getUserById(1).getName());
//        return null;
//    }
}
