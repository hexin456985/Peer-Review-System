package com.micro.conferencesserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.conferencesserve.sys.entity.pcmembers;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2023-11-29
 */
@Mapper
public interface PCMembersMapper extends BaseMapper<pcmembers> {
    @Select("SELECT LAST_INSERT_ID()")
    Integer getLastInsertPcId();
}
