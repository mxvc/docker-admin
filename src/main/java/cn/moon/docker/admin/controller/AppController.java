package cn.moon.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.entity.Project;
import cn.moon.docker.admin.vo.ContainerVo;
import cn.moon.docker.admin.entity.App;
import cn.moon.docker.admin.service.AppService;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.CommonQueryParam;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.io.UnsupportedEncodingException;
import java.util.*;


@RestController
@Slf4j
@RequestMapping(value = "app")
public class AppController {


    @Resource
    private AppService service;

    private JpaQuery<App> buildQuery() {
        JpaQuery<App> q = new JpaQuery<>();
        Subject subject = SecurityUtils.getSubject();
        q.or(qq->{
            qq.isNull("sysOrg.id");
            qq.in("sysOrg.id", subject.getOrgPermissions());
        });
        return q;
    }


    @HasPermission("app:list")
    @RequestMapping("list")
    public Page<App> list(@RequestBody CommonQueryParam param, @PageableDefault(sort = {"updateTime", "createTime"}, direction = Sort.Direction.DESC) Pageable pageable, HttpSession session) {
        long  start = System.currentTimeMillis();
        JpaQuery<App> q = buildQuery();
        String keyword = param.getKeyword();
        q.searchText(keyword, "name", "remark");

        Page<App> list = service.findAll(q, pageable);
        log.info("查询app列表，耗时：{}毫秒", System.currentTimeMillis() - start);
        return list;
    }

    @RequestMapping("get")
    public App view(HttpServletRequest request, String id) throws UnsupportedEncodingException {
        App app = service.findOne(id);

        if (app.getImageUrl() == null) {
            String fullUrl = app.getProject().getRegistry().getFullUrl();
            app.setImageUrl(fullUrl + "/" + app.getProject().getName());
        }

        String url = LogUrlTool.getLogViewUrl(request, id);
        app.setLogUrl(url);
        return app;
    }

    @RequestMapping("container")
    public AjaxResult container(String id) {
        App app = service.findOne(id);
        Assert.state(app != null, "应用不存在");
        ContainerVo container = service.getContainerVo(app);

        return AjaxResult.ok().data(container);
    }


    @HasPermission("app:save")
    @RequestMapping("save")
    public AjaxResult save(@RequestBody App app) {
        service.save(app);
        return AjaxResult.ok().msg("保存成功");
    }


    @HasPermission("app:save")
    @RequestMapping("update")
    public AjaxResult update(@RequestBody App app) {
        service.save(app);
        return AjaxResult.ok().msg("修改成功");
    }
    @HasPermission("app:save")
    @RequestMapping("updateBaseInfo")
    public AjaxResult updateBaseInfo(@RequestBody App app) {
        service.updateBaseInfo(app);
        return AjaxResult.ok().msg("修改成功");
    }


    @HasPermission(value = "app:config",label = "配置")
    @RequestMapping("updateConfig")
    public AjaxResult updateConfig(String id, @RequestBody App.AppConfig appConfig) {
        App app = service.updateConfig(id, appConfig);
        service.deploy(app);

        return AjaxResult.ok().msg("修改成功，应用会自动重启").data(app);
    }

    @HasPermission("app:update")
    @RequestMapping("updateVersion")
    public AjaxResult updateVersion(String id, String version) {
        service.updateAppVersion(id, version);

        return AjaxResult.ok().msg("更新指定已发布");
    }


    @HasPermission("app:delete")
    @RequestMapping("delete")
    public AjaxResult delete(String id, Boolean force) {
        if (force != null && force) {
            service.deleteById(id);
            return AjaxResult.ok().msg("强制删除数据成功");
        }

        try {
            service.deleteApp(id);
        } catch (Exception e) {
            log.error("删除应用失败", e);

            return AjaxResult.err().msg("删除失败");
        }

        return AjaxResult.ok();
    }


    @HasPermission("app:deploy")
    @RequestMapping("deploy/{id}")
    public AjaxResult deploy(@PathVariable String id) {
        log.info("开始部署");
        App app = service.findOne(id);

        service.deploy(app);
        log.info("部署指令已发送");
        return AjaxResult.ok();
    }


    @HasPermission(value = "app:deploy")
    @RequestMapping("autoDeploy")
    public AjaxResult autoDeploy(String id, boolean autoDeploy) throws InterruptedException {

        App db = service.findOne(id);
        db.setAutoDeploy(autoDeploy);

        service.save(db);


        return AjaxResult.ok().msg("调整自动发布:" + (autoDeploy ? "启用" : "停用"));
    }


    @HasPermission(value = "app:start",label = "启动")
    @RequestMapping("start/{appId}")
    public AjaxResult start(@PathVariable String appId) {
        service.start(appId);
        return AjaxResult.ok().msg("启动指令已发送");
    }

    @HasPermission("app:stop")
    @RequestMapping("stop/{appId}")
    public AjaxResult stop(@PathVariable String appId) {
        service.stop(appId);
        return AjaxResult.ok().msg("停止指令已发送");
    }

    @HasPermission(value = "app:config",label = "配置")
    @RequestMapping("rename")
    public AjaxResult rename(@RequestBody Map<String, String> map) {
        String appId = map.get("appId");
        String newName = map.get("newName");
        Assert.hasText(appId, "appId不能为空");
        Assert.hasText(newName, "新名称不能为空");
        App app = service.rename(appId, newName);


        return AjaxResult.ok().msg("部署指令已发送").data(app);
    }


    @RequestMapping("options")
    public AjaxResult options() {
        List<Option> list = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, BaseEntity.FIELD_UPDATE_TIME);

        List<App> all = service.findAll(sort);

        for (App app : all) {
            list.add(new Option(app.getName(), app.getId()));
        }

        return AjaxResult.ok().data(list);
    }


}
