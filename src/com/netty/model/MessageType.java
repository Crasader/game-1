package com.netty.model;

/**
 * 
 * @author mc 请求枚举类, 所有请求必须在这个类里面 否则会抛出异常
 */
public enum MessageType {

	ServerClose("服务器正在关闭"), NotFound("非法请求"), OtherLogin, Error(
			"服务器繁忙"),

	loginGameAction,  //action请求 登陆
	return_userInfo,   //返回玩家信息
	;
	MessageType() {
	}

	MessageType(String message) {
		this.message = message;
	}

	private String message;

	@Override
	public String toString() {
		return message == null ? name() : message;
	}

}
