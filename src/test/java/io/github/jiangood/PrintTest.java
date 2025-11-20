package io.github.jiangood;

import cn.hutool.core.date.DateUtil;

import java.util.Date;

public class PrintTest {

    public static void main(String[] args) {
        String x = DateUtil.formatBetween(DateUtil.yesterday(), new Date());
        System.out.println(x);


    }
}
