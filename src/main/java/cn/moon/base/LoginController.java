package cn.moon.base;

import cn.moon.base.shiro.ShiroTool;
import cn.moon.lang.web.Result;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录验证
 */
@RestController
@RequestMapping("api")
public class LoginController {


    @RequestMapping("login")
    public Result login(@RequestBody LoginParam loginParam) {
        String username = loginParam.username;
        String inputPassword = loginParam.password;

        UsernamePasswordToken token = new UsernamePasswordToken(username, inputPassword);

        try {
            SecurityUtils.getSubject().login(token);
            return Result.ok().msg("登录成功").data(getInitData());
        } catch (Exception e) {
            return Result.err().msg("登录失败" + e.getMessage());
        }
    }


    @RequestMapping("login/check")
    public Result checkLogin() {
        boolean isLogin = SecurityUtils.getSubject().isAuthenticated();

        if (isLogin) {
            return Result.ok().data(getInitData());
        }

        return Result.err().msg("检查登录结果：未登录");
    }

    private Object getInitData() {
        Map<String, Object> data = new HashMap<>();

        Subject subject = SecurityUtils.getSubject();

        Set<String> perms = ShiroTool.scanPerms();


        Set<String> permSet = perms.stream().filter(subject::isPermitted).collect(Collectors.toSet());


        data.put("perms", permSet);


        return data;
    }

    @RequestMapping("logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.ok().msg("退出成功");
    }


    @Getter
    @Setter
    public static class LoginParam {
        String username;
        String password;
    }
}
