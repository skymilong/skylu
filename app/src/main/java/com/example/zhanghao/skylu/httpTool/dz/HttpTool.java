package com.example.zhanghao.skylu.httpTool.dz;

import java.net.URL;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public class HttpTool {
	
	public final static String HTTP_EXCEPTION="HTTP_EXCEPTION";
	public HttpTool() {
	}

	public String httGet(String httpUrl, String pageList, String encode) {

		BufferedReader bin = null;
		StringBuffer buffer = new StringBuffer();
		try {

			URL url = new URL(httpUrl
					+ ("".equals(pageList) ? "" : "?" + pageList));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			

			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.connect();

			InputStream in = conn.getInputStream();			

			if ("gzip".equalsIgnoreCase(conn.getContentEncoding())) {
				GZIPInputStream gzin = new GZIPInputStream(in);
				bin = new BufferedReader(new InputStreamReader(gzin, encode));
			} else {

				bin = new BufferedReader(new InputStreamReader(in, encode));
			}

			String s;
			while ((s = bin.readLine()) != null) {
				buffer.append(s);
			}

		} catch (Exception ex) {			
			return HTTP_EXCEPTION;
		} finally {
			if (bin != null) {
				try {
					bin.close();
				} catch (Exception e) {

				}
			}
		}

		return buffer.toString();
	}

}
