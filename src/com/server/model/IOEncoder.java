package com.server.model;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class IOEncoder extends OneToOneEncoder{

	@Override
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		byte[] writeDataArr = (byte[])msg;
		int len = writeDataArr.length;
		ChannelBuffer cb = ChannelBuffers.buffer(len+4);
		cb.writeInt(len);
		cb.writeBytes(writeDataArr);
		return cb;
	}
}
