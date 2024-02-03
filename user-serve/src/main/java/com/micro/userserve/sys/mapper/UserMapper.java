package com.micro.userserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.userserve.sys.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 作者
 * @since 2023-09-20
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    public List<String> getRoleNameByUserId(Integer userId);
}
