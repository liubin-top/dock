package com.sinosoft.controller;

import com.sinosoft.base.controller.Result;
import com.sinosoft.task.DataDockComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class test {

    @Autowired
    DataDockComplete dataDockComplete;


    @GetMapping(value = "/api/test")
    public void test() throws IOException, InterruptedException, ParseException {
        long startMs = System.currentTimeMillis();
        dataDockComplete.test();
//        dataDockComplete.dataData();
//        dataDockComplete.localData();
//        dataDockComplete.importData();
//        dataDockComplete.importDataTwo();
//        风险监测没有分类的指标
//        dataDockComplete.importDataTwoOne();
//        dataDockComplete.testjia();
        long endMs = System.currentTimeMillis();
        System.out.println("Time elapsed:" + ms2DHMS(startMs,endMs));
    }


    private static String ms2DHMS(long startMs,long endMs){
        String retval = null;
        long secondCount = (endMs - startMs) / 1000;
        String ms = (endMs - startMs) % 1000 + "ms";

        long days = secondCount / (60 * 60 * 24);
        long hours = (secondCount % (60 * 60 * 24)) / (60 * 60);
        long minutes = (secondCount % (60 * 60)) / 60;
        long seconds = secondCount % 60;

        if (days > 0) {
            retval = days + "d" + hours + "h" + minutes + "m" + seconds + "s";
        } else if (hours > 0) {
            retval = hours + "h" + minutes + "m" + seconds + "s";
        } else if (minutes > 0) {
            retval = minutes + "m" + seconds + "s";
        } else if(seconds > 0) {
            retval = seconds + "s";
        }else {
            return ms;
        }

        return retval + ms;

    }
}
