package com.netty.server.impl;

import com.business.model.User;
import com.business.server.BusinessServer;
import com.common.util.FactoryUtil;
import com.netty.exception.BusinessException;
import com.netty.model.Message;
import com.netty.server.LogicServer;

public class SockLogicServerImpl implements LogicServer {

	@Override
	public void doMsg(User user, Message msg) throws BusinessException {

		try {
			
			if(user.getRoom()!=null&&user.getRoom().isClosing()&&user.getRole().getIsAdmin()==0){
				throw new BusinessException("房间"+user.getRoom().getName()+"正在关闭中。。。");
			}
			
			BusinessServer businessServer = FactoryUtil.getBusiness(msg
					.getMessageType());

			businessServer.doMsg(user, msg);
		} catch (Exception e) {
			
			 if(e instanceof BusinessException){
				 
				 BusinessException temp = (BusinessException) e;
				 
				 if(temp.getMsg()==null){
					 temp.setMsg(msg.toErrMsg(temp.getMessage()));
				 }
				 
				 throw temp;
				 
			 }else{
				 throw new BusinessException(msg.toErrMsg("非法请求"));
			 }
			
		}

	}

	@Override
	public void doError(User user, Message msg) {
		user.write(msg);
	}

}
