package cn.moon.base.shiro;

import cn.moon.lang.json.JsonTool;
import cn.moon.lang.web.Result;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        if (isAjax(req)) {
            Result rs = Result.err().msg("未登录").code(401);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().println(JsonTool.toJsonQuietly(rs));
            return false;
        }



        // 非Ajax请求，交给Spring异常处理机制进行处理
        return super.onAccessDenied(request, response);
    }


    private boolean isAjax(HttpServletRequest req){
        if("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))){
            return  true;
        }

        return req.getHeader("Accept") != null && req.getHeader("Accept").contains("application/json");
    }

}