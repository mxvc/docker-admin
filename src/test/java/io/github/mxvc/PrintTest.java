package io.github.mxvc;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class PrintTest {

    public static void main(String[] args) {
        String x = DateUtil.formatBetween(DateUtil.yesterday(), new Date());
        System.out.println(x);


    }
}
