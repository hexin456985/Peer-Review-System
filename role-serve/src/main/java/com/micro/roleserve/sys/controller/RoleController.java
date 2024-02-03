package com.micro.roleserve.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.micro.common.Result;
import com.micro.roleserve.sys.entity.Role;
import com.micro.roleserve.sys.service.IRoleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author laocai
 * @since 2023-02-07
 */
@Api(tags = {"角色接口列表"})
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;


    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable("id") Integer id) {
        Role role = roleService.getRoleById(id);
        return Result.success(role);
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> getRoleList(@RequestParam(value = "name", required = false) String roleName,
                                                   @RequestParam(value = "pageNo") Long pageNo,
                                                   @RequestParam(value = "pageSize") Long pageSize) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        // rolename不为空或null则添加查找条件
        wrapper.eq(StringUtils.hasLength(roleName), Role::getName, roleName);
        // wrapper.orderByDesc(Role::getId);

        Page<Role> page = new Page<>(pageNo, pageSize);
        roleService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("total", page.getTotal());
        data.put("rows", page.getRecords());

        return Result.success(data);
    }

    @PostMapping
    public Result<?> addRole(@RequestBody Role role) {
        roleService.addRole(role);
        return Result.success("新增角色成功");
    }

    @PutMapping
    public Result<?> updateRole(@RequestBody Role role) {
        roleService.updateRole(role);
        return Result.success("修改角色成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteRoleById(@PathVariable("id") Integer id) {
        roleService.deleteRoleById(id);
        return Result.success("删除角色成功");
    }

    @GetMapping("/all")
    public Result<List<Role>> getAllRole() {
        List<Role> roleList = roleService.list();
        return Result.success(roleList);
    }
}
