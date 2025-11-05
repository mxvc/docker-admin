package io.github.mxvc.docker.admin.controller;

import io.tmgg.data.query.JpaQuery;
import io.tmgg.dto.AjaxResult;
import io.github.mxvc.docker.admin.entity.AppGroup;
import io.github.mxvc.docker.admin.service.AppGroupService;
import io.tmgg.web.argument.RequestBodyKeys;
import jakarta.annotation.Resource;
import io.tmgg.web.annotion.HasPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("appGroup")
public class AppGroupController  {

    @Resource
    private AppGroupService service;

    @HasPermission
    @RequestMapping("page")
    public AjaxResult page( String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<AppGroup> q = new JpaQuery<>();
        q.like(AppGroup.Fields.name, searchText);

        Page<AppGroup> page = service.findAllByRequest(q, pageable);

        return AjaxResult.ok().data(page);
   }

    @RequestMapping("list")
    public AjaxResult list() throws Exception {
        JpaQuery<AppGroup> q = new JpaQuery<>();

        List<AppGroup> list = service.findAll(q, Sort.by("seq"));

        return AjaxResult.ok().data(list);
    }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody AppGroup input, RequestBodyKeys updateFields) throws Exception {
        service.saveOrUpdateByRequest(input, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }


    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteByRequest(id);
        return AjaxResult.ok().msg("删除成功");
    }

}

