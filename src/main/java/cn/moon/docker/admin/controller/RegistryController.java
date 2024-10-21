package cn.moon.docker.admin.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.ws.rs.GET;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("registry")
public class RegistryController extends BaseCURDController<Registry> {

    @Resource
    RegistryService service;

    @GetMapping({"options"})
    public AjaxResult options() {
        List<Registry> list = service.findAll(Sort.by(Sort.Direction.DESC,Registry.Fields.defaultRegistry));
        List<Option> options = list.stream().map(r -> {
            String label = r.getFullUrl();
            if (r.getDefaultRegistry() != null && r.getDefaultRegistry()) {
                label += " (默认)";
            }
            String value = r.getId();
            return Option.builder().label(label).value(value).build();
        }).collect(Collectors.toList());


        return AjaxResult.ok().data(options);
    }


}

