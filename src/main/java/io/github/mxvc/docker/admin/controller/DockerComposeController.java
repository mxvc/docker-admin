package io.github.mxvc.docker.admin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import io.github.mxvc.docker.admin.entity.App;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.github.mxvc.docker.admin.entity.Project;
import io.github.mxvc.docker.admin.entity.converter.DockerComposeConverter;
import io.github.mxvc.docker.admin.service.DockerComposeServiceItemService;
import io.tmgg.lang.obj.AjaxResult;
import io.github.mxvc.docker.admin.service.DockerComposeService;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.persistence.BaseController;


import io.tmgg.web.persistence.specification.JpaQuery;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;


import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("dockerCompose")
public class DockerComposeController {

    @Resource
    DockerComposeService service;

    @Resource
    DockerComposeServiceItemService dockerComposeServiceItemService;

    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(Map<String, Object> param, String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<DockerCompose> q = new JpaQuery<>();

        q.searchText(searchText, service.getSearchableFields());
        q.searchMap(param,service.getFields());

        Subject subject = SecurityUtils.getSubject();
        q.addSubOr(qq->{
            qq.isNull("sysOrg.id");
            qq.in("sysOrg.id", subject.getOrgPermissions());
        });



        Page<DockerCompose> page = service.findAll(q, pageable);


        return service.autoRender(page);
    }

    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody DockerCompose input, RequestBodyKeys updateFields) throws Exception {
        service.saveOrUpdate(input, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }

    @GetMapping("get")
    public AjaxResult get(String id){
        DockerCompose one = service.findOne(id);

        return AjaxResult.ok().data(one);
    }

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
        Assert.hasText(id,"id不能为空");
        List<DockerComposeServiceItem> list = dockerComposeServiceItemService.findByPid(id);


        return AjaxResult.ok().data(list);
    }

    @GetMapping("servicesStatus")
    public AjaxResult servicesStatus(String id) throws IOException {
        Assert.hasText(id,"id不能为空");
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

