package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.vo.ContainerVo;
import cn.moon.docker.admin.entity.App;
import cn.moon.docker.admin.service.AppService;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping(value = "api/app")
public class AppController {


    @Resource
    private AppService service;


    @HasPermission("app:list")
    @RequestMapping("list")
    public Page<App> list(String keyword, @PageableDefault(sort = "updateTime", direction = Sort.Direction.DESC) Pageable pageable, HttpSession session) {
        JpaQuery<App> q = new JpaQuery<>();
        q.like("name", keyword);


        Subject subject = SecurityUtils.getSubject();
        if(!subject.hasRole("admin")){
            CurrentUser user = (CurrentUser) subject.getPrincipal();
            Set<String> perms = user.getDataPerms().get("A");
            q.in("id", perms);
        }

        Page<App> list = service.findAll(q, pageable);
        return list;
    }

    @RequestMapping("get")
    public App view(String id) throws UnsupportedEncodingException {
        App app = service.findOne(id);

        if(app.getImageUrl() == null){
            String fullUrl = app.getProject().getRegistry().getFullUrl();
            app.setImageUrl(fullUrl +"/" + app.getProject().getName());
        }

        String url = LogUrlTool.getLogViewUrl(id);
        app.setLogUrl(url);
        return app;
    }

    @RequestMapping("container")
    public Result container(String id) {
        App app = service.findOne(id);
        Assert.state(app != null, "应用不存在");
        ContainerVo container = service.getContainerVo(app);


        return Result.ok().msg("获取容器信息成功").data(container);
    }


    @HasPermission("app:save")
    @RequestMapping("save")
    public Result save(@RequestBody App app) {
         service.save(app);
        return Result.ok().msg("保存成功");
    }


    @HasPermission("app:save")
    @RequestMapping("update")
    public Result update(@RequestBody App project) {
        service.save(project);
        return Result.ok().msg("修改成功");
    }

    @HasPermission("app:config")
    @RequestMapping("updateConfig")
    public Result updateConfig(String id, @RequestBody App.AppConfig appConfig) {
        App app = service.updateConfig(id, appConfig);
        service.deploy(app);

        return Result.ok().msg("修改成功，应用会自动重启").data(app);
    }

    @HasPermission("app:update")
    @RequestMapping("updateVersion")
    public Result updateVersion(String id, String version) {
        service.updateAppVersion(id, version);

        return Result.ok().msg("更新指定已发布");
    }


    @HasPermission("app:delete")
    @RequestMapping("delete")
    public Result delete(String id, Boolean force)  {
        if (force != null && force) {
            service.deleteById(id);
            return Result.ok().msg("强制删除数据成功");
        }

        try {
            service.deleteApp(id);
        } catch (Exception e) {
            log.error("删除应用失败", e);

            return Result.err().msg("删除失败");
        }


        return Result.ok();
    }


    @HasPermission("app:deploy")
    @RequestMapping("deploy/{id}")
    public Result deploy(@PathVariable String id) {
        log.info("开始部署");
        App app = service.findOne(id);

        service.deploy(app);
        log.info("部署指令已发送");
        return Result.ok();
    }


    @HasPermission("app:deploy")
    @RequestMapping("autoDeploy")
    public Result autoDeploy(String id, boolean autoDeploy) throws InterruptedException {

        App db = service.findOne(id);
        db.setAutoDeploy(autoDeploy);

        service.save(db);


        Result rs = Result.ok().msg("自动部署:" + (autoDeploy ? "启用" : "停用"));
        return rs;
    }


    @HasPermission("app:start")
    @RequestMapping("autoRestart")
    public Result autoRestart(String id, boolean autoRestart) throws InterruptedException {

        App db = service.findOne(id);
        db.setAutoRestart(autoRestart);
        db.getConfig().setRestart(autoRestart);

        service.save(db);
        service.deploy(db);


        Result rs = Result.ok().msg("自动重启:" + (autoRestart ? "启用" : "停用"));
        return rs;
    }



    @HasPermission("app:start")
    @RequestMapping("start/{appId}")
    public Result start(@PathVariable String appId) {
        service.start(appId);
        return Result.ok().msg("启动指令已发送");
    }

    @HasPermission("app:stop")
    @RequestMapping("stop/{appId}")
    public Result stop(@PathVariable String appId) {
        service.stop(appId);
        return Result.ok().msg("停止指令已发送");
    }

    @HasPermission("app:config")
    @RequestMapping("rename")
    public Result rename(@RequestBody Map<String, String> map) {
        String appId = map.get("appId");
        String newName = map.get("newName");
        Assert.hasText(appId, "appId不能为空");
        Assert.hasText(newName, "新名称不能为空");
        App app = service.rename(appId, newName);


        return Result.ok().msg("部署指令已发送").data(app);
    }


    @RequestMapping("options")
    public Result options() {
        List<Option> list = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, BaseEntity.Fields.modifyTime);

        List<App> all = service.findAll(sort);

        for (App app : all) {
            list.add(Option.valueLabel(app.getId(), app.getName()));
        }

        return Result.ok().data(list);
    }



}
