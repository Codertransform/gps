package com.yibo.gps.entity;

import com.alibaba.fastjson.JSONObject;

public class OriginGPSData {
    private String id;
    private String manuName;            //制造商名称
    private String serialNumber;        //车载机序列号
    private String dataType;            //数据类型
    private String time;                //时间
    private String valid;               //数据有效位
    private String latitude;            //纬度
    private String lat_flag;            //纬度标志
    private String longitude;           //经度
    private String lon_flag;            //经度标志
    private String speed;               //速度
    private String direction;           //方位角
    private String date;                //日期
    private String vehicle_status;      //车辆状态
    private String net_mcc;             //移动国家码
    private String net_mnc;             //移动网络码
    private String net_lac;             //基站区域码
    private String net_cellid;          //基站编码
    private String voltage;             //电压
    private String GSM;                 //GSM信号值
    private String satellites;          //GPS卫星数量
    private String voltage_unit;        //电池电压单位
    private String iccid;               //iccid
    private String trackId;             //轨迹id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManuName() {
        return manuName;
    }

    public void setManuName(String manuName) {
        this.manuName = manuName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLat_flag() {
        return lat_flag;
    }

    public void setLat_flag(String lat_flag) {
        this.lat_flag = lat_flag;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLon_flag() {
        return lon_flag;
    }

    public void setLon_flag(String lon_flag) {
        this.lon_flag = lon_flag;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVehicle_status() {
        return vehicle_status;
    }

    public void setVehicle_status(String vehicle_status) {
        this.vehicle_status = vehicle_status;
    }

    public String getNet_mcc() {
        return net_mcc;
    }

    public void setNet_mcc(String net_mcc) {
        this.net_mcc = net_mcc;
    }

    public String getNet_mnc() {
        return net_mnc;
    }

    public void setNet_mnc(String net_mnc) {
        this.net_mnc = net_mnc;
    }

    public String getNet_lac() {
        return net_lac;
    }

    public void setNet_lac(String net_lac) {
        this.net_lac = net_lac;
    }

    public String getNet_cellid() {
        return net_cellid;
    }

    public void setNet_cellid(String net_cellid) {
        this.net_cellid = net_cellid;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getGSM() {
        return GSM;
    }

    public void setGSM(String GSM) {
        this.GSM = GSM;
    }

    public String getSatellites() {
        return satellites;
    }

    public void setSatellites(String satellites) {
        this.satellites = satellites;
    }

    public String getVoltage_unit() {
        return voltage_unit;
    }

    public void setVoltage_unit(String voltage_unit) {
        this.voltage_unit = voltage_unit;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",id);
        jsonObject.put("manuName",manuName);
        jsonObject.put("serialNumber",serialNumber);
        jsonObject.put("dataType",dataType);
        jsonObject.put("time",time);
        jsonObject.put("valid",valid);
        jsonObject.put("latitude",latitude);
        jsonObject.put("lat_flag",lat_flag);
        jsonObject.put("longitude",longitude);
        jsonObject.put("lon_flag",lon_flag);
        jsonObject.put("speed",speed);
        jsonObject.put("direction",direction);
        jsonObject.put("date",date);
        jsonObject.put("vehicle_status",vehicle_status);
        jsonObject.put("net_mcc",net_mcc);
        jsonObject.put("net_mnc",net_mnc);
        jsonObject.put("net_lac",net_lac);
        jsonObject.put("net_cellid",net_cellid);
        jsonObject.put("voltage",voltage);
        jsonObject.put("GSM",GSM);
        jsonObject.put("satellites",satellites);
        jsonObject.put("voltage_unit",voltage_unit);
        jsonObject.put("iccid",iccid);
        jsonObject.put("trackId",trackId);
        return jsonObject.toString();
    }
}
