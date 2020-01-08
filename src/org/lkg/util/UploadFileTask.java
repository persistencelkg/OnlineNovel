package org.lkg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import org.lkg.entity.Novel;
import org.lkg.protocal.ProtocolImpl;

/**
 * 客户端上传线程
 * @description: 
 * 1.先从本地读取文件
 * 2.通过套接字输出流 发送给服务端
 * @author: 浮~沉
 * @version: 1.0
 * @data 2020年1月8日 上午11:50:29
 * @CopyRight lkg.nb.com
 */
public class UploadFileTask implements Runnable{
	private String OUTPUT_SIGN_ASTERISK = "**********************************************";
	private String OUTPUT_TEXT_FILENOTFOUND = "文件未找到，请确认后重试！";
	private String OUTPUT_TEXT_UPLOADFAIL = "文件上传失败！";
	private static final int size=1024*4;
	private ProtocolImpl<Novel, ? extends Serializable> transfrom;
	private String fileName;  	  //来自用户上传操作时的输入
	
	public UploadFileTask(ProtocolImpl<Novel, ? extends Serializable> transfrom, String fileName) {
		this.transfrom=transfrom;
		this.fileName = fileName;
	}


	/**
	 * 数据传输 用字节流
	 */

	@Override
	public void run() {
		File file=new File(fileName);
		byte[] buffer=new byte[size];
		InputStream inputStream=null;
		try {
			inputStream=new FileInputStream(file);
			int len=-1;
			while((len=inputStream.read(buffer))!=-1) {
				transfrom.getOut().write(buffer,0,len);
			}
			transfrom.getOut().flush();
			//告诉服务端我已经上传完毕
			transfrom.getSocket().shutdownOutput();
		} catch (FileNotFoundException e) {
			System.out.println(OUTPUT_SIGN_ASTERISK);
			System.err.println(OUTPUT_TEXT_FILENOTFOUND);
			System.out.println(OUTPUT_SIGN_ASTERISK);
		} catch (IOException e) {
			System.out.println(OUTPUT_SIGN_ASTERISK);
			System.err.println(OUTPUT_TEXT_UPLOADFAIL);
			System.out.println(OUTPUT_SIGN_ASTERISK);
		}finally {
			try {
				if(transfrom.getOut()!=null) 	transfrom.getOut().close();
				if(inputStream!=null) inputStream.close();
				transfrom.destory();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}

}
