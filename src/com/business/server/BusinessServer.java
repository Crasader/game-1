package com.business.server;

import java.util.Map;

import com.business.model.User;
import com.common.util.FactoryUtil;
import com.netty.exception.BusinessException;
import com.netty.model.Message;
import com.netty.model.MessageType;

/**
 * 
 * @author mc
 *  业务抽象类
 */
public abstract class BusinessServer {

	public static class allBusiness {

		public final static Map<MessageType, BusinessServer> allBusiness = FactoryUtil
				.newMap();
        //业务子类map
		public static final String defaultPage = "com.business.server.impl";
        //默认业务包名
	}

	/**
	 * 
	 * @param user
	 * @param msg 
	 * @throws BusinessException
	 *  处理用户消息
	 */
	public abstract void doMsg(User user, Message msg) throws BusinessException;
	
	/**
	 * 
	 * @param msg
	 * @param key
	 * @return
	 * @throws BusinessException
	 *  
	 */
	protected String get(Message msg, String key) throws BusinessException {
		try {
			return msg.getMessage().getString(key);
		} catch (Exception e) {
			return null;
		}
	}
	

}
