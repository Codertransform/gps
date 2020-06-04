package com.yibo.gps.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yibo.gps.dao.TransDao;
import com.yibo.gps.entity.TransData;
import com.yibo.gps.utils.EntityIdGenerate;
import com.yibo.gps.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Consumer {

    @Autowired
    private TransDao transDao;

    @JmsListener(destination = "gps_transform_data")
    public void receiveMsg(String text){
        Map<String,String> map = new HashMap<>();
        String TRANSFORM_URL = "https://restapi.amap.com/v3/assistant/coordinate/convert";
        String KEY = "1c92d37732848ca864c4daac21454294";
        String COORDSYS = "gps";

        JSONObject jsonObject = JSON.parseObject(text);
        String latitude = String.valueOf(jsonObject.get("latitude"));
        String longitude = String.valueOf(jsonObject.get("longitude"));
        map.put("key", KEY);
        map.put("coordsys", COORDSYS);
        map.put("locations",longitude + "," + latitude);
        map.put("output","JSON");
        String result  = HttpClientUtil.doGet(TRANSFORM_URL,map);

        JSONObject object = JSON.parseObject(result);
        String[] loca = object.getString("locations").split(",");
        TransData data = new TransData();
        data.setId(EntityIdGenerate.generateId());
        data.setOriginId(String.valueOf(jsonObject.get("id")));
        data.setLongitude(loca[0]);
        data.setLatitude(loca[1]);
        transDao.insert(data);
        System.out.println("收到消息："+result);
    }
}
