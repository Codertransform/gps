package com.yibo.gps.dao;

import com.yibo.gps.entity.Track;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TrackMapper {

    void insert(Track track);

    Track get(@Param("trackId") String trackId);

    void update(Track track);

    Track getLast(Track track);
}
