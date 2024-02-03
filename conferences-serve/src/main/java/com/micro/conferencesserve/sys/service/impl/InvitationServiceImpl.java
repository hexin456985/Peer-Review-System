package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.conferencesserve.sys.entity.Invitation;
import com.micro.conferencesserve.sys.entity.pcmembers;
import com.micro.conferencesserve.sys.mapper.InvitationMapper;
import com.micro.conferencesserve.sys.mapper.PCMembersMapper;
import com.micro.conferencesserve.sys.service.IInvitationService;
import com.micro.userroleserve.proto.UserRoleRequest;
import com.micro.userroleserve.proto.UserRoleResponse;
import com.micro.userroleserve.proto.UserRoleServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author x
 * @since 2023-10-28
 */
@Service
public class InvitationServiceImpl extends ServiceImpl<InvitationMapper, Invitation> implements IInvitationService {
    @Autowired
    private InvitationMapper invitationMapper;

    @Resource
    private PCMembersMapper pcMembersMapper;

    @GrpcClient("user-serve")
    UserRoleServiceGrpc.UserRoleServiceBlockingStub synchronousUserRoleClient;

    @Override
    public List<Invitation> getInvitationIdByUserId(Integer userId) {
        return invitationMapper.getInvitationsByUserId(userId);
    }

    @Override
    public void replyInvitation(Integer invitationId, Integer conferenceId, String action) {
        Invitation queryInvitation = getById(invitationId);

        // 2.修改状态
        UpdateWrapper<Invitation> updateWrapper = new UpdateWrapper<>();
        int status = action.equals("accept") ? 2 : 3; // 2-接受， 3-拒绝
        updateWrapper.lambda().eq(Invitation::getInvitationId, invitationId).set(Invitation::getStatus, status);
        update(updateWrapper);

        // 3. 调用 gRPC 服务检查用户角色
        if (action.equals("accept")) {
            UserRoleRequest userRoleRequest = UserRoleRequest.newBuilder()
                    .setUserId(queryInvitation.getUserId())
                    .setRoleId(4)
                    .build();
            UserRoleResponse userRoleResponse = synchronousUserRoleClient.checkUserRole(userRoleRequest);

            UserRoleRequest userRoleRequest2 = UserRoleRequest.newBuilder()
                    .setUserId(queryInvitation.getUserId())
                    .setRoleId(3)
                    .build();
            UserRoleResponse userRoleResponse2 = synchronousUserRoleClient.checkUserRole(userRoleRequest2);
        }
    }
}

