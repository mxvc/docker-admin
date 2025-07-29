package io.github.mxvc.docker.admin.controller;

import cn.hutool.setting.yaml.YamlUtil;
import io.github.mxvc.base.tool.YamlTool;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.tmgg.lang.obj.AjaxResult;
import io.github.mxvc.docker.admin.service.DockerComposeService;
import io.tmgg.web.persistence.BaseController;


import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("dockerCompose")
public class DockerComposeController  extends BaseController<DockerCompose>{

    @Resource
    DockerComposeService service;



    @GetMapping("get")
    public AjaxResult get(String id){
        DockerCompose one = service.findOne(id);


        return AjaxResult.ok().data(one);
    }


    @GetMapping("services")
    public AjaxResult services(String id) throws IOException {
        DockerCompose one = service.findOne(id);

        List<DockerComposeServiceItem> items = DockerComposeServiceItem.load(one.getContent());

        return AjaxResult.ok().data(items);
    }



    @GetMapping("deploy")
    public AjaxResult deploy(String id,String name) throws IOException, InterruptedException {
        service.deploy(id, name);
        return AjaxResult.ok();
    }


}

