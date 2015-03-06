package com.common.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.common.model.CallBack;

public class LogicHandler implements InvocationHandler {

	private Object object;

	private CallBack callback;

	private Object newinstance;

	public LogicHandler(Object object, CallBack callback) {
		this.object = object;
		this.callback = callback;

	}

	public Object newProxyInstance() {
		if (newinstance == null) {
			newinstance = Proxy.newProxyInstance(object.getClass()
					.getClassLoader(), object.getClass().getInterfaces(),
					this);
		}
		return newinstance;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object ret = null;
		try {
			ret = method.invoke(object, args);
			callback.callBack(method, ret);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return ret;
	}

}
