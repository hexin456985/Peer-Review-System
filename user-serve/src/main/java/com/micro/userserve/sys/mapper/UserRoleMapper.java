package com.micro.userserve.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.micro.userserve.sys.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-02-07
 */
@Mapper
@Repository
public interface UserRoleMapper extends BaseMapper<UserRole> {
    public static UserRole getUserRoleById(Integer userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserRoleById'");
    }
}
