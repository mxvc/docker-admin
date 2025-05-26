package cn.moon.docker.admin.controller;

import cn.hutool.core.date.DateUtil;
import cn.moon.docker.admin.entity.BuildLog;
import cn.moon.docker.admin.service.BuildLogService;


import io.tmgg.web.persistence.BaseEntity;
import io.tmgg.web.persistence.specification.JpaQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import java.io.UnsupportedEncodingException;

import static cn.moon.docker.admin.controller.LogUrlTool.getLogViewUrl;

@RestController
@Slf4j
@RequestMapping(value = "buildLog")
public class BuildLogController {

    @Resource
    private BuildLogService service;

    @RequestMapping("list")
    public Page<BuildLog> list(String projectId, @PageableDefault(sort = BaseEntity.FIELD_CREATE_TIME, direction = Sort.Direction.DESC) Pageable pageable) throws UnsupportedEncodingException {
        JpaQuery<BuildLog> q = new JpaQuery<>();
        q.eq("projectId", projectId);
        Page<BuildLog> page = service.findAll(q, pageable);


        for (BuildLog log : page) {
            log.setLogUrl(getLogViewUrl(log.getId()));
            if (log.getTimeSpend() == null) {
                log.setTimeSpend(DateUtil.date().getTime() - log.getCreateTime().getTime());
            }
        }

        return page;
    }



}
