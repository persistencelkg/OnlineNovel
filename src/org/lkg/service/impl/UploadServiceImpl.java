package org.lkg.service.impl;

import java.io.Serializable;

import org.lkg.entity.Classifcation;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;

/**
 * 对小说进行上传请求
 * 
 * @description: 需要告知上传的类别
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月7日 上午10:48:21
 * @CopyRight lkg.nb.com
 */
public class UploadServiceImpl extends BaseServiceImpl<Classifcation>{

	@Override
	public Service<? extends Serializable> execute() {
		System.out.println(getInputData().getClassName()+"类即将进行一次上传");
		return null;
	}

}
