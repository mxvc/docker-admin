package io.github.jiangood.docker.admin.controller;

import cn.hutool.core.date.DateUtil;
import io.github.jiangood.docker.admin.entity.BuildLog;
import io.github.jiangood.docker.admin.service.BuildLogService;


import io.admin.framework.data.query.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.io.UnsupportedEncodingException;

@RestController
@Slf4j
@RequestMapping(value = "admin/buildLog")
public class BuildLogController {

    @Resource
    private BuildLogService service;

    @RequestMapping("list")
    public Page<BuildLog> list(String projectId, @PageableDefault(sort = "createTime", direction = Sort.Direction.DESC) Pageable pageable) throws UnsupportedEncodingException {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.eq("projectId", projectId);
        Page<BuildLog> page = service.findAll(q, pageable);


        for (BuildLog log : page) {
            log.setLogUrl(LogUrlTool.getLogViewUrl(log.getId()));
            if (log.getTimeSpend() == null) {
                log.setTimeSpend(DateUtil.date().getTime() - log.getCreateTime().getTime());
            }
        }

        return page;
    }



}
