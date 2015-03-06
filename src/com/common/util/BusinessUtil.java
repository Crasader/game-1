package com.common.util;

import java.sql.ResultSet;

import com.business.model.Role;
import com.netty.config.ConnectionLevel;
import com.netty.exception.BusinessException;
import com.server.model.Server;

public class BusinessUtil {
	
	/**
	 * 
	 * @param uid
	 * @param pass
	 * @return 是否是一个合法用户
	 * @throws BusinessException 
	 */
	public static void loginCheck(Integer uid,String pass) throws BusinessException{
		
		String sql = "select count(uid) from user where uid = '"+uid+"' and loginpass='"+pass+"'";
		
		
		ResultSet result = SQLUtils.executeQuery(sql, ConnectionLevel.now);
		try {
			while (result.next()) {
				if (result.getInt(1) == 0)
					throw new BusinessException("用户名信息无效,请尝试重新登陆");
			}
		} catch (BusinessException e) {
			throw e;
		}catch (Exception e) {
           Log.error("用户登录校验失败： "+uid+" <> "+pass);
		}
		
		
	}

	public static Role getRole(Integer uid) throws BusinessException {
		Role role = Server.allRole.get(uid);
		if (role == null)
			throw new BusinessException("用户不在线或不存在");
		return role;
	}

	public static Role getRole(Object obj) throws BusinessException {

		if (obj instanceof String) {

			try {
				Integer uid = Integer.parseInt(obj.toString());
				return getRole(uid);
			} catch (Exception e) {
				throw new BusinessException("非法用户名");
			}
		}

		return null;
	}

}
