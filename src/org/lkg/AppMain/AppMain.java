package org.lkg.AppMain;

import java.io.Serializable;

import org.lkg.service.Service;
import org.lkg.service.StartService;
import org.lkg.service.impl.ServiceFactory;
import org.lkg.util.SysConstants;

public class AppMain {

	public static void main(String[] args) {
		new AppMain().start();
	}
	
	/**
	 * 开始
	 */
	public void start() {
		Service<? extends Serializable> startService= ServiceFactory.getService(SysConstants.开始);
		while(true) {
			if(startService==null){
				System.out.println("已退出");
				break;
			}
			//循环指向下一个功能直到结束
			startService=startService.execute();
		}
	}

}
