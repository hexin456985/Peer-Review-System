
package com.micro.userserve.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.micro.userserve.sys.entity.Menu;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> getAllMenu();

    List<Menu> getMenuListByUserId(Integer userId);
}
