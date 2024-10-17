package cn.moon.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.base.Role;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.entity.User;
import cn.moon.docker.admin.service.UserService;
import cn.moon.lang.web.Option;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.persistence.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.HasPermission;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/user")
@RequiresRoles("admin")
public class UserController {


    @Resource
    private UserService service;




    @HasPermission("user:list")
    @RequestMapping("list")
    public Page<User> list(@PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable, Host host) {
        Page<User> list = service.findAll(pageable);

        return list;
    }


    @RequestMapping("save")
    public Result save(@RequestBody User user) {

        service.save(user);
        return Result.ok().msg("保存");
    }

    @RequestMapping("update")
    public Result update(@RequestBody User input) {
        User db = service.findOne(input.getId());
        if (StrUtil.isEmpty(input.getPassword())) {
            input.setPassword(db.getPassword());
        }

        service.save(input);
        return Result.ok().msg("保存");
    }


    @RequestMapping("delete")
    public Result delete(String id) {
        service.deleteById(id);
        return Result.ok().msg("删除成功");
    }


    @RequestMapping("roleOptions")
    public Result roleOptions() {
        List<Option> list = new ArrayList<>();

        Role[] values = Role.values();

        for (Role role : values) {
            list.add( Option.valueLabel(role.name(), role.getLabel()));
        }

        return Result.ok().data(list);
    }


}
