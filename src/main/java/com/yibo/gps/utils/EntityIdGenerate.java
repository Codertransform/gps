package com.yibo.gps.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class EntityIdGenerate {

    public static String generateId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static String generateImgName(){
        Random random = new Random();
        // 随机数的量 自由定制，这是5位随机数
        int r = random.nextInt(90000) + 10000;

        DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String timeStr = sdf.format(new Date());
        return "IMG" + timeStr + r;
    }

    /**
     * 生成主键id
     * 时间+随机数
     * @return
     */
    public static synchronized String generateOrderId(){
        Random random = new Random();
        // 随机数的量 自由定制，这是5位随机数
        int r = random.nextInt(90000) + 10000;

        // 返回  13位时间
        Long timeMillis = System.currentTimeMillis();

        // 返回  17位时间
        DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timeStr = sdf.format(new Date());

        // 13位毫秒+5位随机数
//        return  "YBTC" + timeMillis + String.valueOf(r);
        // 17位时间+5位随机数
        return "YBTC" + timeStr + r;
    }

    public static void main(String[] args) {
        System.out.println(generateOrderId());
    }
}
