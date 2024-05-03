package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.service.HostService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("api/index")
public class IndexController {

    @Resource
    HostService hostService;


    public void host(){

        hostService.findAll();
    }
}
