package com.ai.commons.pager;

import java.util.List;

abstract class AbstractQueryer<T> implements IQueryer<T>{
	protected PagedList<T> pagedList;

	@Override
	public PagedList<T> getPageList() {
		return this.pagedList;
	}
	@Override
	public void query() {
		beforeQuery();
		//数据库查询
		List<T> result=queryList();
		int totalCount=queryCount();
		//实例化结果集对象
		if(this.pagedList==null){
			this.pagedList=new PagedList<T>();
		}
		//回设结果集和总记录数
		this.pagedList.setResult(result);
		this.pagedList.setTotalCount(totalCount);
		afterQuery();
	}
	//预留声明周期函数
	public abstract void beforeQuery();
	public abstract void afterQuery();
	//数据库查询
	public abstract List<T> queryList();
	public abstract int queryCount();
}
