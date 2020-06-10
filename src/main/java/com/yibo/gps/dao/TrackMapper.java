package com.yibo.gps.dao;

import com.yibo.gps.entity.Track;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TrackMapper {

    void insert(Track track);
}
