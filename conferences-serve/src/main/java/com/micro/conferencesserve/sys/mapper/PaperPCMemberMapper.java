package com.micro.conferencesserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.conferencesserve.sys.entity.PaperPCMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-11-30
 */
@Mapper
public interface PaperPCMemberMapper extends BaseMapper<PaperPCMember> {
    public boolean find(Integer paperId, Integer pcMemberId);
}
