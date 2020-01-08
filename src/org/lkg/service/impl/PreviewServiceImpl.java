package org.lkg.service.impl;

import java.io.IOException;
import java.io.Serializable;

import org.lkg.entity.Classifcation;
import org.lkg.entity.Novel;
import org.lkg.entity.SysDTO;
import org.lkg.protocal.Protocol;
import org.lkg.protocal.ProtocolImpl;
import org.lkg.service.BaseServiceImpl;
import org.lkg.service.Service;
import org.lkg.util.GetProperies;
import org.lkg.util.ResultStatus;
import org.lkg.util.SysConstants;
/**
 * 对选择小说进行预览的请求
 * @description:需要携带小说参数
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月7日 上午10:47:28
 * @CopyRight lkg.nb.com
 */
public class PreviewServiceImpl extends BaseServiceImpl<Novel>{

	private String OUTPUT_SIGN_ASTERISK = "**********************************************\n";
	private String OUTPUT_SIGN_BEGINLINE = "当前阅读：《%1$s》";
	private String OUTPUT_SIGN_ENDLINE = "\n......，省略内容请下载后阅读\n";
	private String OUTPUT_TEXT_SELECTLIST = "继续显示列表请输入1，下载TXT请输入2：";
	private String OUTPUT_TEXT_FILENOTFOUND = "文件未找到，可能已被删除！";
	private String OUTPUT_TEXT_FILECANNOTREAD = "文件读取错误，预览失败！";
	private StringBuilder MENU_TEXT_SORT_BEGIN = new StringBuilder(
			OUTPUT_SIGN_ASTERISK).append(OUTPUT_SIGN_BEGINLINE);
	private StringBuilder MENU_TEXT_SORT_END = new StringBuilder(
			OUTPUT_SIGN_ENDLINE).append(OUTPUT_SIGN_ASTERISK).append(
			OUTPUT_TEXT_SELECTLIST);
	
	@Override
	public Service<? extends Serializable> execute() {
		SysDTO<Novel> dto=new SysDTO<>();   //携带novel向服务端发出预览请求
		dto.setData(getInputData());
		dto.setType(SysConstants.预览);
		
		Protocol<Novel,String> transform=new ProtocolImpl<>();
		//服务端返回响应结果
				SysDTO<String> reponse=null;
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
				
				System.out.println(OUTPUT_SIGN_ASTERISK);
				ResultStatus result=reponse.getResust();
				if(result==ResultStatus.FILE_NOT_FOUND) {
					System.out.println(OUTPUT_TEXT_FILENOTFOUND);
					System.out.println(OUTPUT_SIGN_ASTERISK);
				}else if(result==ResultStatus.FILE_NOT_READ) {
					System.out.println(OUTPUT_TEXT_FILECANNOTREAD);
					System.out.println(OUTPUT_SIGN_ASTERISK);
				}else {//正常预览
					System.out.println(String.format(MENU_TEXT_SORT_BEGIN.toString(),getInputData().getName()));
					System.out.println("\t\t\t\t"+reponse.getData());
					System.out.println(MENU_TEXT_SORT_END.toString());
					//功能选择
					String choice=null;
					while(true) {
						
						choice=input.next().trim();
						switch(choice) {
							case "1": //返回-获取小说列表
								Service<Classifcation> getNovelList=ServiceFactory.getService(SysConstants.小说集合);
								getNovelList.setInutData(getInputData().getClassifcation());
								return getNovelList;
							case "2":
								Service<Novel> download=ServiceFactory.getService(SysConstants.下载);
								download.setInutData(getInputData());
								return download;
							default:
								System.out.println(OUTPUT_TEXT_INVALIDINPUT);
								break;
						}
						
					}
					
				}
				//非正常情况跳回开始
				//return ServiceFactory.getService(SysConstants.开始);
				return null;
	}

}
