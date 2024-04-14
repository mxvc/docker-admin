package cn.moon.base.shiro;

import cn.moon.docker.admin.dao.UserDao;
import cn.moon.docker.admin.entity.User;
import cn.moon.base.role.Role;
import cn.moon.docker.admin.service.DataPermService;
import cn.moon.lang.web.TreeOption;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Component
public class MyRealm extends AuthorizingRealm {


    @Resource
    UserDao userDao;

    @Resource
    DataPermService dataPermService;


    //用于在进行用户身份认证时获取用户的认证信息。
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = new String(token.getPassword());

        // 根据用户名查询用户信息（比如从数据库中查询）
        User user = userDao.findByUsername(username);

        // 判断用户是否存在
        if (user == null) {
            throw new UnknownAccountException("用户不存在");
        }

        // 验证密码是否匹配
        if (!password.equals(user.getPassword())) {
            throw new IncorrectCredentialsException("密码错误");
        }

        // 构建认证信息
        CurrentUser userInfo = new CurrentUser();
        userInfo.setUsername(username);

        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userInfo, user.getPassword(), getName());


        return authenticationInfo;
    }


    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 获取用户名
        CurrentUser currentUser = (CurrentUser) principals.getPrimaryPrincipal();
        String username = currentUser.getUsername();

        User user = userDao.findByUsername(username);

        Role role = user.getRole();

        if (username.equals("admin") && role == null) {
            role = Role.admin;
        }


        // 构建授权信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        for (String perm : role.getPerms()) {
            info.addStringPermission(perm);
        }

        // 角色
        info.addRole(role.name());


        // 数据权限
        if (user.getDataPerms() == null) {
            return info;
        }
        for (String dataPerm : user.getDataPerms()) {
            int index = dataPerm.indexOf("-");
            if (index > 0) {
                String k = dataPerm.substring(0, index);
                String v = dataPerm.substring(index + 1);

                if(!currentUser.getDataPerms().containsKey(k)){
                    currentUser.getDataPerms().put(k, new HashSet<>());
                }
                currentUser.getDataPerms().get(k).add(v);
            }
        }


        return info;
    }
}
