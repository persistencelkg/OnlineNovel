package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;

import javax.xml.ws.soap.AddressingFeature.Responses;

import org.lkg.entity.Classifcation;
import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.entity.Users;
import org.lkg.protocal.Protocol;
import org.lkg.protocal.ProtocolImpl;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;
import org.lkg.util.DownloadFileTask;
import org.lkg.util.GetProperies;
import org.lkg.util.ResultStatus;
import org.lkg.util.SysConstants;

/**
 * 
 * 对选择的小说的下载请求
 * @description: 需要携带小说信息
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月7日 上午10:46:52
 * @CopyRight lkg.nb.com
 */
public class DownloadServiceImpl extends BaseServiceImpl<Novel>{
	
	private String OUTPUT_SIGN_ASTERISK = "**********************************************\n";
	private String OUTPUT_TEXT_FILENOTFOUND = "文件未找到，可能已被删除！";
	private String OUTPUT_TEXT_FILECANNOTREAD = "文件无法读取，下载失败！";
	private String OUTPUT_TEXT_DOWNLOADSTART = "文件开始下载！";
	private String OUTPUT_TEXT_DOWNLOADFAIL = "文件下载失败！";
	private String OUTPUT_TEXT_DOWNLOADSUCCESS = "文件下载成功！\n下载后的文件路径是：%1$s\n下载后的文件名是：%2$s";
	private String OUTPUT_TEXT_SELECTLIST = "按回车键返回：";

	/**
	 * 下载业务逻辑:
	 * 1.首先向服务端发送数据传输对象    
	 * 2.获取服务端是否同意下载的信号
	 * 3.进行真正的下载 基于此 关闭流应该注意  关闭应该在下载完毕之后进行
	 */
	@Override
	public Service<? extends Serializable> execute() {
		System.out.println(this.getInputData().getName()+"即将开始下载...");
		
		SysDTO<Novel> dto=new SysDTO<>();
		dto.setData(getInputData());
		dto.setType(SysConstants.下载);
	
		//开始传输
		ProtocolImpl<Novel, ? extends Serializable> transform=new ProtocolImpl<>();
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
		
		/**
		 * 以下代码放在try-catch-finally中是不适合的
		 * 1.下载是一个独立的线程,否则cpu就会被长时间占据 用户无法继续其他操作 体验极差
		 * 2.由于下载是一个线程 就会产生同步问题  那么就有可能根据isContinue=true 直接往下执行
		 * 导致优先执行finally的情况  这就会导致下载的资源提前关闭  
		 * 所以 finally此时不应该存在  而且也不适合将下面代码放try块中
		 * 
		 */
		boolean isContinue=true;
		System.out.println(OUTPUT_SIGN_ASTERISK);
		ResultStatus status=reponse.getResust();
		if(status==ResultStatus.FILE_NOT_FOUND) {
			System.out.println(OUTPUT_TEXT_FILENOTFOUND);
			isContinue=false;
		}else if(status==ResultStatus.FILE_NOT_READ) {
			System.out.println(OUTPUT_TEXT_FILECANNOTREAD);
			isContinue=false;
		}else {
			System.out.println(OUTPUT_TEXT_DOWNLOADSTART);
			isContinue=true;
		}
		//开始真正的下载
		if(isContinue) {
			new Thread(
					new DownloadFileTask(transform,getInputData().getName())
					).start();
			
		}else {
			//关闭资源
			try {
				transform.destory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(OUTPUT_SIGN_ASTERISK);
		System.out.println(OUTPUT_TEXT_SELECTLIST);
		String key=input.nextLine();//特点:遇到回车结束输入 
		
		
		Service<Classifcation> getNovels=ServiceFactory.getService(SysConstants.小说集合);
		getNovels.setInutData(getInputData().getClassifcation());
		return getNovels;
	}

}
