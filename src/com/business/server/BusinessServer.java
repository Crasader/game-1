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
 *  ҵ�������
 */
public abstract class BusinessServer {

	public static class allBusiness {

		public final static Map<MessageType, BusinessServer> allBusiness = FactoryUtil
				.newMap();
        //ҵ������map
		public static final String defaultPage = "com.business.server.impl";
        //Ĭ��ҵ�����
	}

	/**
	 * 
	 * @param user
	 * @param msg 
	 * @throws BusinessException
	 *  �����û���Ϣ
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
