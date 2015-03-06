package com.common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import com.netty.config.ConnectionLevel;
import com.netty.config.DBPool;
import com.sun.rowset.CachedRowSetImpl;

/**
 * SQL��乤����
 * 
 * @author wangli
 * @creation 2013-7-4
 */
public class SQLUtils {

	/**
	 * ִ��sql ��䣬����Ҫ����ֵ �û�����Ҫ���ؽ������䣬���磺���ºͲ���
	 * 
	 * @param sql
	 */
	public static boolean execute(String sql, ConnectionLevel level) {
		Connection conn = DBPool.getConnection(level);
		try {
			if (conn == null || conn.isClosed())
				return false;
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			Log.error("sql exe error: " + sql);
			return false;
		} finally {
			DBPool.free(null, statement, conn);
		}
		return true;
	}

	public static CachedRowSet executeQuery(StringBuffer sql,
			ConnectionLevel level) {
		return executeQuery(sql.toString(), level);
	}

	/**
	 * ִ��sql������ֵ
	 * 
	 * @param cond
	 * @param sql
	 * @return
	 */
	public static CachedRowSet executeQuery(String sql, ConnectionLevel level) {
		Connection conn = DBPool.getConnection(level);
		try {
			if (conn == null || conn.isClosed())
				return null;
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
		Statement statement = null;
		ResultSet rs = null;
		CachedRowSet crs = null;
		try {
			statement = conn.createStatement();
			rs = statement.executeQuery(sql);
			crs = new CachedRowSetImpl();
			crs.populate(rs);

		} catch (SQLException e) {
			Log.error("sql exe error: " + sql);
		} finally {
			DBPool.free(rs, statement, conn);
		}
		return crs;
	}

	public static void execute(List<String> sqls, ConnectionLevel level) {

		Connection conn = DBPool.getConnection(level);
		Statement statement = null;
		try {
			statement = conn.createStatement();
			for (String sql : sqls) {

				statement.addBatch(sql);
			}
			statement.executeBatch();
		} catch (SQLException e) {
			Log.error("����ִ��sqlʧ��");
			Log.error(sqls.toString());
		} finally {
			DBPool.free(null, statement, conn);
		}
	}
}
