package com.netty.server;

import com.business.model.User;
import com.netty.exception.BusinessException;
import com.netty.model.Message;

public interface LogicServer {

	void doMsg(User user, Message msg) throws BusinessException;

	void doError(User user, Message msg);

}
