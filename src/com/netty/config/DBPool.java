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
			Log.error("��ȡ���ݿ������ļ�ʧ��");
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

			// ���ӳ��б�������С������
			dataSource.setMinPoolSize(minConnection);
			// ��ʼ��ʱ��ȡ�������ӣ�ȡֵӦ��minPoolSize��maxPoolSize֮��
			dataSource.setInitialPoolSize(75);
			// ���ӳ��б��������������
			dataSource.setMaxPoolSize(maxConnection);
			// �����ӳ��е����Ӻľ���ʱ��c3p0һ��ͬʱ��ȡ��������(����������)
			dataSource.setAcquireIncrement(15);
			// ���ӹر�ʱĬ�Ͻ�����δ�ύ�Ĳ����ع���Ĭ��Ϊfalse
			dataSource.setAutoCommitOnClose(false);

			// // ��ȡ����ʹ�õ�ʱ�䣬�������׳��쳣
			// dataSource.setCheckoutTimeout(5000);

			dataSource.setMaxIdleTime(1);

			// ���Կ�������Դ�ڼ��ص�PreparedStatement���� Ĭ����0
			dataSource.setMaxStatements(0);
			// ���ӳ��ڵ���������ӵ�е���󻺴�Statement�� Ĭ����0
			dataSource.setMaxStatementsPerConnection(0);
			// �����ڴ����ݿ��ȡ������ʧ�ܺ��ظ����Ի�ȡ�Ĵ�����Ĭ��Ϊ30
			dataSource.setAcquireRetryAttempts(30);

			// C3P0���첽�����ģ�������JDBC����ͨ������������ɡ���չ��Щ����������Ч���������ܣ�ͨ�����߳�ʵ�ֶ������ͬʱ��ִ�С�Ĭ��Ϊ3
			dataSource.setNumHelperThreads(10);

			// �����������������ӳ��еĿ������ӣ�Ĭ��Ϊ0��ʾ�����
			dataSource.setIdleConnectionTestPeriod(60);
			// ��ȡ����ʧ�ܽ����������еȴ����ӳ�����ȡ���ӵ��߳��׳��쳣����������Դ����Ч�����������´ε���getConnection()��ʱ��������Ի�ȡ���ӡ�
			// �����Ϊtrue����ô�ڳ��Ի�ȡ����ʧ�ܺ������Դ�������ѶϿ������ùر� Ĭ��Ϊfalse
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
	 * ��ȡ������
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
			Log.info("�ر�ResultSet����");
			Log.info(e.toString());
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				Log.info("�ر�Statement����");
				Log.info(e.toString());
			} finally {
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					Log.info("�ر�Connection����");
					Log.info(e.toString());
				}
			}
		}
	}
}