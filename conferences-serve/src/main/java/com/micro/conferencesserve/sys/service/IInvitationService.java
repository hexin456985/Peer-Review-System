package com.micro.conferencesserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.conferencesserve.sys.entity.Invitation;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author x
 * @since 2023-10-28
 */
public interface IInvitationService extends IService<Invitation> {
    public List<Invitation> getInvitationIdByUserId(Integer userId);

    public void replyInvitation(Integer invitationId, Integer conferenceId, String action);
}
