package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.entity.ScriptLog;
import cn.moon.docker.admin.service.ScriptLogService;
import cn.moon.docker.admin.service.ScriptService;
import cn.moon.docker.sdk.log.LogConstants;
import cn.hutool.core.date.DateUtil;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.persistence.BaseEntity;
import cn.moon.lang.web.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping(value = "api/scriptLog")
public class ScriptLogController {

    @Resource
    private ScriptLogService service;

    @Resource
    private ScriptService scriptService;

    @RequestMapping("list")
    public Page<ScriptLog> list(@RequestParam String scriptId, @PageableDefault(sort = BaseEntity.Fields.createTime, direction = Sort.Direction.DESC) Pageable pageable) {
        Query<ScriptLog> q = new Query<>();
        q.eq("scriptId", scriptId);


        Page<ScriptLog> list = service.findAll(q, pageable);


        for (ScriptLog log : list) {

            log.setLogUrl(LogConstants.getLogViewUrl(log.getId()));
            if (log.getTimeSpend() == null) {
                log.setTimeSpend(DateUtil.date().getTime() - log.getCreateTime().getTime());
            }
        }

        return list;
    }

    @RequestMapping("stop")
    public Result stop(String id) throws IOException {

        ScriptLog scriptLog = service.findOne(id);
        scriptService.stop(scriptLog.getId());



        return Result.ok();
    }


}
