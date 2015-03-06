package com.business.config;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GameConfig {

	public static final int LoginOut_FengHao = 2;

	public static final int LoginOut_OtherLogin = 1;

	private static SimpleDateFormat formate = new SimpleDateFormat("HH:mm:ss");

	public static String getNowDate() {
		return formate.format(new Date());
	}

}
