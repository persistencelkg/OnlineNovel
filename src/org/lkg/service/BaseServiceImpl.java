package org.lkg.service;

import java.io.Serializable;

/**
 * 
 * 客户端的抽象基类
 * @description: 主要用来添加所有实现Service接口子类的inputData的get/set方法
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月5日 下午12:15:55
 */
public abstract class BaseServiceImpl<T extends Serializable> implements Service<T>{

	private T inputData;
	
	@Override
	public void setInutData(T data) {
		this.inputData=data;
	}

	public T getInputData() {
		return inputData;
	}
}
