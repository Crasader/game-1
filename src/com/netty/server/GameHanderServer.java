package com.netty.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;


public abstract class GameHanderServer extends SimpleChannelUpstreamHandler{

	private LogicServer logicserver;

	public GameHanderServer(LogicServer logicServer) {

		this.logicserver = logicServer;

	}

	
	public abstract void connect(ChannelHandlerContext ctx,
			ChannelStateEvent e);

	
	public abstract void doMsg(ChannelHandlerContext ctx, MessageEvent e);

	
	public abstract void exception(ChannelHandlerContext arg0,
			ExceptionEvent arg1) throws Exception;

	public abstract void cancel(ChannelHandlerContext ctx,
			ChannelStateEvent e);

	public LogicServer getLogicserver() {
		return logicserver;
	}

	public void setLogicserver(LogicServer logicserver) {
		this.logicserver = logicserver;
	}

}
