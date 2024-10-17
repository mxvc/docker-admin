package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import cn.moon.lang.web.Option;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.persistence.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.HasPermission;
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
@RequestMapping("api/registry")
public class RegistryController {

    @Resource
    private RegistryService service;


    @HasPermission("registry:list")
    @RequestMapping("list")
    public Page<Registry> list(@PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Registry> list = service.findAll(pageable);

        return list;
    }

    @RequestMapping("save")
    public Result save(@RequestBody Registry registry) {
        service.save(registry);
        return AjaxResult.ok().msg("保存");
    }

    @RequestMapping("update")
    public Result update(@RequestBody Registry u) {
        Registry db = service.findOne(u.getId());

        service.save(u);
        return AjaxResult.ok().msg("保存");
    }


    @RequestMapping("delete")
    public Result delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


    @RequestMapping("options")
    public Result options() {
        List<Option> list = new ArrayList<>();

        List<Registry> values = service.findAll();

        for (Registry t : values) {
            list.add( Option.valueLabel(t.getId(), t.getFullUrl()));
        }

        return AjaxResult.ok().data( list);
    }


}
