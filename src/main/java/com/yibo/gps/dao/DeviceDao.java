package com.yibo.gps.dao;

import com.yibo.gps.entity.Device;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceDao {

    Device get(Device device);
}
