package cn.moon.docker.admin.controller;

import cn.moon.base.shiro.CurrentUser;
import cn.moon.docker.admin.entity.Host;
import cn.moon.docker.admin.service.HostService;
import cn.moon.docker.admin.vo.DockerInfo;
import cn.moon.lang.web.Option;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.persistence.BaseEntity;
import cn.moon.lang.web.persistence.Query;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("api/host")
public class HostController {


    @Resource
    private HostService service;

    @RequiresPermissions("host:list")
    @RequestMapping("list")
    public Page<Host> list(@PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Query<Host> q = getHostQuery();

        Page<Host> list = service.findAll(q, pageable);

        return list;
    }

    @NotNull
    private static Query<Host> getHostQuery() {
        Query<Host> q = new Query<>();
        Subject subject = SecurityUtils.getSubject();
        if (!subject.hasRole("admin")) {
            CurrentUser user = (CurrentUser) subject.getPrincipal();
            Set<String> perms = user.getDataPerms().get("H");
            q.in("id", perms);
        }
        return q;
    }

    @RequestMapping("save")
    public Result update(@RequestBody Host host) {
        service.save(host);
        return Result.ok().msg("保存成功");
    }


    @RequestMapping("delete")
    public Result delete(@RequestBody Host host) {
        service.deleteById(host.getId());
        return Result.ok().msg("删除成功");
    }


    @RequestMapping("options")
    public List<Option> options() {
        Query<Host> q = getHostQuery();
        List<Host> list = service.findAll(q, Sort.by(Host.Fields.name));
        List<Option> options = new ArrayList<>();
        for (Host h : list) {
            options.add(Option.valueLabel(h.getId(), h.getName()));
        }
        return options;
    }

    @RequestMapping("get")
    public Map<String, Object> get(String id) {
        Host host = service.findOne(id);
        Info info = service.getDockerInfo(host);

        DockerInfo dockerInfo = new DockerInfo();
        BeanUtils.copyProperties(info, dockerInfo);


        Map<String, Object> result = new HashMap<>();
        result.put("host", host);
        result.put("info", dockerInfo);

        return result;
    }


    @RequestMapping("containers")
    public List<Container> containers(String id) {
        return service.getContainers(id);
    }

    @RequestMapping("images")
    public List<Image> images(String id) {
        return service.getImages(id);
    }

    @RequestMapping("deleteImage")
    public Result deleteImage(String id, String imageId) {
        try {
            service.deleteImage(id, imageId);
        } catch (ConflictException e) {
            return Result.err().msg("删除镜像失败" + e.getMessage());
        }
        return Result.ok();
    }

}
