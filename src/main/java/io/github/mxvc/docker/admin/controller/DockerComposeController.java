package io.github.mxvc.docker.admin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.github.mxvc.docker.admin.entity.converter.DockerComposeConverter;
import io.github.mxvc.docker.admin.service.DockerComposeServiceItemService;
import io.tmgg.lang.obj.AjaxResult;
import io.github.mxvc.docker.admin.service.DockerComposeService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.persistence.BaseController;


import lombok.Data;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dockerCompose")
public class DockerComposeController  extends BaseController<DockerCompose>{

    @Resource
    DockerComposeService service;

    @Resource
    DockerComposeServiceItemService dockerComposeServiceItemService;



    @GetMapping("get")
    public AjaxResult get(String id){
        DockerCompose one = service.findOne(id);

        return AjaxResult.ok().data(one);
    }

    @Override
    @HasPermission
    @RequestMapping({"delete"})
    public AjaxResult delete(String id) {
        List<DockerComposeServiceItem> items = dockerComposeServiceItemService.findByPid(id);
        Assert.state(CollUtil.isEmpty(items), "请先删除容器");
        this.service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


    @GetMapping("services")
    public AjaxResult services(String id) throws IOException {
        List<DockerComposeServiceItem> list = dockerComposeServiceItemService.findByPid(id);


        return AjaxResult.ok().data(list);
    }

    @GetMapping("servicesStatus")
    public AjaxResult servicesStatus(String id) throws IOException {
        Map<String, Object> map = dockerComposeServiceItemService.servicesStatus(id);


        return AjaxResult.ok().data(map);
    }



    @GetMapping("configFile")
    public AjaxResult configFile(String id){
        List<DockerComposeServiceItem> items = dockerComposeServiceItemService.findByPid(id);

        return AjaxResult.ok().data(DockerComposeConverter.toConfigFile(items));
    }



    @PostMapping("saveConfigFile")
    public AjaxResult saveConfigFile(@RequestBody SaveConfigParam param) throws IOException {
        String id = param.getId();
        String content = param.getContent();


        service.saveConfigFile(id,content);

        return AjaxResult.ok().msg("保存配置文件成功,需手动点击部署");
    }

    @PostMapping("moveApp")
    public AjaxResult moveApp(@RequestBody MoveAppParam param) throws IOException {
        String id = param.getId();

        service.moveApp(id,param.getApp());

        return AjaxResult.ok().msg("移动应用成功");
    }


    @Data
    public static class SaveConfigParam {
        String id;
        String content;
    }


    @Data
    public static class MoveAppParam {
        String id;
        String app;
    }
}

