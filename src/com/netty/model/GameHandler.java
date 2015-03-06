package com.netty.model;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.netty.server.GameHanderServer;

public class GameHandler extends SimpleChannelUpstreamHandler {

	private GameHanderServer game;

	public GameHandler(GameHanderServer gameHanderServer) {
		this.game = gameHanderServer;
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		game.connect(ctx, e);
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		game.doMsg(ctx, e);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		game.cancel(ctx,e);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, ExceptionEvent arg1)
			throws Exception {
		game.exception(arg0, arg1);
	}
	
	
   
}
