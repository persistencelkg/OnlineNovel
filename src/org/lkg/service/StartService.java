package org.lkg.service;

import java.io.Serializable;
import java.util.Scanner;

import org.lkg.service.impl.ServiceFactory;
import org.lkg.util.SysConstants;

/**
 * 
 * 启动程序的开始功能展示选择
 * @description:提供用户看到程序的界面-功能选择 
 * 注:由于启动类并不需要指明具体类型 但是有不可避免添加泛型 所以使用一个Serializable代替本质上没有实际意义
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月5日 下午12:31:40
 */
public class StartService extends BaseServiceImpl<Serializable>{
	/**
	 * 开始界面的提示信息
	 */
	private String OUTPUT_SIGN_LINE = "-------------------------------------\n";
	private String OUTPUT_TEXT_INVALIDINPUT = "你的输入无效，请重新输入！";
	private String OUTPUT_TEXT_SELECT = "请选择： ";
	private String OUTPUT_TEXT_TITLE = "欢迎使用<<Open-在线小说>>\n";
	private String OUTPUT_TEXT_LOGIN = "1.登录\n";
	private String OUTPUT_TEXT_REGISTER = "2.注册\n";
	private String OUTPUT_TEXT_LOGOUT = "3.退出\n";
	private String OUTPUT_TEXT_THANK="谢 谢 使 用！";
	private StringBuilder MENU_START = new StringBuilder(OUTPUT_TEXT_TITLE)
								.append(OUTPUT_SIGN_LINE)
								.append(OUTPUT_TEXT_LOGIN)
								.append(OUTPUT_TEXT_REGISTER)
								.append(OUTPUT_TEXT_LOGOUT)
								.append(OUTPUT_SIGN_LINE)
								.append(OUTPUT_TEXT_SELECT);
	
	@Override
	public Service<? extends Serializable> execute() {
		System.out.println(MENU_START);
		
		@SuppressWarnings("resource")
		Scanner input=new Scanner(System.in);
		String choice=null;
		
		while(true) {
			choice=input.next().trim();
			switch (choice) {
			case "1":
				return ServiceFactory.getService(SysConstants.登录);
			case "2":
				return ServiceFactory.getService(SysConstants.注册);
			case "3":
				System.out.println(OUTPUT_TEXT_THANK);
				System.exit(0);
			default:
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				break;
			}
		
		}
	}

}
