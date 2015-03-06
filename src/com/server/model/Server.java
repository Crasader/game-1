package com.server.model;

import java.util.Map;
import java.util.Set;

import com.business.model.Role;
import com.business.model.Room;
import com.business.model.User;
import com.common.util.FactoryUtil;
import com.common.util.Log;
import com.netty.model.ConnectionInfo;
import com.netty.model.SchedulerImpl;
import com.netty.server.Scheduler;

public class Server {

	/**
	 * blacklist ip
	 */
	public static Set<String> blackListIP = FactoryUtil.newSet();

	public static Set<Integer> blackListID = FactoryUtil.newSet();

	/**
	 * server is Closing
	 */
	public static boolean Closing = false;

	/**
	 * all Connection ip
	 */
	public static Map<String, ConnectionInfo> conIp = FactoryUtil.newMap();

	/**
	 * all Connection user
	 */
	public static Map<Integer, User> allUser = FactoryUtil.newMap();

	/**
	 * all open Room
	 */
	public static Map<String, Room> allRoom = FactoryUtil.newMap();

	public static Map<Integer, Role> allRole = FactoryUtil.newMap();

	public static Scheduler scheduler = new SchedulerImpl();

	private static int count = 0;

	static {
		scheduler.submit(Scheduler.ONE_MINUTE_MILLISECOND, new Runnable() {

			@Override
			public void run() {
				if (count++ % 10 == 0) {
					count = 0;
					blackListIP.clear();
					blackListID.clear();
				}

			}
		});
	}

	public static void Close() {

		Log.info("准备关闭服务器,通知每个房间关闭");

		Closing = true;

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (allRoom.size() == 0) {
						Log.info("服务器关闭");
						System.exit(0);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}

				}
			}
		}).start();

	}

}
