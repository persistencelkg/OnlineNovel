package org.lkg.service;

import java.io.Serializable;

/**
 * 客户端的业务功能接口
 * @description: 注意:完成一个功能需要返回下一个功能的接口
 * 客户端不能实现runnable  因为多线程是为了能够同时处理多个请求  对于客户端而言
 * 例如:当用户在登录时肯定不能做其他事情
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月5日 下午12:02:47
 * @CopyRight lkg.nb.com
 */
public interface Service<T extends Serializable>{
	
	/**
	 * 获得依赖上一个功能的数据
	 * 由于存在下一个功能可能依赖于上一个功能的数据对象
	 * 例如:上传之前要明确小说的类别
	 * @param data 不确定的数据对象
	 */
	public void setInutData(T data);
	
	
	/**
	 * 客户端的功能
	 * @return 返回下一个功能接口,由于不明确具体的接口,所以使用?代表任意可能的功能
	 */
	public Service<? extends Serializable> execute();
}
