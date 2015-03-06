package com.server.model;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class IODecoder extends FrameDecoder {
	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer buffer) throws Exception {
		int size = buffer.readableBytes();
		if (size < 4 || size >= 256)
		{			
			return null;
		}
		buffer.markReaderIndex();
		int dataLength = buffer.readInt();
		if (dataLength > size - 4) {
			// 数据没有准备好,重置
			buffer.resetReaderIndex();
			return null;
		}
		byte[] decoded = new byte[dataLength];
		buffer.readBytes(decoded);
		return decoded;

	}
}
