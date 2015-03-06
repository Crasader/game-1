package com.common.util;

import org.jboss.netty.channel.Channel;

/**
 *  
 * @author mc
 * 格式化工具类
 */
public class FormatterUtil {

	
	public static Integer toInteger(String str){
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getIp(Channel channel) {
		return channel.getRemoteAddress().toString().split(":")[0];
	}

	/**
	 * 
	 * @param obj
	 * @return new String(byte[],"utf-8");
	 */
	public static String toString(Object obj) {

		String info = null;
		try {
			if (obj instanceof String) {
				info = obj.toString();
				info = new String(info.getBytes("utf-8"));
			} else if (obj instanceof byte[]) {
				info = new String((byte[]) obj, "utf-8");
			} else {
				info = obj.toString();
			}

		} catch (Exception e) {

		}

		return info;

	}

}
