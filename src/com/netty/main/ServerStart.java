package com.netty.main;

import static com.netty.config.ServerConfig.HttpPort;
import static com.netty.config.ServerConfig.SocketPort;

import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.common.util.FactoryUtil;
import com.netty.config.DBPool;
import com.netty.config.ServerConfig;
import com.netty.model.ConnectionType;
import com.server.model.IOASEncoder;
import com.server.model.IODecoder;
import com.server.model.IOEncoder;
import com.server.model.RunServer;

/**
 * 
 * @author mc 服务器主程序
 */
public class ServerStart {

	private static Map<String, Object> cfg = FactoryUtil.newMap();

	static {
		cfg.put("reuseAddress", true);
		cfg.put("child.tcpNoDelay", true);
	}

	public static void main(String[] args) throws Exception {
		DBPool.init();
		ServerConfig.initConfig();
		initNettyServer();
		initNettyServer(843);
		//inithttpServer();

	}

	/**
	 * 
	 * 开放843端口,给flash获取策略文件
	 */
	static void initNettyServer(int port) {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool(), Runtime.getRuntime()
								.availableProcessors() * 4));
		ChannelPipeline channelPipeline = Channels.pipeline(new IODecoder(),
				new IOASEncoder(),
				FactoryUtil.getGameHander(ConnectionType.strategy));
		bootstrap.setPipeline(channelPipeline);
		new RunServer(port, bootstrap, cfg);
	}

	static void initNettyServer() {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool(), Runtime.getRuntime()
								.availableProcessors() * 4));

		ChannelPipeline channelPipeline = Channels.pipeline(new IODecoder(),
				new IOEncoder(),
				FactoryUtil.getGameHander(ConnectionType.socket));
		bootstrap.setPipeline(channelPipeline);

		new RunServer(SocketPort, bootstrap, cfg);

	}

	static void inithttpServer() {
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decoder", new IODecoder());
				pipeline.addLast("encoder", new IOEncoder());
				pipeline.addLast("handler",
						FactoryUtil.getGameHander(ConnectionType.http));
				return pipeline;
			}
		});

		new RunServer(HttpPort, bootstrap, null);

	}

}
