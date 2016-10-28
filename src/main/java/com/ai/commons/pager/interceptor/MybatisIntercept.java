/**
 * 
 */
package com.ai.commons.pager.interceptor;

import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * Title: ai-commons <br>
 * Description: Mybatis连接器工具 <br>
 * Date: 2015年10月4日 <br>
 * Copyright (c) 2015 Asiainfo <br>
 * 
 * @author liujs
 */
public class MybatisIntercept {

	/**
	 * Mybatis连接组件使用
	 * 当有多个连接器时，被连接的对象被多次代理，通过此找到原始的被代理对象
	 * @param invocation
	 * @return
	 */
	public static MetaObject getTarget(Invocation invocation) {
		MetaObject metaObj = SystemMetaObject.forObject(invocation.getTarget());  
        // 分离代理对象链(由于目标类可能被多个拦截器拦截，从而形成多次代理，通过下面的两次循环  
        // 可以分离出最原始的的目标类)  
        while (metaObj.hasGetter("h")) {  
            Object object = metaObj.getValue("h");  
            metaObj = SystemMetaObject.forObject(object);  
        }  
        // 分离最后一个代理对象的目标类  
        while (metaObj.hasGetter("target")) {  
            Object object = metaObj.getValue("target");  
            metaObj = SystemMetaObject.forObject(object);  
        } 
        return metaObj;
	}
}
