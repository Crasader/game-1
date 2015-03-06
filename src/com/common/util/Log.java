package com.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
	private static Logger logger = LoggerFactory.getLogger(Log.class);

	public static void info(Object info) {
		info(info.toString());
	}

	public static void debug(String info) {
		logger.debug(info);
	}

	public static void info(String info) {
		logger.info(info);
	}

	public static void error(String info) {
		logger.error(info);
	}

}
