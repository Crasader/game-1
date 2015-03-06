package com.netty.config;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.common.util.FactoryUtil;
import com.common.util.Log;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBPool {
	private static ComboPooledDataSource dataSource = null;
	public static String Mysql_Url = "122.224.6.159:3306";
	private static String Mysql_User = "tigeradmin";// ibm
	private static String Mysql_PW = "9+9+9+9ooo";// fuckibm+1

	private static Vector<Integer> nowConnection = FactoryUtil.newVector();

	private static Vector<Integer> leisureConnection = FactoryUtil.newVector();

	private static String Mysql_DBName = "tiger";

	private static final int maxConnection = 150;
	private static final int minConnection = 30;
	private static final int peakConnection = maxConnection * 3 / 4;

	private static final Map<Integer, Connection> connections = FactoryUtil
			.newMap();

	private static int id = 0;

	static {
		String xmlName = "server";
		String xmlPath = System.getProperty("user.dir") + "/res/" + xmlName
				+ ".xml";
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(new File(xmlPath));
			Element rootElt = doc.getRootElement();
			Mysql_Url = rootElt.elementText("dburl");
			Mysql_User = rootElt.elementText("user");
			Mysql_PW = rootElt.elementText("pass");
			Mysql_DBName = rootElt.elementText("dbname");

		} catch (Exception e) {
			Log.error("读取数据库配置文件失败");
		}

	}

	public static void init() {
		try {
			dataSource = new ComboPooledDataSource();
			dataSource.setDataSourceName(Mysql_DBName);
			dataSource.setDriverClass("com.mysql.jdbc.Driver");

			dataSource
					.setJdbcUrl("jdbc:mysql://"
							+ Mysql_Url
							+ "/"
							+ Mysql_DBName
							+ "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");

			dataSource.setUser(Mysql_User);
			dataSource.setPassword(Mysql_PW);

			// 连接池中保留的最小连接数
			dataSource.setMinPoolSize(minConnection);
			// 初始化时获取三个连接，取值应在minPoolSize与maxPoolSize之间
			dataSource.setInitialPoolSize(75);
			// 连接池中保留的最大连接数
			dataSource.setMaxPoolSize(maxConnection);
			// 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数(新增连接数)
			dataSource.setAcquireIncrement(15);
			// 连接关闭时默认将所有未提交的操作回滚。默认为false
			dataSource.setAutoCommitOnClose(false);

			// // 获取连接使用的时间，超过会抛出异常
			// dataSource.setCheckoutTimeout(5000);

			dataSource.setMaxIdleTime(1);

			// 用以控制数据源内加载的PreparedStatement数量 默认是0
			dataSource.setMaxStatements(0);
			// 连接池内单个连接所拥有的最大缓存Statement数 默认是0
			dataSource.setMaxStatementsPerConnection(0);
			// 定义在从数据库获取新连接失败后重复尝试获取的次数，默认为30
			dataSource.setAcquireRetryAttempts(30);

			// C3P0是异步操作的，缓慢的JDBC操作通过帮助进程完成。扩展这些操作可以有效的提升性能，通过多线程实现多个操作同时被执行。默认为3
			dataSource.setNumHelperThreads(10);

			// 隔多少秒检查所有连接池中的空闲连接，默认为0表示不检查
			dataSource.setIdleConnectionTestPeriod(60);
			// 获取连接失败将会引起所有等待连接池来获取连接的线程抛出异常。但是数据源仍有效保留，并在下次调用getConnection()的时候继续尝试获取连接。
			// 如果设为true，那么在尝试获取连接失败后该数据源将申明已断开并永久关闭 默认为false
			dataSource.setBreakAfterAcquireFailure(true);

			new Thread(new Runnable() {

				@Override
				public void run() {

					int k = 0;

					while (true) {

						try {

							if (!nowConnection.isEmpty()) {

								Iterator<Integer> coniter = nowConnection
										.iterator();
								while (coniter.hasNext()) {
									Integer key = coniter.next();
									connections.put(key,
											dataSource.getConnection());
									coniter.remove();
								}

							}

							if (++k % 10 == 0) {

								k = 0;

								if (!leisureConnection.isEmpty()) {

									Iterator<Integer> coniter = leisureConnection
											.iterator();

									while (coniter.hasNext()) {

										if (dataSource.getNumBusyConnections() > peakConnection
												|| !nowConnection.isEmpty()) {
											break;
										}
										Integer key = coniter.next();
										connections.put(key,
												dataSource.getConnection());
										coniter.remove();
									}

								}

							}

						} catch (Exception e) {

						}

					}

				}
			}).start();

		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取基础库
	 * 
	 * @return
	 */
	public static final Connection getConnection(ConnectionLevel level) {

		if (id > 10000)
			id = 0;

		int temp = id++;

		if (level == ConnectionLevel.now) {
			nowConnection.add(temp);
		} else {
			leisureConnection.add(temp);
		}

		while (connections.get(temp) == null)
			;

		Connection conn = connections.get(temp);

		connections.remove(temp);

		return conn;

	}

	public static void free(ResultSet rs, Statement st, Connection conn) {

		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			Log.info("关闭ResultSet出错");
			Log.info(e.toString());
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				Log.info("关闭Statement出错");
				Log.info(e.toString());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					Log.info("关闭Connection出错");
					Log.info(e.toString());
				}
			}
		}
	}
}