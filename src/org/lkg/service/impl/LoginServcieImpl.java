package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.lkg.entity.SysDTO;
import org.lkg.entity.Users;
import org.lkg.protocal.Protocol;
import org.lkg.protocal.ProtocolImpl;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;
import org.lkg.util.GetProperies;
import org.lkg.util.ResultStatus;
import org.lkg.util.StringUtil;
import org.lkg.util.SysConstants;

/**
 * 客户端-登录实现类
 * @description: 由于并不需要指明泛型的目标 可以简单给个序列化接口
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月6日 上午9:13:19
 * @CopyRight lkg.nb.com
 */
public class LoginServcieImpl extends BaseServiceImpl<Serializable>{

	/**
	 * 登录界面提示
	 */
	private String OUTPUT_TEXT_USERNAME = "请输入登录名：";
	private String OUTPUT_TEXT_PASSWORD = "请输入密码：";
	private String OUTPUT_TEXT_INVALIDINPUT = "你的输入无效，请重新输入！";
	private String OUTPUT_TEXT_SUCCESS = "登录成功！";
	private String OUTPUT_TEXT_FAILED = "用户名或密码错误，请重新输入！";
	private String OUTPUT_TEXT_SERVERERROR = "服务器故障，请重试！";
	private String OUTPUT_TEXT_ERROR = "系统存在错误，服务终止！";
	
	@Override
	public Service<? extends Serializable> execute() {
		while(true) {
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			System.out.print(OUTPUT_TEXT_USERNAME);
			String uname=input.next().trim();
			System.out.print(OUTPUT_TEXT_PASSWORD);
			String pass=input.next().trim();
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			//良好的习惯:客户端首先对用户的输入进行初步判断,以减少服务器的资源浪费
			if(StringUtil.isEmpty(uname)||StringUtil.isEmpty(pass)) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}
			
			//封装数据传输对象
			Users users=new Users(uname,pass);
			//开始交流
			ResultStatus result=communicate(users);
			
			if(result==ResultStatus.LOGIN_SUCCESS) {
				//小说分类
				System.out.println(OUTPUT_TEXT_SUCCESS);
				return ServiceFactory.getService(SysConstants.小说分类);
			}else if(result==ResultStatus.USERNAME_NOT_EXIST||
					result==ResultStatus.LOGIN_FAIL) {
				System.out.println(OUTPUT_TEXT_FAILED);
				continue;
			}else {
				//服务端出现问题导致反馈了不可预料的结果
				System.out.println(OUTPUT_TEXT_SERVERERROR);
				continue;
			}
		}
		
		
		
		
	
	}
	
	
	/**
	 * 客户端与服务端交流以及反馈结果
	 * @param users 传入客户端登录用户
	 * @return 反馈服务端响应结果
	 */
	private ResultStatus communicate(Users users) {
		//客户端封装数据传输对象
		SysDTO<Users> dto=new SysDTO<>();
		dto.setData(users);
		dto.setType(SysConstants.登录);
		
		//开始传输
		Protocol<Users, ? extends Serializable> transform=new ProtocolImpl<>();
		//服务端返回响应结果
		SysDTO<?> reponse=null;
		try {
			transform.Init(
					GetProperies.getValue("socket.server.ip"),
					Integer.valueOf(GetProperies.getValue("socket.server.port")));
			
			//客户端-服务端已经通信
			reponse=transform.Communiate(dto);
			
		} catch (NumberFormatException | ClassNotFoundException e) {
			//这部分是由于配置文件或者其他致命性的程序错误需要立即结束程序
			System.out.println(OUTPUT_TEXT_ERROR);
			System.exit(1);
		}catch (IOException e) {
			//服务器异常回到startService功能处即可
			System.out.println(OUTPUT_TEXT_SERVERERROR);
			e.printStackTrace();
			return ResultStatus.SERVER_ERROR;
		}finally {
			//一次通信结束的善后工作
			try {
				transform.destory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ResultStatus result=reponse.getResust();
		return result;
	}
}
