package com.yibo.gps.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ChannelHandler.Sharable
public class TransformHandler extends MessageToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List list) throws Exception {

    }
}
