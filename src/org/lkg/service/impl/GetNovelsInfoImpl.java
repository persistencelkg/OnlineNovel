package org.lkg.service.impl;

import java.io.Serializable;

import org.lkg.entity.Classifcation;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;

/**
 * 获取所有小说信息的请求
 * @description: 由于请求目标是分类 所以需要 泛型实例为Classifcation
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月6日 下午9:30:12
 * @CopyRight lkg.nb.com
 */
public class GetNovelsInfoImpl extends BaseServiceImpl<Classifcation>{

	@Override
	public Service<? extends Serializable> execute() {
		System.out.println("即将获得"+getInputData().getClassName()+"的分类信息");
		return null;
	}

}
