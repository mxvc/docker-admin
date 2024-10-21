package cn.moon.docker.admin.controller;

import io.tmgg.lang.RequestTool;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LogUrlTool {

    public static  String getLogViewUrl(HttpServletRequest req, String logger) throws UnsupportedEncodingException {
        File file = new File("docker-admin-logs", logger + ".log");
        String path = file.getAbsolutePath();

        path = URLEncoder.encode(path,"utf-8");

        return "/ws-log-view?path=" + path;
    }
}
