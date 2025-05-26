package cn.moon.docker.admin.controller;

import io.tmgg.lang.obj.AjaxResult;
import io.tmgg.lang.obj.Option;
import cn.moon.docker.admin.entity.GitCredential;
import cn.moon.docker.admin.service.GitCredentialService;


import io.tmgg.web.persistence.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("gitCredential")
public class GitCredentialController extends BaseController<GitCredential> {

    @Resource
    GitCredentialService service;



}

