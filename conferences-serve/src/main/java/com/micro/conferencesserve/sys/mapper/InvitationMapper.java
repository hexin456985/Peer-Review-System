package com.micro.conferencesserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.conferencesserve.sys.entity.Invitation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-10-28
 */
@Mapper
public interface InvitationMapper extends BaseMapper<Invitation> {
    public List<Invitation> getInvitationsByUserId(@Param("userId") Integer userId);
}