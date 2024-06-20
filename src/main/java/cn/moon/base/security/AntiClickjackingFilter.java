package cn.moon.base.security;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/***
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options
 */
public class AntiClickjackingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
        chain.doFilter(request, response);
    }


}
