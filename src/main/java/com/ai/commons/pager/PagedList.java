package com.ai.commons.pager;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果集
 * @author wu
 *
 */
public class PagedList<T> implements Serializable{
	private List<T> result;//结果集
	private int pageSize=20;//分页大小
	private int currentPage=1;//当前页
	private int totalPage;//总页数
	private int totalCount;//总记录数
	
	/**
	 * 获取查询到的列表
	 * @return
	 */
	public List<T> getResult(){
		return this.result;
	}
	public void setResult(List<T> result){
		this.result=result;
	}
	
	//---------页面相关分页参数----------
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		//计算总页数
		this.totalPage=(int)Math.ceil((double)totalCount/pageSize);
	}
}
