package com.netty.config;

import com.business.model.Room;
import com.netty.exception.BusinessException;
import com.server.model.Server;

/**
 * 
 * @author mc ·þÎñÆ÷ÅäÖÃ
 */
public class ServerConfig {

	public static final int HttpPort = 8080;

	public static final int SocketPort = 1010;

	public static void initConfig() throws BusinessException {
		// TODO
		Room test = new Room("1", "test", true);
		Server.allRoom.put(test.getRoomId(), test);
	}

}
