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

    private static String trackId;

    private static int flag = 0;

    @Autowired
    private TransDao transDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private TrackMapper trackMapper;

    @JmsListener(destination = "gps_transform_data")
    public void receiveMsg(String text) throws ParseException {
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
        String result  = HttpClientUtil.doGet(TRANSFORM_URL,map);
        System.out.println("收到消息："+result);

        JSONObject object = JSON.parseObject(result);
        String[] loca = object.getString("locations").split(",");
        String longitude = loca[0];
        String latitude = loca[1];
        TransData data = new TransData();
        data.setId(EntityIdGenerate.generateId());
        data.setOriginId(String.valueOf(jsonObject.get("id")));
        data.setLongitude(longitude);
        data.setLatitude(latitude);
        data.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        transDao.insert(data);

        if (jsonObject.get("trackId") != null){
            trackId = jsonObject.getString("trackId");
        }

        Device device = new Device();
        device.setDeviceId(jsonObject.getString("serialNumber"));
        Device dev = deviceDao.get(device);
        if (acc(jsonObject.getString("vehicle_status"))) {
            if (flag != 0){
                String url = "https://tsapi.amap.com/v1/track/point/upload";
                map.clear();
                map.put("key","1c92d37732848ca864c4daac21454294");
                map.put("sid",dev.getsId());
                map.put("tid", dev.gettId());
                map.put("trid", trackId);
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
                System.out.println("车辆启动");
                String url = "https://tsapi.amap.com/v1/track/trace/add";
                map.clear();
                map.put("key","1c92d37732848ca864c4daac21454294");
                map.put("sid",dev.getsId());
                map.put("tid",dev.gettId());
                String result3 = HttpClientUtil.doPost(url,map);
                System.out.println(result3);
                JSONObject object2 = JSONObject.parseObject(result3);
                if (object2.getString("errcode").equals("10000")){
                    JSONObject json = object2.getJSONObject("data");
                    Track track = new Track();
                    track.setId(EntityIdGenerate.generateId());
                    track.setTrackId(json.get("trid").toString());
                    track.setStartTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    track.setCarId(dev.getCarId());
                    trackMapper.insert(track);
                    trackId = track.getTrackId();
                }
                flag = 1;
            }
        }else {
            System.out.println("车辆熄火");
            Track track = null;
            if (trackId != null){
                track = trackMapper.get(trackId);
            }else {
                track = trackMapper.getLast();
            }
            track.setEndTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            String url = "https://tsapi.amap.com/v1/track/terminal/trsearch";
            map.clear();
            map.put("key","1c92d37732848ca864c4daac21454294");
            map.put("sid",dev.getsId());
            map.put("tid",dev.gettId());
            map.put("trid",track.getTrackId());
            map.put("correction", "denoise=1,mapmatch=0");
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
}
