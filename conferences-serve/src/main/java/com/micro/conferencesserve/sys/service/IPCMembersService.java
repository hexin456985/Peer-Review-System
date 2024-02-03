package com.micro.conferencesserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.conferencesserve.sys.entity.pcmembers;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2023-11-29
 */
public interface IPCMembersService extends IService<pcmembers> {
    public Integer getPCMemberCount(Integer conferenceId);

    public Integer getLastInsertId();

    List<Integer> getConferenceListByPcmemberId(Integer pcmemberId);
}
