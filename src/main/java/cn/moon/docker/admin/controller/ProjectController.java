package cn.moon.docker.admin.controller;

import cn.hutool.core.util.StrUtil;
import cn.moon.docker.admin.BuildParam;
import cn.moon.docker.admin.entity.Project;
import cn.moon.docker.admin.service.BuildLogService;
import cn.moon.docker.admin.service.ProjectService;



import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import io.tmgg.web.annotion.HasPermission;
import io.tmgg.web.argument.RequestBodyKeys;
import io.tmgg.web.perm.SecurityUtils;
import io.tmgg.web.perm.Subject;
import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.specification.JpaQuery;
import jakarta.annotation.Resource;
import lombok.Data;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("project")
public class ProjectController  {


    @Resource
    private ProjectService service;

    @Resource
    private BuildLogService logService;

    @Data
    public static class QueryParam {
        String orgId;
    }

    @HasPermission
    @PostMapping("page")
    public AjaxResult page(@RequestBody QueryParam param, String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = {"updateTime"}) Pageable pageable) {
        JpaQuery<Project> q = buildQuery();
        q.searchText(searchText, "name","remark");

        if(StrUtil.isNotEmpty(param.getOrgId())){
            q.eq(Project.Fields.sysOrg + ".id", param.getOrgId());
        }


        Page<Project> page = this.service.findAll(q, pageable);

        return AjaxResult.ok().data(page);
    }

    private JpaQuery<Project> buildQuery() {
        JpaQuery<Project> q = new JpaQuery<>();
        Subject subject = SecurityUtils.getSubject();
        q.addSubOr(qq->{
            qq.isNull("sysOrg.id");
            qq.in("sysOrg.id", subject.getOrgPermissions());
        });

        return q;
    }
    @HasPermission
    @PostMapping({"save"})
    public AjaxResult save(@RequestBody Project param,  RequestBodyKeys updateFields) throws Exception {
        if(param.getSysOrg().getId() == null){
            param.setSysOrg(null);
        }
        Project result = this.service.saveOrUpdate(param,updateFields);
        return AjaxResult.ok().data(result.getId()).msg("保存成功");
    }



    @HasPermission
    @PostMapping({"delete"})
    public AjaxResult delete(String id) {
        this.service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }

    @HasPermission("project:list")
    @RequestMapping("get")
    public Project get(String id) {
        return service.findOne(id);
    }


    @HasPermission(value = "project:build",label = "构建")
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


        return AjaxResult.ok().msg("构建已触发");
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


    @RequestMapping("options")
    public List<Option> options() throws InterruptedException, IOException, GitAPIException {
        JpaQuery<Project> q = buildQuery();

        List<Project> list = service.findAll(q, Sort.by(Sort.Direction.DESC, BaseEntity.FIELD_UPDATE_TIME));

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
