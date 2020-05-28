package com.yibo.gps.dao;

import com.yibo.gps.entity.OriginGPSData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OriginDataMapper {

    int insert(OriginGPSData originGPSData);

}
