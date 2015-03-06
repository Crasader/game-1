package com.server.model;

import java.net.InetSocketAddress;
import java.util.Map;

import org.jboss.netty.bootstrap.ServerBootstrap;

import com.common.util.Log;

public class RunServer {

	private int port;

	private ServerBootstrap bootstrap;

	private Map<String, Object> config;


	public RunServer(int port, ServerBootstrap bootstrap,
			Map<String, Object> cfg ) {
		this.port = port;
		this.bootstrap = bootstrap;
		this.config = cfg;
		init();
	}

	private void init() {
		if (config != null) {
			for (String key : config.keySet()) {
				try {
					bootstrap.setOption(key, config.get(key));
				} catch (Exception e) {
					Log.debug(e.getMessage());
				}
			}
		}
		bootstrap.bind(new InetSocketAddress(port));
		Log.info("server start: " + port);
	}

}
