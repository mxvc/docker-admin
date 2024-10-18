package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.BuildParam;
import cn.moon.docker.admin.entity.Project;
import cn.moon.docker.admin.service.BuildLogService;
import cn.moon.docker.admin.service.ProjectService;
import cn.moon.docker.admin.service.RegistryService;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.dao.specification.JpaQuery;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("project")
public class ProjectController {


    @Resource
    private ProjectService service;

    @Resource
    private BuildLogService logService;

    @Resource
    private RegistryService registryService;

    @RequestMapping("check")
    public AjaxResult check() {
        long count = registryService.countEnabled();
        if (count == 0) {
            return AjaxResult.err().msg("请先定义注册中心！");
        }
        return AjaxResult.ok();
    }


    @HasPermission("project:list")
    @RequestMapping("list")
    public Page<Project> list(String keyword, @PageableDefault(sort = BaseEntity.Fields.updateTime, direction = Sort.Direction.DESC) Pageable pageable) {
        JpaQuery<Project> q = getQuery();
        q.like(Project.Fields.name, keyword);

        return service.findAll(q, pageable);
    }

    private JpaQuery<Project> getQuery() {
        JpaQuery<Project> q = new JpaQuery<>();
        Subject subject = SecurityUtils.getSubject();
        q.in("sysOrg.id", subject.getOrgPermissions());
        return q;
    }

    @HasPermission("project:save")
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Valid Project project) {
        service.saveProject(project);
        return AjaxResult.ok().msg("保存成功");
    }

    @HasPermission("project:save")
    @RequestMapping("update")
    public AjaxResult update(@RequestBody @Valid Project project) {
        service.saveProject(project);
        return AjaxResult.ok().msg("修改成功");
    }


    @HasPermission("project:delete")
    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        Project project = service.findOne(id);
        service.deleteProject(id);

        return AjaxResult.ok().msg("删除成功");
    }


    @HasPermission("project:list")
    @RequestMapping("get")
    public Project get(String id) {
        return service.findOne(id);
    }


    @HasPermission("project:build")
    @RequestMapping("build")
    public AjaxResult build(
            BuildParam buildParam,
            @RequestParam String projectId,
            String buildHostId
    ) throws InterruptedException, IOException, GitAPIException {


        Project project = service.findOne(projectId);
        service.checkBuildImage();


        // 更新最近时间,方便排序
        project.setUpdateTime(new Date());
        project = service.save(project);

        buildParam.setBranchOrTag(project.getBranch());
        buildParam.setProjectId(project.getId());
        buildParam.setDockerfile(project.getDockerfile());
        buildParam.setBuildHostId(buildHostId);
        service.buildImage(buildParam);


        return AjaxResult.ok();
    }

    @RequestMapping("stopBuild")
    public AjaxResult stopBuild(@RequestParam String id) throws IOException {
        service.stopBuild(id);

        return AjaxResult.ok();
    }

    @RequestMapping("cleanErrorLog")
    public AjaxResult cleanErrorLog(@RequestParam String id) {
        service.cleanErrorLog(id);
        return AjaxResult.ok();
    }

    @RequestMapping("updateAutoPushLatest")
    public AjaxResult updateAutoPushLatest(@RequestParam String id, boolean value) {
        service.updateAutoPushLatest(id, value);
        return AjaxResult.ok();
    }


    @RequestMapping("options")
    public List<Option> options() throws InterruptedException, IOException, GitAPIException {
        JpaQuery<Project> q = getQuery();

        List<Project> list = service.findAll(q, Sort.by(Sort.Direction.DESC, BaseEntity.Fields.updateTime));

        List<Option> options = new ArrayList<>();
        for (Project h : list) {
            options.add(new Option(h.getName(), h.getId()));
        }


        return options;
    }

    @RequestMapping("versions")
    public List<Option> versions(String projectId) {
        List<String> versions = logService.versions(projectId);


        return versions.stream().map(v -> new Option(v, v)).collect(Collectors.toList());
    }
}
