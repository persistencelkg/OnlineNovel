package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;

import org.lkg.entity.Classifcation;
import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.entity.Users;
import org.lkg.protocal.Protocol;
import org.lkg.protocal.ProtocolImpl;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;
import org.lkg.util.GetProperies;
import org.lkg.util.ResultStatus;
import org.lkg.util.SysConstants;

/**
 * 获取所有小说信息的请求
 * @description: 由于请求目标需要提供分类信息  所以需要 泛型实例为Classifcation
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月6日 下午9:30:12
 * @CopyRight lkg.nb.com
 */
public class GetNovelsInfoImpl extends BaseServiceImpl<Classifcation>{
	
	private String OUTPUT_TEXT_READDOWN = "阅读和下载请选择文件序号，上传TXT请输入-1，返回请输入0：";
	private String OUTPUT_SIGN_BEGINLINE = "------------------%1$s列表结束------------------";
	private String OUTPUT_SIGN_HEAD = "序号\t\t名称\t\t作者\t\t简介";
	private String OUTPUT_SIGN_ENDLINE = "------------------小说列表结束------------------";
	private String OUTPUT_SIGN_LINE="-------------------------------------\n";
	private String STORY_TEXT_RETURN="0.返回上一级菜单\n";
	private String STORY_TEXT_READ="1.在线阅读\n";
	private String STORY_TEXT_DOWNLOAD="2.下载TXT\n";
	private String OUTPUT_SIGN_SELECT="请选择： ";
	private StringBuffer MENU_FUNCTION = new StringBuffer(OUTPUT_SIGN_LINE)
											.append(STORY_TEXT_RETURN)
											.append(STORY_TEXT_READ)
											.append(STORY_TEXT_DOWNLOAD)
											.append(OUTPUT_SIGN_LINE)
											.append(OUTPUT_SIGN_SELECT);


	@Override
	public Service<? extends Serializable> execute() {
		System.out.println("即将获得"+getInputData().getClassName()+"的分类信息");
		
		
		SysDTO<Classifcation> dto=new SysDTO<>();
		dto.setData(getInputData());
		dto.setType(SysConstants.小说集合);
		
		
		//开始传输
		Protocol<Classifcation,Novel[]> transform=new ProtocolImpl<>();
		//服务端返回响应结果
		SysDTO<Novel[]> reponse=null;
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
		
		Novel[] novels=reponse.getData();
		
		/**
		 * 由于标题是可变的所以  使用占位符
		 * %1$s :代表format的参数列表的第1个参数(getName())  可以复用 
		 */
		System.out.println(String.format(OUTPUT_SIGN_BEGINLINE,getInputData().getClassName()));
		System.out.println(OUTPUT_SIGN_HEAD);
		
		//遍历小说集合
		int i=0;
		StringBuffer buffer=null;
		for (Novel novel : novels) {
			buffer=new StringBuffer(++i+"").append("\t\t").append(novel.getName()).
			append("\t\t").append(novel.getAuthor()).append("\t\t").
			append(novel.getDesc());
			
			System.out.println(buffer);
		}
		System.out.println(OUTPUT_SIGN_ENDLINE);
		
		int choice = selectNovels(novels.length);
		
		//选择上传
		if(choice==-1) {
			//指定上传的分类
			Service<Classifcation> upload=ServiceFactory.getService(SysConstants.上传);
			upload.setInutData(getInputData());
			return upload;
		}else if(choice==0) {
			return ServiceFactory.getService(SysConstants.小说分类);
		}else {
			//进入阅读菜单
			int choice2=readingMenu();
			//获得用户刚选择的小说
			Novel novel=novels[choice2-1];
			
			switch(choice2) {
			 case 0: //返回上一级 : 重新获得小说集合
				 Service<Classifcation> getNovels=ServiceFactory.getService(SysConstants.小说集合);
				 //本类已经存在类别信息 之间送入getNovels即可访问
				 getNovels.setInutData(this.getInputData());
				 return getNovels;
			 case 1://对选择的小说进行预览
				 Service<Novel> preview=ServiceFactory.getService(SysConstants.预览);
				 preview.setInutData(novel);
				 return preview;
			 case 2://对选择的小说进行下载
				 Service<Novel> download=ServiceFactory.getService(SysConstants.下载);
				 download.setInutData(novel);
				 return download;
					
			}
			
		}
		
		
		return null;
	}


	
	/**
	 * 选择小说功能菜单
	 * @param length 小说的数量,决定了选择序号的范围
	 * @return 返回选择的小说序号
	 */
	private int selectNovels(int length) {
		System.out.println(OUTPUT_TEXT_READDOWN);
		int choice=-1;
		while(true) {
			try {
				choice=Integer.valueOf(input.next().trim());
				if(choice<-1||choice>length) {
					System.out.println(OUTPUT_TEXT_INVALIDINPUT);
					continue;
				}else
					break;
				
			} catch (NumberFormatException e) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}
		}
		return choice;
	}
	
	
	/**
	 * 选择小说后进入阅读菜单 进行二次选择
	 * @return 返回选择的结果
	 */
	private int readingMenu() {
		int choice;
		System.out.println(MENU_FUNCTION);
		while(true) {
			try {
				choice=Integer.valueOf(input.next().trim());
				if(choice<0||choice>2) {//只有三个选项 
					System.out.println(OUTPUT_TEXT_INVALIDINPUT);
					continue;
				}else 
					break;
				
			} catch (NumberFormatException e) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}
		}
		return choice;
	}



}
