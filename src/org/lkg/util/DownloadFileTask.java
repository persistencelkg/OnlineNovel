package org.lkg.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class DownloadFileTask implements Runnable{
	private String OUTPUT_SIGN_ASTERISK = "**********************************************\n";
	private String OUTPUT_TEXT_DOWNLOADFAIL = "文件下载失败！";
	private String OUTPUT_TEXT_DOWNLOADSUCCESS = "文件下载成功！\n下载后的文件路径是：%1$s\n下载后的文件名是：%2$s";
	private String OUTPUT_TEXT_SELECTLIST = "按回车键返回：";
	private static final int size=1024*1;
	private String novelName;
	private ObjectInputStream in;  //客户端的对象读入流
	
	public DownloadFileTask(ObjectInputStream in,String novelName) {
		this.in=in;
		this.novelName=novelName;
	}
	
	@Override
	public void run() {
		String path=GetProperies.getValue("client.download.path");
		if(!path.endsWith(File.separator))
			path+=File.separator;
		String fileName=path+novelName+".txt";
		//本地下载文件
		File file=new File(fileName);
		
		//接受服务端的字节流  将其写入本地下载位置
		byte[] bytes=new byte[size];
		OutputStream outputStream=null;
		int len=-1;
		try {
			outputStream=new FileOutputStream(file);
			while((len=in.read(bytes))!=-1) {
				outputStream.write(bytes,0,len);
			}
			outputStream.flush();
		} catch (IOException e) {
			//下载失败的善后工作
			file.delete();
			System.err.print(OUTPUT_SIGN_ASTERISK);
			System.out.println(OUTPUT_TEXT_DOWNLOADFAIL);
			System.err.print(OUTPUT_SIGN_ASTERISK);
			e.printStackTrace();
		}finally {
			try {
				if(outputStream!=null) 
					outputStream.close();
				if(in!=null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print(OUTPUT_SIGN_ASTERISK);
			System.out.println(String.format(OUTPUT_TEXT_DOWNLOADSUCCESS,fileName,novelName));
			System.out.print(OUTPUT_SIGN_ASTERISK);
		}
		
		
	}

}
