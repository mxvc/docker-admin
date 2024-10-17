package cn.moon.docker.admin.controller;

import cn.moon.docker.admin.service.DataPermService;
import cn.moon.docker.admin.service.ProjectService;
import cn.moon.lang.web.Result;
import cn.moon.lang.web.TreeOption;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 数据权限设置
 */
@RestController
@RequestMapping("api/data-perm")
@RequiresRoles("admin")
public class DataPermController {

    @Resource
    DataPermService dataPermService;


    @GetMapping("tree")
    public Result tree() {
        List<TreeOption> tree = dataPermService.tree();

        return Result.ok().data(tree);
    }

    @PostMapping("grant")
    public Result grant(@NotNull String id, @RequestParam("keys[]") String[] keys) {
        dataPermService.grant(id, keys);

        return Result.ok();
    }


}
