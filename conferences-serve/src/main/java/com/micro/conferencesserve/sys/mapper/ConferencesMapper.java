package com.micro.conferencesserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.conferencesserve.sys.entity.Conferences;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 
 * @since 2023-10-24
 */

@Mapper
public interface ConferencesMapper extends BaseMapper<Conferences> {

    void updateConferencesStatus(Integer conferenceID,String action);

    @Select("SELECT LAST_INSERT_ID()")
    Integer getLastInsertId();
}
