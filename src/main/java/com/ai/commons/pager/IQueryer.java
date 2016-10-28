package com.ai.commons.pager;


/**
 * 分页查询器
 * @author wu
 *
 */
public interface IQueryer<T> {
	/**
	 * 得到查询结果
	 * @return
	 */
	public PagedList<T> getPageList();
	/**
	 * 真正发出数据库查询
	 */
	public void query();

}
