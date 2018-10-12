package com.example.zhanghao.skylu.httpTool.dz;

import android.os.Environment;

import java.io.File;

import java.io.FileWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PrintLog {

	private static SimpleDateFormat curformatter = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

	/**
	 * 日志路径
	 */
	public static String logpath = "jiemalog";

	/**
	 * 将信息输入到txt中
	 * @param path
	 * @param content
	 * @param type
	 * @param debug
	 * @throws IOException
	 */
	public void writefile(String path, String content, String type,boolean debug)
			throws IOException {

		File F = new File(path);

		// 如果文件不存在,就动态创建文件
		File file = new File(Environment.getExternalStorageDirectory()+"/msc/" + logpath);
		if (!file.exists()) // 目录不存在，则创建相应的目录
			file.mkdirs();

		if (!F.exists()) {

			F.createNewFile();

		}

		FileWriter fw = null;

		String writeDate = "时间:" + this.get_nowDate() + "---" + type + content;
		if(debug)
			System.out.println(writeDate);

		try {

			// 设置为:True,表示写入的时候追加数据

			fw = new FileWriter(F, true);

			// 回车并换行

			fw.write(writeDate + "\r\n");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (fw != null) {

				fw.close();

			}

		}

	}

	/**
	 * error
	 * @param logName
	 * @param content
	 */
	public void error(String logName,String content) {

		try {
			writefile(getLogPath(logName), content, "error:",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * info
	 * @param logName
	 * @param content
	 */
	public void info(String logName,String content) {

		try {
			writefile(getLogPath(logName), content, "-info:",false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @获取系统当前时间
	 *
	 * @return
	 */

	public String get_nowDate() {

		return formatter.format(Calendar.getInstance().getTime());

	}

	public static String printStack(Exception e) {
		StringBuffer str = new StringBuffer();
		try {
			str.append(e).append("\r\n");
			StackTraceElement[] trace = e.getStackTrace();
			for (int i = 0; i < trace.length; i++)
				if (i == 0) {
					str.append("\tat " + trace[i]);
				} else {
					str.append("\r\n").append("\tat " + trace[i]);
				}

		} catch (Exception ex) {

		}
		return str.toString();
	}

	public String getLogPath(String logName) {
		String dir = logName;
		return (new StringBuilder(String.valueOf(logpath))).append("/").append(
				dir).append("/").append(curformatter.format(Calendar.getInstance().getTime())).append('_').append(logName).append(
				".log").toString();
	}

}
