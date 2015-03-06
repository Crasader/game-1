package com.netty.model;

/**
 * 
 * @author mc ����ö����, ���������������������� ������׳��쳣
 */
public enum MessageType {

	ServerClose("���������ڹر�"), NotFound("�Ƿ�����"), OtherLogin, Error(
			"��������æ"),

	loginGameAction,  //action���� ��½
	return_userInfo,   //���������Ϣ
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
