package com.micro.userserve.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.userserve.sys.entity.Menu;
import com.micro.userserve.sys.mapper.MenuMapper;
import com.micro.userserve.sys.service.IMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public List<Menu> getAllMenu() {
        // 一级菜单
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, 0);
        List<Menu> menuList = this.list(wrapper);
        // 填充子菜单
        setMenuChildren(menuList);
        return menuList;
    }

    @Override
    public List<Menu> getMenuListByUserId(Integer userId) {
        // 一级菜单
        List<Menu> menuList = menuMapper.getMenuListByUserId(userId, 0);
        // 子菜单
        setMenuChildrenByUserId(userId, menuList);
        menuList.sort(Comparator.comparing(Menu::getMenuId));
        return menuList;
    }

    private void setMenuChildrenByUserId(Integer userId, List<Menu> menuList) {
        if (menuList != null) {
            for (Menu menu: menuList) {
                List<Menu> subMenuList = menuMapper.getMenuListByUserId(userId, menu.getMenuId());
                log.warn(subMenuList.toString());
                menu.setChildren(subMenuList);
                // 递归
                setMenuChildrenByUserId(userId, subMenuList);
            }
        }
    }

    private void setMenuChildren(List<Menu> menuList) {
        if (menuList != null) {
            for (Menu menu: menuList) {
                LambdaQueryWrapper<Menu> subWrapper = new LambdaQueryWrapper<>();
                // 因为子菜单的parentId是当前菜单的id。因此查找时id = menu.getMenuId()
                subWrapper.eq(Menu::getParentId, menu.getMenuId());
                List<Menu> subMenuList = this.list(subWrapper);
                menu.setChildren(subMenuList);
                // 递归查找
                setMenuChildren(subMenuList);
            }
        }
    }
}
