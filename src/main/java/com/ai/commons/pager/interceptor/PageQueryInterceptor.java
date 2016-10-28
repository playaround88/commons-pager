package com.ai.commons.pager.interceptor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

/**
 * 分页拦截器
 */
@Intercepts({
	@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})
})  
public class PageQueryInterceptor implements Interceptor {
	private Properties properties;

	@SuppressWarnings("unchecked")
	public Object intercept(Invocation invocation) throws Throwable {
		if(invocation.getTarget() instanceof StatementHandler) {
	        MetaObject metaStatementHandler = MybatisIntercept.getTarget(invocation);
	        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");  
	        StatementHandler statementHandler = (StatementHandler)metaStatementHandler.getOriginalObject();
			if (statementHandler instanceof RoutingStatementHandler) {
				//拦截到的prepared sql
				final BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");  
				String preSql = boundSql.getSql();
	
				Object paramObj = boundSql.getParameterObject();
				if (!(paramObj instanceof Map)) {//如果查询参数不是Map，直接返回
					return invocation.proceed();
				}
				Map<String, String> param = (Map<String, String>) boundSql.getParameterObject();
				//如果不分页，直接调用返回
				if (!isPage(param)) {
					return invocation.proceed();
				}
				//包装生成分页的查询语句
				final String pageSql = genPagedSql(preSql, param);
				metaStatementHandler.setValue("delegate.boundSql.sql", pageSql);
				
				//查询总数
				Connection conn = (Connection) invocation.getArgs()[0];
				setTotalCount(preSql, mappedStatement, boundSql, conn);
			}
		}
		return invocation.proceed();
	}

	/**
	 * 是否分页
	 */
	private boolean isPage(Map<String, String> param) {
		if (param.containsKey("startNum") && param.containsKey("endNum")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 生成分页sql
	 * @param preSql
	 * @param param
	 * @return
	 */
	private String genPagedSql(String preSql, Map<String, String> param) {
		String result;//待返回结果
		//分页参数
		String startNum = param.get("startNum");
		String endNum = param.get("endNum");
		String pageSize = param.get("pageSize");
		//拦截器配置的数据库类型
		String dbType = properties.getProperty("dbType");
		//拼分页语句
		if (dbType.equalsIgnoreCase("oracle")) {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from (select a.* ,rownum r from (");
			sb.append(preSql);
			sb.append(") a where rownum <= ");
			sb.append(endNum);
			sb.append(")where r>=");
			sb.append(startNum);
			result = sb.toString();
			//pageSql="select * from (select a.* ,rownum r from ("+psql+") a where rownum <= "+endNum+")where r>="+startNum;
		} else {
			result = preSql + "limit " + startNum + "," + pageSize;
		}
		return result;
	}

	/**
	 * 生成简单的count语句
	 */
	private String genCountSql(String sql) {
		return "select count(*) from(" + sql + ")";
	}

	/**
	 *查询总数
	 */
	private void setTotalCount(String preSql, MappedStatement ms, BoundSql boundSql, Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			//构造查询总数SQL
			String countSql = this.genCountSql(preSql);  
			//构造ParameterHandler，用于设置查询条件
			@SuppressWarnings("unchecked")
			Map<String, String> param = (Map<String, String>)boundSql.getParameterObject();
			List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();    
			BoundSql countBoundSql = new BoundSql(ms.getConfiguration(), countSql, parameterMappings, param);    
			ParameterHandler parameterHandler = new DefaultParameterHandler(ms, param, countBoundSql);  
			
			param.put("totalCount", "0");
			
			pstmt = conn.prepareStatement(countSql);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				int totalRecord = rs.getInt(1);
				param.put("totalCount", totalRecord + "");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Object plugin(Object target) {
		if (target instanceof StatementHandler || target instanceof ResultSetHandler) {  
            return Plugin.wrap(target, this);  
        } else {  
            return target;  
        }
	}

	public void setProperties(Properties properties0) {
		this.properties = properties0;
	}
}
