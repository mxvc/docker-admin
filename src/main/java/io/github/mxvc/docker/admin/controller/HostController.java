package io.github.mxvc.docker.admin.controller;

import io.github.mxvc.docker.admin.entity.Host;
import io.github.mxvc.docker.admin.service.HostService;
import io.github.mxvc.docker.admin.vo.DockerInfo;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.persistence.BaseController;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("host")
public class HostController extends BaseController<Host> {


    @Resource
    private HostService service;


    @RequestMapping("options")
    public List<Option> options(@RequestParam(defaultValue = "false") boolean onlyRunner, String searchText) {
        JpaQuery<Host> q = new JpaQuery<>();
        if (onlyRunner) {
            q.eq(Host.Fields.isRunner, true);
        }
        q.searchText(searchText, Host.Fields.name, Host.Fields.remark, Host.Fields.dockerHost, Host.Fields.dockerHostHeader);
        List<Host> list = service.findAll(q, Sort.by(Host.Fields.name));
        List<Option> options = new ArrayList<>();
        for (Host h : list) {
            if (onlyRunner && !h.getIsRunner()) {
                continue;
            }
            options.add(Option.builder().label(h.getName()).value(h.getId()).build());
        }
        return options;
    }

    @RequestMapping("get")
    public Host get(String id) {
        Host host = service.findOne(id);

        return host;
    }

    @RequestMapping("runtime/get")
    public AjaxResult runtime(String id) {
        Host host = service.findOne(id);

        Info info = service.getDockerInfo(host);

        DockerInfo dockerInfo = new DockerInfo();
        BeanUtils.copyProperties(info, dockerInfo);

        return AjaxResult.ok().data(dockerInfo);
    }


    @RequestMapping("containers")
    public AjaxResult containers(String id) {
        return AjaxResult.ok().data(service.getContainers(id));
    }

    @RequestMapping("images")
    public List<Image> images(String id) {
        return service.getImages(id);
    }

    @PostMapping("deleteImage")
    public AjaxResult deleteImage(String id, String imageId) {
        try {
            service.deleteImage(id, imageId);
        } catch (ConflictException e) {
            return AjaxResult.err().msg("删除镜像失败" + e.getMessage());
        }
        return AjaxResult.ok().msg("删除镜像" + imageId + "成功");
    }

    @RequestMapping("cleanImage")
    public AjaxResult cleanImage(String id) {
        try {
            service.cleanImage(id);
        } catch (Exception e) {
            return AjaxResult.err().msg("清理镜像失败" + e.getMessage());
        }
        return AjaxResult.ok();
    }

    @PostMapping("syncImageToHost")
    public AjaxResult syncImageToHost(@NotNull String hostId, @NotNull String url, @NotNull String tag, String newName) {
        try {
            service.syncImageToHost(hostId, url, tag, newName);
        } catch (Exception e) {
            return AjaxResult.err().msg("拉取镜像失败" + e.getMessage());
        }
        return AjaxResult.ok().msg("拉取镜像成功");
    }

    @ExceptionHandler(Exception.class)
    public AjaxResult exception(Exception e){
        log.error(e.getMessage());
        return AjaxResult.err("连接容器引擎失败");
    }

}
