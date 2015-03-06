package com.netty.server.impl;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import com.business.model.User;
import com.common.util.FormatterUtil;
import com.common.util.Log;
import com.netty.exception.BusinessException;
import com.netty.model.Message;
import com.netty.model.MessageType;
import com.netty.server.GameHanderServer;
import com.netty.server.LogicServer;
import com.server.model.Server;

public class SocketHanderServerImpl extends GameHanderServer {

	public SocketHanderServerImpl(LogicServer logicServer) {
		super(logicServer);
	}

	@Override
	public void connect(ChannelHandlerContext ctx, ChannelStateEvent e) {
		String ip = FormatterUtil.getIp(e.getChannel());
		if (Server.blackListIP.contains(ip)) {
			e.getChannel().close().awaitUninterruptibly();
			return;
		}
		User user = new User(e.getChannel());
		user.setIp(ip);
		e.getChannel().setAttachment(user);

	}

	@Override
	public void doMsg(ChannelHandlerContext ctx, MessageEvent e) {
		String ip = FormatterUtil.getIp(e.getChannel());

		User user = (User) e.getChannel().getAttachment();

		if (Server.blackListIP.contains(ip)) {
			user.write(Message.fromMessage(MessageType.Error));
			user.close();
			return;
		}

		if (Server.Closing) {
			user.write(Message.fromMessage(MessageType.ServerClose));
			return;
		}

		long now = System.currentTimeMillis();
		if (user.getConnCount() > 20) {
			if (now - user.getSocketConnectTime() < 2000) {
				Server.blackListIP.add(ip);
				Log.info(ip + "协议发送过快被拉进ip黑名单,被禁ip数量:"
						+ Server.blackListIP.size());
				user.close().awaitUninterruptibly();
				return;
			}
			user.setConnCount(0);
			user.setSocketConnectTime(now);
		}
		user.setNearSocketTime(now);
		user.setConnCount(user.getConnCount() + 1);

		String info = FormatterUtil.toString(e.getMessage());

		Message msg = null;

		try {
			msg = Message.toMessage(info);
			getLogicserver().doMsg(user, msg);
		} catch (BusinessException err) {
			getLogicserver().doError(user, err.getMsg());
		}

	}

	@Override
	public void exception(ChannelHandlerContext arg0, ExceptionEvent arg1)
			throws Exception {
	}

	@Override
	public void cancel(ChannelHandlerContext ctx, ChannelStateEvent e) {
		try {
			super.channelDisconnected(ctx, e);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
	}

}
