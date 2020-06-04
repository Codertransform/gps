package com.yibo.gps.dao;

import com.yibo.gps.entity.TransData;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TransDao {

    void insert(TransData data);
}
