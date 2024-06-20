package cn.moon.base.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class PermissionsPolicyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // 这行代码将阻止任何源（包括顶层页面和 <iframe>）使用任何受权限策略控制的特性。星号 * 表示所有特性，而空列表 () 表示没有任何源被允许使用这些特性
        httpServletResponse.setHeader("Permissions-Policy", "*");
        chain.doFilter(request, response);
    }


}
