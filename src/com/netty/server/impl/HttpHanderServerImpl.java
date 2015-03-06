package com.netty.server.impl;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import com.netty.server.GameHanderServer;
import com.netty.server.LogicServer;

public class HttpHanderServerImpl extends GameHanderServer {

	public HttpHanderServerImpl(LogicServer logicServer) {
		super(logicServer);
	}

	@Override
	public void connect(ChannelHandlerContext ctx, ChannelStateEvent e) {

	}

	@Override
	public void doMsg(ChannelHandlerContext ctx, MessageEvent e) {

	}

	@Override
	public void exception(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {

	}

	@Override
	public void cancel(ChannelHandlerContext ctx, ChannelStateEvent e) {

	}

}
