package com.micro.conferencesserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.micro.conferencesserve.sys.entity.pcmembers;
import com.micro.conferencesserve.sys.mapper.PCMembersMapper;
import com.micro.conferencesserve.sys.service.IPCMembersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2023-11-29
 */
@Service
public class PCMembersServiceImpl extends ServiceImpl<PCMembersMapper, pcmembers> implements IPCMembersService {
    @Resource
    private PCMembersMapper pcMembersMapper;

    public Integer getPCMemberCount(Integer conferenceId) {
        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pcmembers::getConferenceID, conferenceId);

        return this.list(queryWrapper).size();
    }

    public Integer getLastInsertId() {
        return pcMembersMapper.getLastInsertPcId();
    }

    @Override
    public List<Integer> getConferenceListByPcmemberId(Integer userId) {
        LambdaQueryWrapper<pcmembers> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(pcmembers::getUserId, userId);
        List<pcmembers> pcmembersList = pcMembersMapper.selectList(queryWrapper);
        List<Integer> conferencesIdList = pcmembersList.stream()
                .map(pcmembers::getConferenceID)
                .collect(Collectors.toList());
        return conferencesIdList;
    }
}
