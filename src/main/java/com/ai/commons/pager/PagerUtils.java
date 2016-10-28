package com.ai.commons.pager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 分页工具类
 * @author wu
 *
 */
public class PagerUtils {

	/**
	 * 构建查询分页参数
	 * @param request
	 * @param params
	 */
	public static void buildPageParam(HttpServletRequest request, Map<String,String> params){
		params.put("currentPage", request.getParameter("currentPage"));
		params.put("pageSize", request.getParameter("pageSize"));
	}
	
	public static void buildPageParamBs(HttpServletRequest request, Map<String,String> params){
		params.put("currentPage", request.getParameter("pageNumber"));
		params.put("pageSize", request.getParameter("pageSize"));
	}
	
	public static void save2Request(HttpServletRequest request,PagedList pagedList, String listName){
		saveCommon(request,pagedList);
		request.setAttribute(listName, pagedList.getResult());
	}
	private static void saveCommon(HttpServletRequest request,PagedList pagedList){
		request.setAttribute("pageSize", pagedList.getPageSize());
		request.setAttribute("currentPage", pagedList.getCurrentPage());
		request.setAttribute("totalCount", pagedList.getTotalCount());
		request.setAttribute("totalPage",pagedList.getTotalPage());
		//html页面，分页组件
		//	request.setAttribute("pager", getPager());
	}
	/**
	 * 构建bootstrap-table的分页结果集，如下
	 * {“total":100, "rows":[{}]}
	 * @param pList
	 */
	public static HashMap buildResultBs(PagedList pList) {
		HashMap result=new HashMap();
		int totalCount = 0;
		List list = new ArrayList();
		if(pList != null) {
			totalCount = pList.getTotalCount();
			list = pList.getResult();
		}
		result.put("total", totalCount);
		result.put("rows", list);
		return result;
	}
}
