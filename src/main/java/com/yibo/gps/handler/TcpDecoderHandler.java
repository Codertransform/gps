package com.yibo.gps.handler;

import com.yibo.gps.dao.OriginDataMapper;
import com.yibo.gps.entity.OriginGPSData;
import com.yibo.gps.utils.EntityIdGenerate;
import com.yibo.gps.utils.HexConvert;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@ChannelHandler.Sharable
public class TcpDecoderHandler extends MessageToMessageDecoder<ByteBuf> {

    private static final Logger logger = LoggerFactory.getLogger(TcpDecoderHandler.class);

    @Autowired
    private OriginDataMapper originDataMapper;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        logger.info("解析client上报数据");
        byte[] data = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        char s = (char) data[0];
        OriginGPSData gpsData = new OriginGPSData();
        if (s == '*') {
            String msg = new String(data, StandardCharsets.UTF_8);
            msg = msg.substring(1,msg.length()-1);
            System.out.println(msg);
            String[] split = msg.split(",");
            gpsData.setId(EntityIdGenerate.generateId());
            gpsData.setManuName(split[0]);
            gpsData.setSerialNumber(split[1]);
            gpsData.setDataType(split[2]);
            gpsData.setTime(split[3]);
            gpsData.setValid(split[4]);
            gpsData.setLatitude(position(split[5]));
            gpsData.setLat_flag(split[6]);
            gpsData.setLongitude(position(split[7]));
            gpsData.setLon_flag(split[8]);
            gpsData.setSpeed(split[9]);
            gpsData.setDirection(split[10]);
            gpsData.setDate(split[11]);
            gpsData.setVehicle_status(split[12]);
            gpsData.setNet_mcc(split[13]);
            gpsData.setNet_mnc(split[14]);
            gpsData.setNet_lac(split[15]);
            gpsData.setNet_cellid(split[16]);
            if (split[2].equals("V1")){
                gpsData.setVoltage(split[17]);
                gpsData.setGSM(split[18]);
                gpsData.setSatellites(split[19]);
                gpsData.setVoltage_unit(split[20]);
            }else {
                gpsData.setIccid(split[17]);
            }
            int i = originDataMapper.insert(gpsData);
            System.out.println("接收到数据" + gpsData);
        }
        if (s == '$'){
            String hexString = HexConvert.BinaryToHexString( data ).replace( " ","" );
            System.out.println(hexString);
            gpsData.setId(EntityIdGenerate.generateId());
            gpsData.setSerialNumber(hexString.substring(2,12));
            gpsData.setDataType("No");
            int h = Integer.parseInt(hexString.substring(12,14)) + 8;
            String hour = "";
            if (h < 10) {
                hour = "0"+ h;
            }else {
                hour = String.valueOf(h);
            }
            String min = hexString.substring(14,16);
            String sec = hexString.substring(16,18);
            gpsData.setTime(hour + ":" + min + ":" +sec);
            String day = hexString.substring(18,20);
            String mon = hexString.substring(20,22);
            String year = new SimpleDateFormat("yy").format(new Date()) + hexString.substring(22,24);
            gpsData.setDate(year + "-" + mon + "-" + day);
            String latitude = hexString.substring(24,32);
            String longitude = hexString.substring(34,43);
            String lat = new DecimalFormat(".000000").format(Double.parseDouble(latitude.substring(2))/10000/60);
            String lon = new DecimalFormat(".000000").format(Double.parseDouble(longitude.substring(3))/10000/60);
            latitude = latitude.substring(0,2) + lat;
            longitude = longitude.substring(0,3) + lon;
            gpsData.setLatitude(latitude);
            gpsData.setLongitude(longitude);
            String flag = hexString.substring(43,44);
            String bytes = HexConvert.hexString2binaryString(flag);
            char[] chars = bytes.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (i == 0 & chars[i] == '1'){
                    gpsData.setValid("A");
                }else if (i == 0 & chars[i] == '0'){
                    gpsData.setValid("V");
                }
                if (i == 1 & chars[i] == '1'){
                    gpsData.setLat_flag("N");
                }else if (i == 1 & chars[i] == '0'){
                    gpsData.setLat_flag("S");
                }
                if (i == 2 & chars[i] == '1'){
                    gpsData.setLon_flag("E");
                }else if (i == 2 & chars[i] == '0'){
                    gpsData.setLon_flag("W");
                }
            }
            gpsData.setSpeed(String.valueOf(Integer.parseInt(hexString.substring(44,47))*1.852));
            gpsData.setDirection(hexString.substring(47,50));
            gpsData.setVehicle_status(hexString.substring(50,58));
            long GSM = Long.parseLong(hexString.substring(62,64), 16);
            gpsData.setGSM(String.valueOf(GSM));
            gpsData.setSatellites(hexString.substring(64,66));
            long in = Long.parseLong(hexString.substring(66,68),16);
            long sn = Long.parseLong(hexString.substring(68,70),16);
            String unit = in + "." +sn;
            gpsData.setVoltage_unit(unit);
            gpsData.setNet_mcc(String.valueOf(Long.parseLong(hexString.substring(76,78),16)));
            gpsData.setNet_mnc(String.valueOf(Long.parseLong(hexString.substring(78,80),16)));
            gpsData.setNet_lac(String.valueOf(Long.parseLong(hexString.substring(80,84),16)));
            gpsData.setNet_cellid(String.valueOf(Long.parseLong(hexString.substring(84,88),16)));
            int i = originDataMapper.insert(gpsData);
            System.out.println("收到发来的消息：" + gpsData);
        }
    }

    private String position(String pos){
        String[] ps = pos.split("\\.");
        String position = "";
        if (ps[0] != null) {
            int length = ps[0].length();
            String front = ps[0].substring(0,length-2);
            String last = ps[0].substring(length-2,length);
            position = front + "." + last + ps[1];
        }
        return position;
    }
}
