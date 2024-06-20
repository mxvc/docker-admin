package cn.moon.base.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***
 * @description 解决CSP问题
 */
public class CSPFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Content-Security-Policy",
                "default-src 'self'; img-src 'self' data: ; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; frame-ancestors 'self'; form-action 'self'; base-uri 'self'; object-src 'none'; report-uri 'self'");
        chain.doFilter(request, response);
    }




}
