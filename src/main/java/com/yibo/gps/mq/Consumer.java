package com.yibo.gps.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yibo.gps.dao.DeviceDao;
import com.yibo.gps.dao.TrackMapper;
import com.yibo.gps.dao.TransDao;
import com.yibo.gps.entity.Device;
import com.yibo.gps.entity.Track;
import com.yibo.gps.entity.TransData;
import com.yibo.gps.utils.EntityIdGenerate;
import com.yibo.gps.utils.HexConvert;
import com.yibo.gps.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class Consumer {

    @Autowired
    private TransDao transDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private TrackMapper trackMapper;

    @JmsListener(destination = "gps_transform_data")
    public void receiveMsg(String text) throws ParseException {
        //配置坐标转换接口参数
        Map<String,String> map = new HashMap<>();
        String TRANSFORM_URL = "https://restapi.amap.com/v3/assistant/coordinate/convert";
        String KEY = "1c92d37732848ca864c4daac21454294";
        String COORDSYS = "gps";

        JSONObject jsonObject = JSON.parseObject(text);
        String lat = String.valueOf(jsonObject.get("latitude"));
        String lon = String.valueOf(jsonObject.get("longitude"));
        map.put("key", KEY);
        map.put("coordsys", COORDSYS);
        map.put("locations",lon + "," + lat);
        map.put("output","JSON");
        //调用高德坐标转换接口
        String result  = HttpClientUtil.doGet(TRANSFORM_URL,map);
        System.out.println("收到消息："+result);

        //将转换后的坐标信息封装并保存到数据库
        JSONObject object = JSON.parseObject(result);
        String[] loca = object.getString("locations").split(",");
        String longitude = loca[0];
        String latitude = loca[1];
        TransData data = new TransData();
        data.setId(EntityIdGenerate.generateId());
        data.setOriginId(String.valueOf(jsonObject.get("id")));
        data.setDeviceId(String.valueOf(jsonObject.get("serialNumber")));
        data.setLongitude(longitude);
        data.setLatitude(latitude);
        data.setLatitude(latitude);
        data.setSpeed(jsonObject.getString("speed"));
        data.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        transDao.insert(data);

        Track track = null;
        if (jsonObject.get("trackId") != null){
            track = trackMapper.get(jsonObject.getString("trackId"));
        }else {
            Track t = new Track();
            t.setDeviceId(jsonObject.getString("serialNumber"));
            track = trackMapper.getLast(t);
        }

        Device device = new Device();
        device.setDeviceId(jsonObject.getString("serialNumber"));
        Device dev = deviceDao.get(device);
        if (acc(jsonObject.getString("vehicle_status"))) {
            String url = "https://tsapi.amap.com/v1/track/point/upload";
            map.clear();
            map.put("key","1c92d37732848ca864c4daac21454294");
            map.put("sid",dev.getsId());
            map.put("tid", dev.gettId());
            map.put("trid", track.getTrackId());
            JSONArray array = new JSONArray();
            Map<String,String> map1 = new HashMap<>();
            map1.put("location", longitude + "," + latitude);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject.getString("date") + " " + jsonObject.getString("time"));
            map1.put("locatetime", String.valueOf(date.getTime()));
            map1.put("speed",jsonObject.getString("speed"));
            map1.put("direction",jsonObject.getString("direction"));
            array.add(map1);
            map.put("points",array.toJSONString());
            String result1 = HttpClientUtil.doPost(url,map);
            System.out.println(result1);
        }else {
            System.out.println("车辆熄火");
            track.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            String url = "https://tsapi.amap.com/v1/track/terminal/trsearch";
            map.clear();
            map.put("key","1c92d37732848ca864c4daac21454294");
            map.put("sid",dev.getsId());
            map.put("tid",dev.gettId());
            map.put("trid",track.getTrackId());
            map.put("correction", "denoise=1,mapmatch=1");
            String result2 = HttpClientUtil.doGet(url,map);
            JSONObject object1 = JSONObject.parseObject(result2);
            if (object1.getString("errcode").equals("10000")) {
                JSONObject jsonObject1 = object1.getJSONObject("data");
                JSONArray array = jsonObject1.getJSONArray("tracks");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    track.setMiles(obj.getString("distance"));
                }
            }
            trackMapper.update(track);
        }
    }

    private boolean acc(String status){
        String str = HexConvert.hexString2binaryString(status.substring(4,6));
        char ch = str.charAt(5);
        return ch != '0';
    }

    @JmsListener(destination = "ActiveMQ.DLQ")
    public void receiveMsg2(String text) throws ParseException {
        //配置坐标转换接口参数
        Map<String,String> map = new HashMap<>();
        String TRANSFORM_URL = "https://restapi.amap.com/v3/assistant/coordinate/convert";
        String KEY = "1c92d37732848ca864c4daac21454294";
        String COORDSYS = "gps";

        JSONObject jsonObject = JSON.parseObject(text);
        String lat = String.valueOf(jsonObject.get("latitude"));
        String lon = String.valueOf(jsonObject.get("longitude"));
        map.put("key", KEY);
        map.put("coordsys", COORDSYS);
        map.put("locations",lon + "," + lat);
        map.put("output","JSON");
        //调用高德坐标转换接口
        String result  = HttpClientUtil.doGet(TRANSFORM_URL,map);
        System.out.println("收到消息："+result);

        //将转换后的坐标信息封装并保存到数据库
        JSONObject object = JSON.parseObject(result);
        String[] loca = object.getString("locations").split(",");
        String longitude = loca[0];
        String latitude = loca[1];
        TransData data = new TransData();
        data.setId(EntityIdGenerate.generateId());
        data.setOriginId(String.valueOf(jsonObject.get("id")));
        data.setDeviceId(String.valueOf(jsonObject.get("serialNumber")));
        data.setLongitude(longitude);
        data.setLatitude(latitude);
        data.setLatitude(latitude);
        data.setSpeed(jsonObject.getString("speed"));
        data.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        transDao.insert(data);

        Track track = null;
        if (jsonObject.get("trackId") != null){
            track = trackMapper.get(jsonObject.getString("trackId"));
        }else {
            Track t = new Track();
            t.setDeviceId(jsonObject.getString("serialNumber"));
            track = trackMapper.getLast(t);
        }

        Device device = new Device();
        device.setDeviceId(jsonObject.getString("serialNumber"));
        Device dev = deviceDao.get(device);
        if (acc(jsonObject.getString("vehicle_status"))) {
            String url = "https://tsapi.amap.com/v1/track/point/upload";
            map.clear();
            map.put("key","1c92d37732848ca864c4daac21454294");
            map.put("sid",dev.getsId());
            map.put("tid", dev.gettId());
            map.put("trid", track.getTrackId());
            JSONArray array = new JSONArray();
            Map<String,String> map1 = new HashMap<>();
            map1.put("location", longitude + "," + latitude);
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jsonObject.getString("date") + " " + jsonObject.getString("time"));
            map1.put("locatetime", String.valueOf(date.getTime()));
            map1.put("speed",jsonObject.getString("speed"));
            map1.put("direction",jsonObject.getString("direction"));
            array.add(map1);
            map.put("points",array.toJSONString());
            String result1 = HttpClientUtil.doPost(url,map);
            System.out.println(result1);
        }else {
            System.out.println("车辆熄火");
            track.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            String url = "https://tsapi.amap.com/v1/track/terminal/trsearch";
            map.clear();
            map.put("key","1c92d37732848ca864c4daac21454294");
            map.put("sid",dev.getsId());
            map.put("tid",dev.gettId());
            map.put("trid",track.getTrackId());
            map.put("correction", "denoise=1,mapmatch=1");
            String result2 = HttpClientUtil.doGet(url,map);
            JSONObject object1 = JSONObject.parseObject(result2);
            if (object1.getString("errcode").equals("10000")) {
                JSONObject jsonObject1 = object1.getJSONObject("data");
                JSONArray array = jsonObject1.getJSONArray("tracks");
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    track.setMiles(obj.getString("distance"));
                }
            }
            trackMapper.update(track);
        }
    }
}
