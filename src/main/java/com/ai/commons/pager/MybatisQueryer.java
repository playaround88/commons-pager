package com.ai.commons.pager;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.session.SqlSession;

public class MybatisQueryer<T> extends AbstractQueryer<T>{
	private SqlSession sqlSession;
	private String querySql;
	private Map<String,String> param;
	
	public MybatisQueryer(SqlSession sqlSession,String querySql, Map<String,String> param){
		this.sqlSession=sqlSession;
		this.querySql=querySql;
		this.param=param;
		//创建结果集对象
		pagedList=new PagedList<T>();
		if(param!=null){//该判断是为了防止反序列化时报错
			//初始化分页参数
			if(param.get("pageSize")!=null && !"".equals(param.get("pageSize"))){
				pagedList.setPageSize(Integer.parseInt(param.get("pageSize")));
			}
			if(param.get("currentPage")!=null&&!"".equals(param.get("currentPage"))){
				pagedList.setCurrentPage(Integer.parseInt(param.get("currentPage")));
			}
		}
	}

	@Override
	public void beforeQuery() {
		//通过pageSize和currentPage计算startNum和endNum
		dealParam(param);
	}

	@Override
	public void afterQuery() {
		//取出拦截器设置的，符合条件的记录总数
		pagedList.setTotalCount(MapUtils.getIntValue(param, "totalCount"));
		//清理资源
		this.sqlSession=null;
	}

	@Override
	public List<T> queryList() {
		return sqlSession.selectList(querySql, param);
	}

	@Override
	public int queryCount() {
		//目前是发出单词查询，直接返回0
		return 0;
	}

	/**
	 * 处理查询参数，计算startNum和endNum
	 */
	private void dealParam(Map<String,String> param){
		//pageSize=-1时，查询所有
		if(pagedList.getPageSize()!=-1){
			//计算startNum和endNum	1<=x<=10
			int startNum=(pagedList.getCurrentPage()-1)*pagedList.getPageSize()+1;
			int endNum=pagedList.getCurrentPage()*pagedList.getPageSize();
			//修改param
			param.put("startNum", String.valueOf(startNum));
			param.put("endNum", String.valueOf(endNum));
		}
	}
}
