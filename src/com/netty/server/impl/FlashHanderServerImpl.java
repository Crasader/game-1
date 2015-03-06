package com.netty.server.impl;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;

import com.common.util.Log;
import com.netty.model.ConnectionInfo;
import com.netty.server.GameHanderServer;
import com.netty.server.LogicServer;
import com.server.model.Server;

public class FlashHanderServerImpl extends GameHanderServer {

	public FlashHanderServerImpl(LogicServer logicServer) {
		super(logicServer);
	}

	static byte asData[];
	
	static {
		String xml = "<cross-domain-policy> "
				+ "<allow-access-from domain=\"*\" to-ports=\"*\"/>"
				+ "</cross-domain-policy> ";

		asData = xml.getBytes();
		Log.debug("__________________xmlLength:" + asData.length);
	}

	@Override
	public void connect(ChannelHandlerContext ctx, ChannelStateEvent e) {
		String ip = e.getChannel().getRemoteAddress().toString().split(":")[0];

		if (Server.blackListIP.contains(ip)) {
			e.getChannel().close().awaitUninterruptibly();
			return;
		}

		long now = System.currentTimeMillis();

		ConnectionInfo conInfo = Server.conIp.get(ip);

		if (conInfo != null) {

			conInfo.addCount();

			if (conInfo.getCount() > 50) {

				if (now - conInfo.getBeforTime() < 2000) {
					Server.blackListIP.add(ip);
					Log.info(ip + "被加入黑名单(获取策略文件过快),ip黑名单总数量: "
							+ Server.blackListIP.size());
					e.getChannel().close().awaitUninterruptibly();
					return;
				}

				conInfo.setCount(0);
			}

			conInfo.setBeforTime(now);

		} else {
			ConnectionInfo info = new ConnectionInfo();
			info.setBeforTime(now);
			info.setCount(0);
			info.setIp(ip);
			Server.conIp.put(ip, info);
		}

		e.getChannel().write(asData).awaitUninterruptibly();
		Log.info(ip + "获取策略文件,asCount:" + conInfo.getCount());
		e.getChannel().close();
	}

	@Override
	public void doMsg(ChannelHandlerContext ctx, MessageEvent e) {
		e.getChannel().close().awaitUninterruptibly();
		return;
	}

	@Override
	public void exception(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		ctx.getChannel().close();
	}

	@Override
	public void cancel(ChannelHandlerContext ctx, ChannelStateEvent e) {
		try {
			super.channelDisconnected(ctx, e);
		} catch (Exception e1) {

		}
	}

}
