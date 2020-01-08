package org.lkg.service;

import java.io.Serializable;
import java.util.Scanner;

/**
 * 
 * 客户端的抽象基类
 * @description: 主要用来添加所有实现Service接口子类的inputData的get/set方法
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月5日 下午12:15:55
 */
public abstract class BaseServiceImpl<T extends Serializable> implements Service<T>{
	protected String OUTPUT_TEXT_SERVERERROR = "服务器故障，请重试！";
	protected String OUTPUT_TEXT_ERROR = "系统存在错误，服务终止！";
	protected String OUTPUT_TEXT_INVALIDINPUT = "你的输入有误，请重新输入！";
	
	protected static Scanner input=new Scanner(System.in);
	private T inputData;
	
	@Override
	public void setInutData(T data) {
		this.inputData=data;
	}

	public T getInputData() {
		return inputData;
	}
	
}
