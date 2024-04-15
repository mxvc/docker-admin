package cn.moon.docker.admin.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class LogUrlTool {

    public static  String getLogViewUrl(String logger) throws UnsupportedEncodingException {
        File file = new File("docker-admin-logs", logger + ".log");
        String path = file.getAbsolutePath();

        path = URLEncoder.encode(path,"utf-8");
        return "/log-view/index.html?path=" + path;
    }
}
