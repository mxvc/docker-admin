package io.github.mxvc.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import io.github.mxvc.docker.admin.entity.DockerCompose;
import io.github.mxvc.docker.admin.entity.DockerComposeServiceItem;
import io.github.mxvc.docker.admin.service.DockerComposeService;
import io.github.mxvc.docker.admin.service.DockerComposeServiceItemService;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("dockerComposeServiceItem")
public class DockerComposeServiceItemController {

    @Resource
    DockerComposeServiceItemService service;

    @Resource
    DockerComposeService dockerComposeService;


    @HasPermission
    @PostMapping({"save"})
    public AjaxResult save(@RequestBody AddParam input, RequestBodyKeys updateFields) throws Exception {
        DockerCompose dockerCompose = dockerComposeService.findOne(input.pid);

        String name = StrUtil.subAfter(input.getImageUrl(), "/", true);

        DockerComposeServiceItem item = new DockerComposeServiceItem();
        item.setPid(input.getPid());
        item.setImage(input.getImageUrl() + ":" + input.getImageTag());
        item.setName(name);
        item.setContainerName(service.getContainerName(dockerCompose, item));

        this.service.saveOrUpdate(item, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }

    @GetMapping("deploy")
    public AjaxResult deploy(String id, String tag) throws InterruptedException {
        service.deploy(id, tag);
        return AjaxResult.ok().msg("部署完成");
    }

    @GetMapping("delete")
    public AjaxResult delete(String id) throws IOException, InterruptedException {
        service.delete(id);
        return AjaxResult.ok();
    }




    @Data
    public static class AddParam {
        String pid;
        String imageUrl;
        String imageTag;

    }

}

