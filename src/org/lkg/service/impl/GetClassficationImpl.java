package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import org.lkg.entity.Classifcation;
import org.lkg.entity.SysDTO;
import org.lkg.protocal.Protocol;
import org.lkg.protocal.ProtocolImpl;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;
import org.lkg.util.GetProperies;
import org.lkg.util.SysConstants;

/**
 * 客户端请求小说分类信息
 * @description:
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月6日 下午8:27:50
 * @CopyRight lkg.nb.com
 */
public class GetClassficationImpl extends BaseServiceImpl<Serializable>{

	private String OUTPUT_SIGN_LINE = "-------------------------------------\n";
	private String OUTPUT_SIGN_SELECT = "请选择：";
	private String OUTPUT_TEXT_RETURN = "0.退出登录";
	private StringBuilder MENU_TEXT_SORT_BEGIN = new StringBuilder(OUTPUT_SIGN_LINE)
										.append(OUTPUT_TEXT_RETURN);
	private StringBuilder MENU_TEXT_SORT_END = new StringBuilder(OUTPUT_SIGN_LINE)
										.append(OUTPUT_SIGN_SELECT);

	
			
	//1.请求分类  并没有具体的使用对象 所以dto为Serizalizable
	//2.打印分类
	//3.用户选择分类 并将用户参数传入下一个请求
	@Override
	public Service<? extends Serializable> execute() {
		
		//客户端封装数据传输对象
		SysDTO<Serializable> dto=new SysDTO<>();
		dto.setType(SysConstants.小说分类);
		
		//开始传输
		Protocol<Serializable, Classifcation[]> transform=new ProtocolImpl<>();
		//服务端返回响应结果  期待是小说分类
		SysDTO<Classifcation[]> reponse=null;
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
			return ServiceFactory.getService(SysConstants.开始);
		}finally {
			//一次通信结束的善后工作
			try {
				transform.destory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Classifcation[] classes=reponse.getData();
		System.out.println(MENU_TEXT_SORT_BEGIN);
		
		int i=0;
		for (Classifcation classifcation : classes) {
			System.out.println(++i+"."+classifcation.getClassName());
		}
		System.out.println(MENU_TEXT_SORT_END);
		
		//由于选择只能应该根据classes 的长度确定选择的范围
		int choice=-1;
		while(true) {	
			try {
				choice=Integer.valueOf(input.next().trim());
			} catch (NumberFormatException e) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}
			
			if(choice<0||choice>classes.length) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}else if (choice ==0) {//退出登录
				return ServiceFactory.getService(SysConstants.开始);
			}else {
				break;
			}
			
		}
		//正确的输入 -> 获取所有小说集合
		Service<Classifcation> nextFuction=ServiceFactory.getService(SysConstants.小说集合);
		nextFuction.setInutData(classes[choice-1]);
		return nextFuction;
	
	}

}
