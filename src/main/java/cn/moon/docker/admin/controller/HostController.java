package cn.moon.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.service.HostService;
import cn.moon.docker.admin.vo.DockerInfo;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import io.tmgg.lang.dao.BaseCURDController;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("host")
public class HostController extends BaseCURDController<Host> {


    @Resource
    private HostService service;



    @RequestMapping("options")
    public List<Option> options(@RequestParam(defaultValue = "false") boolean onlyRunner) {
        JpaQuery<Host> q = new JpaQuery<>();
        if(onlyRunner){
            q.eq(Host.Fields.isRunner, true);
        }
        List<Host> list = service.findAll(q, Sort.by(Host.Fields.name));
        List<Option> options = new ArrayList<>();
        for (Host h : list) {
            if(onlyRunner && !h.getIsRunner() ){
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

    @RequestMapping("deleteImage")
    public AjaxResult deleteImage(String id, String imageId) {
        try {
            service.deleteImage(id, imageId);
        } catch (ConflictException e) {
            return AjaxResult.err().msg("删除镜像失败" + e.getMessage());
        }
        return AjaxResult.ok();
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
            service.syncImageToHost(hostId, url, tag ,newName);
        } catch (Exception e) {
            return AjaxResult.err().msg("拉取镜像失败" + e.getMessage());
        }
        return AjaxResult.ok().msg("拉取镜像成功");
    }


}
