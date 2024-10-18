package cn.moon.docker.admin.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import cn.moon.docker.admin.entity.Registry;
import cn.moon.docker.admin.service.RegistryService;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseEntity;
import jakarta.ws.rs.GET;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("registry")
public class RegistryController extends BaseCURDController<Registry> {

    @Resource
    RegistryService service;

    @GetMapping({"options"})
    public AjaxResult options() {
        List<Option> options = service.findOptionList(Registry::getFullUrl);

        return AjaxResult.ok().data(options);
    }


}

