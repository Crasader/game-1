package com.business.model;

import java.util.Iterator;

import net.sf.json.JSONObject;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import com.business.config.GameConfig;
import com.netty.exception.BusinessException;
import com.netty.model.Message;
import com.netty.model.MessageType;
import com.server.model.Server;

public class User {

	private Room room;

	private Channel channel; // 通道

	private Role role; // 角色

	private String ip;

	private Integer uid;

	private String mail;

	private int connCount;

	private long socketConnectTime;

	private long nearSocketTime;

	private String pw;

	public boolean isOpen() {
		return channel.isOpen();
	}

	public ChannelFuture close() {
		return channel.close();
	}

	public User(Channel channel) {
		this.channel = channel;
	}

	public ChannelFuture write(Message msg) {
		if (channel == null)
			return null;
		return write(msg.toString());
	}

	private ChannelFuture write(String str) {

		try {
			return write(str.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}

	}

	private ChannelFuture write(byte[] bytes) {
		try {
			return channel.write(bytes);
		} catch (Exception e) {
			return null;
		}
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void installRole() throws BusinessException {

		if (role == null) {
			role = new Role(this);
		}

	}

	public void loginOut(final int state) {

		Server.scheduler.submit(new Runnable() {

			@Override
			public void run() {
				if (isOpen()) {
					{
						String info = null;

						if (state == 1) {
							info = "您的账号已在其他地方登陆";
						}

						if (state == 2) {
							info = "您的账号已被管理员封禁";
						}
						JSONObject json = new JSONObject();
						json.put("err", info);

						write(Message.fromMessage(MessageType.OtherLogin, json))
								.awaitUninterruptibly();
						channel.close();
					}
				}
			}
		});

	}

	public void copy(final User oldUser, User newUser) throws BusinessException {

		if (oldUser == null || oldUser.channel == null)
			throw new BusinessException(Message.fromMessage(MessageType.Error));
		if (oldUser.channel == newUser.channel)
			throw new BusinessException("请勿重复登陆");

		Iterator<User> oldusers = oldUser.getRoom().getUserlist().iterator();
		while (oldusers.hasNext()) {
			if (oldusers.next() == oldUser)
				oldusers.remove();
		}

		newUser.role = oldUser.role;

		newUser.role.setUser(newUser);

		oldUser.loginOut(GameConfig.LoginOut_OtherLogin);

	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPw() {
		return pw;
	}

	public void setPw(String pw) {
		this.pw = pw;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public String toString() {
		return uid.toString();
	}

	public int getConnCount() {
		return connCount;
	}

	public void setConnCount(int connCount) {
		this.connCount = connCount;
	}

	public long getSocketConnectTime() {
		return socketConnectTime;
	}

	public void setSocketConnectTime(long socketConnectTime) {
		this.socketConnectTime = socketConnectTime;
	}

	public long getNearSocketTime() {
		return nearSocketTime;
	}

	public void setNearSocketTime(long nearSocketTime) {
		this.nearSocketTime = nearSocketTime;
	}

}
