package cn.moon.docker.admin.controller;

import io.tmgg.modules.system.entity.SysLog;
import io.tmgg.modules.system.service.SysLogService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("home")
public class HomeController {

    @Resource
    SysLogService sysOpLogService;

    @GetMapping("eventList")
    public List<SysLog> eventList(@PageableDefault(direction = Sort.Direction.DESC,sort = "createTime") Pageable pageable){
        Page<SysLog> page = sysOpLogService.findAll(pageable);

        return page.getContent();

    }
}
