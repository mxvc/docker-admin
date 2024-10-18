package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("registry")
public class RegistryController {

    @Resource
    private RegistryService service;


    @HasPermission("registry:list")
    @RequestMapping("list")
    public Page<Registry> list(@PageableDefault(sort = BaseEntity.Fields.updateTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Registry> list = service.findAll(pageable);

        return list;
    }

    @RequestMapping("save")
    public AjaxResult save(@RequestBody Registry registry) {
        service.save(registry);
        return AjaxResult.ok().msg("保存");
    }

    @RequestMapping("update")
    public AjaxResult update(@RequestBody Registry u) {
        Registry db = service.findOne(u.getId());

        service.save(u);
        return AjaxResult.ok().msg("保存");
    }


    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


    @RequestMapping("options")
    public AjaxResult options() {
        List<Option> list = new ArrayList<>();

        List<Registry> values = service.findAll();

        for (Registry t : values) {
            list.add( new Option(t.getFullUrl(), t.getId()));
        }

        return AjaxResult.ok().data( list);
    }


}
