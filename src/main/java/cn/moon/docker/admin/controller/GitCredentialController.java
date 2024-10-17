package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.entity.GitCredential;
import cn.moon.docker.admin.service.GitCredentialService;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.persistence.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.HasPermission;
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
    public Page<GitCredential> list(@PageableDefault(sort = BaseEntity.Fields.modifyTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<GitCredential> list = service.findAll(pageable);
        return list;
    }

    @HasPermission("gitCredential:save")
    @RequestMapping("save")
    public Result save(@RequestBody GitCredential gitCredential) {
        GitCredential db = service.save(gitCredential);

        Result rs = Result.ok().msg("保存成功");
        return rs;
    }

    @HasPermission("gitCredential:save")
    @RequestMapping("update")
    public Result update(@RequestBody GitCredential gitCredential) {
        service.save(gitCredential);
        return Result.ok().msg("修改成功");
    }



    @HasPermission("gitCredential:delete")
    @RequestMapping("delete")
    public Result delete( String id)  {
        service.deleteById(id);
        return Result.ok().msg("删除成功");
    }







}
