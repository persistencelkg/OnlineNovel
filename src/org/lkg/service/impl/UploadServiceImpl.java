package org.lkg.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.lkg.entity.Classifcation;
import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.protocal.ProtocolImpl;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;
import org.lkg.util.GetProperies;
import org.lkg.util.ResultStatus;
import org.lkg.util.SysConstants;
import org.lkg.util.UploadFileTask;

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
	private String OUTPUT_INPUT_NAME = "请输入小说名：";
	private String OUTPUT_INPUT_AUTHOR = "请输入作者：";
	private String OUTPUT_INPUT_DESC = "请输入简介：";
	private String OUTPUT_INPUT_PATH = "请输入上传的txt(请注意路径用/或者\\\\)：";
	private String OUTPUT_TEXT_REUPLOAD = "继续上传请输入1，返回请输入0：";
	private String OUTPUT_TEXT_FILEEXSITS = "文件已存在，上传终止！ ";
	private String OUTPUT_SIGN_ASTERISK = "**********************************************";
	
	/**
	 * 上传步骤:
	 * 1.构造上传的小说对象 和dto
	 * 2.和服务器沟通是否可以上传(将客户端传来小说对象和服务端的小说集合进行比较 避免覆盖问题)
	 * 3.开始上传-下载的逆过程  同理 关闭资源应该等到上传之后
	 */
	@Override
	public Service<? extends Serializable> execute() {
		System.out.println(getInputData().getClassName()+"类即将进行一次上传");
		
		SysDTO<Novel> dto=new SysDTO<>();
		dto.setType(SysConstants.上传);
		Novel novel = setUploadNovelInfo();
		dto.setData(novel);
		
		//上传路径
		System.out.println(OUTPUT_INPUT_PATH);
		final String path=input.next().trim();
		if(!new File(path).exists()) {
			System.out.println(OUTPUT_SIGN_ASTERISK);
			System.err.println("文件不存在!请确认后再进行上传");
			System.out.println(OUTPUT_SIGN_ASTERISK);
			Service<Classifcation> getNovelist=ServiceFactory.getService(SysConstants.小说集合);
			getNovelist.setInutData(getInputData());
			return getNovelist;
		}
		
		
		ProtocolImpl<Novel, ?> transform=new ProtocolImpl<>();
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
			return ServiceFactory.getService(SysConstants.开始);
		}
		
		
		ResultStatus status=reponse.getResust();
		if(status==ResultStatus.UPLOAD_SUCCESS) {
			//开始上传  - 新开一个线程
			new Thread(new UploadFileTask(transform,path)).start();
			
		}else {
			System.out.println(OUTPUT_SIGN_ASTERISK);
			System.err.println(OUTPUT_TEXT_FILEEXSITS);
			System.out.println(OUTPUT_SIGN_ASTERISK);
			try {
				transform.destory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//继续
		System.out.println(OUTPUT_SIGN_ASTERISK);
		System.out.println(OUTPUT_TEXT_REUPLOAD);
		while(true) {
			String choice=input.next().trim();
			switch (choice) {
			case "0":
				Service<Classifcation> getNoveList=ServiceFactory.getService(SysConstants.小说集合);
				getNoveList.setInutData(getInputData());
				return getNoveList;
			case "1":
				Service<Classifcation> upload=ServiceFactory.getService(SysConstants.上传);
				upload.setInutData(getInputData());
				return upload;
			default:
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				break;
			}
		}
	}

	private Novel setUploadNovelInfo() {
		Novel novel=new Novel();
		novel.setClassifcation(getInputData());
		//接收小说的信息
		System.out.println(OUTPUT_INPUT_NAME);
		novel.setName(input.next().trim());
		System.out.println(OUTPUT_INPUT_AUTHOR);
		novel.setAuthor(input.next().trim());
		System.out.println(OUTPUT_INPUT_DESC);
		novel.setDesc(input.next().trim());
		
		
		return novel;
	}

}
