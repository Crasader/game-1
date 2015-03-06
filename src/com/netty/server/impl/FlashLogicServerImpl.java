package com.netty.server.impl;

import com.business.model.User;
import com.netty.exception.BusinessException;
import com.netty.model.Message;
import com.netty.server.LogicServer;

public class FlashLogicServerImpl implements LogicServer {

	@Override
	public void doMsg(User user, Message msg) throws BusinessException {
		
	}

	@Override
	public void doError(User user, Message msg) {
		
	}



}
