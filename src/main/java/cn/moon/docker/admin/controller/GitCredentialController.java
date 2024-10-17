package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.entity.GitCredential;
import cn.moon.docker.admin.service.GitCredentialService;
import io.tmgg.lang.dao.BaseEntity;
import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.web.annotion.HasPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@Slf4j
@RequestMapping(value = "api/gitCredential")
public class GitCredentialController {


    @Resource
    private GitCredentialService service;


    @HasPermission("gitCredential:list")
    @RequestMapping("list")
    public Page<GitCredential> list(@PageableDefault(sort = BaseEntity.Fields.updateTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<GitCredential> list = service.findAll(pageable);
        return list;
    }

    @HasPermission("gitCredential:save")
    @RequestMapping("save")
    public AjaxResult save(@RequestBody GitCredential gitCredential) {
        GitCredential db = service.save(gitCredential);

        return AjaxResult.ok().msg("保存成功");
    }

    @HasPermission("gitCredential:save")
    @RequestMapping("update")
    public AjaxResult update(@RequestBody GitCredential gitCredential) {
        service.save(gitCredential);
        return AjaxResult.ok().msg("修改成功");
    }



    @HasPermission("gitCredential:delete")
    @RequestMapping("delete")
    public AjaxResult delete( String id)  {
        service.deleteById(id);
        return AjaxResult.ok().msg("删除成功");
    }


}
