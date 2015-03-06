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
 * SQL语句工具类
 * 
 * @author wangli
 * @creation 2013-7-4
 */
public class SQLUtils {

	/**
	 * 执行sql 语句，不需要返回值 用户不需要返回结果的语句，例如：更新和插入
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
	 * 执行sql，返回值
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
			Log.error("批量执行sql失败");
			Log.error(sqls.toString());
		} finally {
			DBPool.free(null, statement, conn);
		}
	}
}
