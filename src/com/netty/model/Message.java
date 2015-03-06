package com.netty.model;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.common.util.Log;
import com.netty.exception.BusinessException;

public class Message {

	private MessageType messageType;

	private JSONObject message;

	public static Message fromMessage(MessageType messageType) {
		return fromMessage(messageType, null);
	}

	public static Message fromMessage(MessageType messageType,
			Iterable<?> array, String key) {
		JSONObject obj = new JSONObject();
		Object value = JSONArray.fromObject(array);
		obj.put(key, value);
		return fromMessage(messageType, obj);
	}

	public static Message fromMessage(MessageType messageType, Object message) {
		return new Message(messageType, message);
	}

	public static Message toMessage(Object message) throws BusinessException {

		try {
			JSONObject jsonobj = JSONObject.fromObject(message);
			MessageType type = MessageType.valueOf(jsonobj.get("msg")
					.toString());
			jsonobj.remove("msg");
			return fromMessage(type, jsonobj);
		} catch (Exception e) {
			throw new BusinessException(Message.fromMessage(MessageType.NotFound));
		}
	}

	private Message(MessageType messageType, Object message) {
		this.messageType = messageType;
		if (message != null) {
			try {
				this.message = JSONObject.fromObject(message);
			} catch (Exception e) {
				this.message = new JSONObject();
				Log.debug(e.getMessage());

			}
		}

	}

	private Message toErrMsg(JSONObject obj, String errinfo) {
		message = obj;
		message.put("err", errinfo);
		return this;
	}

	public Message toErrMsg(Object errinfo) {
		return toErrMsg(errinfo.toString());
	}

	public Message toErrMsg(String errinfo) {
		return toErrMsg(new JSONObject(), errinfo);
	}

	@Override
	public String toString() {
		if (message == null) {
			toErrMsg(messageType.toString());
		}
		JSONObject temp = JSONObject.fromObject(message.toString());
		temp.put("msg", messageType.name());
		return temp.toString();
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public JSONObject getMessage() {
		return message;
	}

	public void setMessage(JSONObject message) {
		this.message = message;
	}

}
