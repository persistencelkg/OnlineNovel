package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import org.lkg.entity.SysDTO;
import org.lkg.entity.Users;
import org.lkg.protocal.Protocol;
import org.lkg.protocal.ProtocolImpl;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;
import org.lkg.util.GetProperies;
import org.lkg.util.ResultStatus;
import org.lkg.util.SysConstants;

public class RegisterServiceImpl extends BaseServiceImpl<Serializable>{
	private String OUTPUT_TEXT_USERNAME = "请输入用户名:";
	private String OUTPUT_TEXT_PASSWORD = "请输入密码:";
	private String OUTPUT_TEXT_PASSWORD2 = "请再次输入密码:";
	private String OUTPUT_TEXT_USEREXIST = "用户名已存在，请重新注册！";
	private String OUTPUT_TEXT_PASSWORD_NOT_EQUAL = "两次密码不一样！";
	private String OUTPUT_USER_SAVESUCESS = "用户注册成功，请登录！";
	private String OUTPUT_USER_SAVEFAIL = "用户注册失败，请重新注册！";
	
	@Override
	public Service<? extends Serializable> execute() {
		while(true) {
			System.out.println(OUTPUT_TEXT_USERNAME);
			String uname=input.next().trim();
			System.out.println(OUTPUT_TEXT_PASSWORD);
			String upass=input.next().trim();
			System.out.println(OUTPUT_TEXT_PASSWORD2);
			String ensurePass=input.next().trim();
			
			if(uname.length()==0||upass.length()==0||ensurePass.length()==0) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}else if(!upass.equals(ensurePass)) {
				System.out.println(OUTPUT_TEXT_PASSWORD_NOT_EQUAL);
				continue;
			}
			
			Users users=new Users(uname,upass);
			
			//进行与客户端的通信
			ResultStatus result=communite(users);
			if(result==ResultStatus.REGIST_SUCCESS) {
				//成功直接过渡到登录
				return ServiceFactory.getService(SysConstants.登录);
			}else if(result==ResultStatus.REGIST_FAIL||result==ResultStatus.USERNAME_EXIST) {
				System.out.println(OUTPUT_TEXT_USEREXIST);
				continue;
			}else{
				System.out.println(OUTPUT_TEXT_SERVERERROR);
				continue;
			}
		}
		
	}

	/**
	 * 客户端与服务端 关于注册的交流事项
	 * @param users
	 * @return
	 */
	private ResultStatus communite(Users users) {
		//客户端封装数据传输对象
		SysDTO<Users> dto=new SysDTO<>();
		dto.setData(users);
		dto.setType(SysConstants.注册);
		
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
