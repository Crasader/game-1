package com.netty.exception;

import com.netty.model.Message;


/**
 * 
 * @author mc 业务异常,抛出业务异常必须要一个Message
 */
public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	private Message msg;
	
	@SuppressWarnings("unused")
	private BusinessException() {
 
	}

	public BusinessException(String msg) {
		super(msg);
	}
	
	public BusinessException(Message msg){
		super(msg.toString());
		this.msg = msg;
	}

	public Message getMsg() {
		return msg;
	}

	public void setMsg(Message msg) {
		this.msg = msg;
	}

}
