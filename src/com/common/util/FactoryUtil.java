package com.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.business.server.BusinessServer;
import com.netty.exception.BusinessException;
import com.netty.model.ConnectionType;
import com.netty.model.GameHandler;
import com.netty.model.Message;
import com.netty.model.MessageType;
import com.netty.server.impl.FlashHanderServerImpl;
import com.netty.server.impl.FlashLogicServerImpl;
import com.netty.server.impl.HttpHanderServerImpl;
import com.netty.server.impl.HttpLoicServerImpl;
import com.netty.server.impl.SockLogicServerImpl;
import com.netty.server.impl.SocketHanderServerImpl;

/**
 * 
 * @author mc 工厂类
 */
public class FactoryUtil {

	public static <K, V> Map<K, V> newMap() {
		return new HashMap<K, V>();
	}

	public static <L> List<L> newList() {
		return newList(0);
	}

	public static <V> Vector<V> newVector() {
		return new Vector<V>();
	}

	public static <L> List<L> newList(int size) {
		return new ArrayList<L>(size);
	}

	public static <S> Set<S> newSet() {
		return new HashSet<S>();
	}

	/**
	 * 
	 * @param type
	 * @return
	 * @throws BusinessException
	 *             根据MessageType获取一个业务处理类 所有业务处理类都是单例的 如果获取失败,会抛出异常
	 */
	public static BusinessServer getBusiness(MessageType type)
			throws BusinessException {

		if (BusinessServer.allBusiness.allBusiness.containsKey(type)) {
			return BusinessServer.allBusiness.allBusiness.get(type);
		}

		BusinessServer businessServer = null;

		try {
			String className = new StringBuffer()
					.append(BusinessServer.allBusiness.defaultPage).append(".")
					.append(type.name().substring(0, 1).toUpperCase())
					.append(type.name().substring(1)).toString();
			businessServer = (BusinessServer) Class.forName(className)
					.newInstance();
		} catch (Exception e) {
			throw new BusinessException(
					Message.fromMessage(MessageType.NotFound));
		}

		BusinessServer.allBusiness.allBusiness.put(type, businessServer);

		return businessServer;
	}

	public static GameHandler getGameHander(ConnectionType type) {

		if (type == null)
			return null;

		GameHandler g = null;

		switch (type) {
		case http:
			return new GameHandler(new HttpHanderServerImpl(
					new HttpLoicServerImpl()));
		case socket:
			return new GameHandler(new SocketHanderServerImpl(
					new SockLogicServerImpl()));
		case strategy:
			return new GameHandler(new FlashHanderServerImpl(
					new FlashLogicServerImpl()));

		}

		return g;

	}

}
