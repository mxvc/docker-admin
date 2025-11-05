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

@RestController
@RequestMapping("appGroup")
public class AppGroupController  {

    @Resource
    private AppGroupService service;

    @HasPermission
    @RequestMapping("page")
    public AjaxResult page(AppGroup appGroup, String searchText, @PageableDefault(direction = Sort.Direction.DESC, sort = "updateTime") Pageable pageable) throws Exception {
        JpaQuery<AppGroup> q = new JpaQuery<>();
        q.searchText(searchText, service.getSearchableFields());
        q.searchParams(appGroup, service.getDomainClass());

        Page<AppGroup> page = service.findAllByClient(q, pageable);

        return AjaxResult.ok().data(page);
   }


    @HasPermission
    @PostMapping("save")
    public AjaxResult save(@RequestBody AppGroup input, RequestBodyKeys updateFields) throws Exception {
        service.saveOrUpdateByClient(input, updateFields);
        return AjaxResult.ok().msg("保存成功");
    }


    @HasPermission
    @RequestMapping("delete")
    public AjaxResult delete(String id) {
        service.deleteByClient(id);
        return AjaxResult.ok().msg("删除成功");
    }

}

