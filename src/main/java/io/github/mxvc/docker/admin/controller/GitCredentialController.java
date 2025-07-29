package io.github.mxvc.docker.admin.controller;

import io.github.mxvc.docker.admin.service.GitCredentialService;
import io.github.mxvc.docker.admin.entity.GitCredential;


import io.tmgg.web.persistence.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import jakarta.annotation.Resource;

@RestController
@RequestMapping("gitCredential")
public class GitCredentialController extends BaseController<GitCredential> {

    @Resource
    GitCredentialService service;



}

