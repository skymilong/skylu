package com.example.zhanghao.skylu.httpTool.RuoKuai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.zhanghao.skylu.httpTool.APICommonDM;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;




public class RuoKuai implements APICommonDM {
    private static final RuoKuai instance = new RuoKuai();

    public static RuoKuai getInstance(){
        return instance;
    }



	public static   String httpRequestData(String url, String param) throws IOException{
		// TODO Auto-generated method stub
		URL u;
		HttpURLConnection con = null;
		OutputStreamWriter osw;
		StringBuffer buffer = new StringBuffer();

		u = new URL(url);
		if (u.getProtocol().toUpperCase().equals("HTTPS")) {
			trustAllHosts();

				HttpsURLConnection https = (HttpsURLConnection) u
						.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
			con = https;

		} else {
			con = (HttpURLConnection)u.openConnection();

		}
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		osw = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
		osw.write(param);
		osw.flush();
		osw.close();

		BufferedReader br = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}

		return buffer.toString();
	}

	public  String Post(){
		String ret =null;
		try {
			URL u = new URL("http://api.ruokuai.com/info.json");

			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.addRequestProperty("encoding", "UTF-8");
			con.setRequestMethod("POST");
			//写入
			OutputStream out = con.getOutputStream();
			OutputStreamWriter ow = new OutputStreamWriter(out);//将要写入流中的字符编码成字节
			BufferedWriter bw = new BufferedWriter(ow);//写入字符输出流
			bw.write("username=a123785&password=123456");
			bw.flush();//刷新该流的缓冲

			//读取
			InputStream in = con.getInputStream();
			InputStreamReader iread = new InputStreamReader(in);//读取字节并将其解码为字符
			BufferedReader read = new BufferedReader(iread);//从字符输入流中读取文本

			StringBuffer buf = new StringBuffer();
			String lin=null;
			//逐行读取
			while (((lin=read.readLine())!=null)) {
				buf.append(lin);
			}
			ret = buf.toString();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//DOM
		try {

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();//          获取 DocumentBuilderFactory 的新实例。
			DocumentBuilder db = dbf.newDocumentBuilder();// 使用当前配置的参数创建一个新的 DocumentBuilder 实例。
			Document doc = db.parse(new InputSource(new StringReader(ret)));
			Element root = doc.getDocumentElement();//允许直接访问文档的文档元素的子节点。
			NodeList h = root.getElementsByTagName("Score");//返回具有带给定值的 ID 属性的 Element。

			System.out.println(h.item(0).getTextContent());//节点文本内容

			//	System.out.println("aa"+his);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 字符串MD5加密
	 * @param s 原始字符串
	 * @return  加密后字符串
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			mdInst.update(btInput);
			byte[] md = mdInst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * 查询用户信息
	 * @param username
	 * @param password
	 * @return
	 */
	public JSONObject getInFo(String username, String password){
		String ret = "";
		String param = String.format("username=%s&password=%s",username,password);
		JSONObject jsonObject= null;
		try{
			ret = httpRequestData("http://api.ruokuai.com/info.json",param);
			jsonObject = JSON.parseObject(ret);
			return jsonObject;
        }catch(Exception e){
			jsonObject = new JSONObject();
			jsonObject.put("error","未知错误");
		}
		return jsonObject;
	}


	/**
	 * 答题
	 * @param url            请求URL，不带参数 如：http://api.ruokuai.com/create.json
	 * @param param            请求参数，如：username=test&password=1
	 * @param data            图片二进制流
	 * @return				平台返回结果XML样式
	 * @throws IOException
	 */
	public Map<String, String> httpPostImage(String url, String param,
											 byte[] data) throws IOException {
		long time = (new Date()).getTime();
		URL u = null;
		HttpURLConnection con = null;
		String boundary = "----------" + MD5(String.valueOf(time));
		String boundarybytesString = "\r\n--" + boundary + "\r\n";
		OutputStream out = null;

		u = new URL(url);
		if (u.getProtocol().toUpperCase().equals("HTTPS")) {
			trustAllHosts();

			HttpsURLConnection https = (HttpsURLConnection) u
					.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			con = https;

		} else {
			con = (HttpURLConnection)u.openConnection();

		}
		con.setRequestMethod("POST");
		//con.setReadTimeout(95000);
		con.setConnectTimeout(95000); //此值与timeout参数相关，如果timeout参数是90秒，这里就是95000，建议多5秒
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(true);
		con.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);

		out = con.getOutputStream();

		for (String paramValue : param.split("[&]")) {
			out.write(boundarybytesString.getBytes("UTF-8"));
			String paramString = "Content-Disposition: form-data; name=\""
					+ paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
			out.write(paramString.getBytes("UTF-8"));
		}
		out.write(boundarybytesString.getBytes("UTF-8"));

		String paramString = "Content-Disposition: form-data; name=\"image\"; filename=\""
				+ "sample.gif" + "\"\r\nContent-Type: image/gif\r\n\r\n";
		out.write(paramString.getBytes("UTF-8"));

		out.write(data);

		String tailer = "\r\n--" + boundary + "--\r\n";
		out.write(tailer.getBytes("UTF-8"));

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}
        String result = buffer.toString();
        return (Map<String,String>) JSON.parse(result);
	}

	public  RuoKuaiSuccess httpPostBase64Image(String url, String param,
										 String pic64) throws IOException {
		long time = (new Date()).getTime();
		URL u = null;
		HttpURLConnection con = null;
		String boundary = "----------" + MD5(String.valueOf(time));
		String boundarybytesString = "\r\n--" + boundary + "\r\n";
		OutputStream out = null;

		u = new URL(url);

		con = (HttpURLConnection) u.openConnection();
		con.setRequestMethod("POST");
		//con.setReadTimeout(95000);
		con.setConnectTimeout(95000); //此值与timeout参数相关，如果timeout参数是90秒，这里就是95000，建议多5秒
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setUseCaches(true);
		con.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + boundary);

		out = con.getOutputStream();

		for (String paramValue : param.split("[&]")) {
			out.write(boundarybytesString.getBytes("UTF-8"));
			String paramString = "Content-Disposition: form-data; name=\""
					+ paramValue.split("[=]")[0] + "\"\r\n\r\n" + paramValue.split("[=]")[1];
			out.write(paramString.getBytes("UTF-8"));
		}
		out.write(boundarybytesString.getBytes("UTF-8"));

		String paramString = "Content-Disposition: form-data; name=\"image\"; filename=\""
				+ "sample."+pic64.substring(pic64.indexOf("/")+1,pic64.indexOf(";")) + "\"\nContent-Transfer-Encoding: base64\r\nContent-Type: application/octet-stream\r\n\r\n";
		out.write(paramString.getBytes("UTF-8"));


		String tailer = "\r\n--" + boundary + "--\r\n";
		out.write(tailer.getBytes("UTF-8"));

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(con
				.getInputStream(), "UTF-8"));
		String temp;
		while ((temp = br.readLine()) != null) {
			buffer.append(temp);
			buffer.append("\n");
		}
		String result = buffer.toString();
		RuoKuaiSuccess ruoKuaiSuccess = JSON.parseObject(result, RuoKuaiSuccess.class);
		return ruoKuaiSuccess;
	}



	/**
	 * 上传题目图片返回结果
	 * @param username        用户名
	 * @param password        密码
	 * @param typeid        题目类型
	 * @param timeout        任务超时时间
	 * @param softid        软件ID
	 * @param softkey        软件KEY
	 * @param filePath        题目截图或原始图二进制数据路径
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> createByPost(String username, String password,
											String typeid, String timeout, String softid, String softkey,
											String filePath) {
		Map<String, String>  result = null;
		String param = String.format(
				"username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s",
				username, password, typeid, timeout, softid, softkey);
		try {
			File f = new File(filePath);
			if (null != f) {
				int size = (int) f.length();
				byte[] data = new byte[size];
				FileInputStream fis = new FileInputStream(f);
				fis.read(data, 0, size);
				if(null != fis) fis.close();

				if (data.length > 0) {
					result = httpPostImage("http://api.ruokuai.com/create.json", param, data);
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			result = new HashMap<>();
			result.put("error",e.getMessage());
		}

		return result;
	}




	/**
	 * URL验证
	 * @param softId
	 * @param softKey
	 * @param typeid
	 * @param username
	 * @param password
	 * @param url
	 * @return
	 */
	@Override
	public Map<String, String> creatByUrl(String softId, String softKey, String typeid, String username, String password, String url, String body, Map<String, String> headers) {
		// TODO Auto-generated method stub
		String param = String.format("username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s", username,password,typeid,"90",softId,softKey);
		Map<String, String>  ret=null;

		byte[] imageFromNetByUrl = getImageFromNetByUrl(url,headers,body);
		try {
			ret = httpPostImage("http://api.ruokuai.com/create.json", param, imageFromNetByUrl);
			return ret;
		} catch (IOException e) {
			ret = new HashMap<>();
			ret.put("error",e.getMessage());
            return ret;
		}

	}

	@Override
	public RuoKuaiSuccess httpPostImageBase64(String softId, String softKey, String typeid,String timeout, String username, String password, String base64) {
		String param = String.format(
				"username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s",
				username, password, typeid, timeout, softId, softKey);
		try {
			return httpPostBase64Image("http://api.ruokuai.com/create.json",param,base64);
		} catch (IOException e) {
			e.printStackTrace();
			return  new RuoKuaiSuccess("未知错误","-1","","","");
		}
	}

	public  byte[] getImageFromNetByUrl(String strUrl,Map<String,String> headers,String body) {
		try {
			URL url = new URL(strUrl+"?"+body);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5 * 1000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			Set<String> strings = headers.keySet();
			for (String key : strings){
				String val = headers.get(key);
				conn.setRequestProperty(key,val);
			}
			if (url.getProtocol().toUpperCase().equals("HTTPS")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url
						.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				conn = https;
			}
            InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			byte[] btImg = outStream.toByteArray();// 得到图片的二进制数据

			inStream.close();
			outStream.close();
			conn.disconnect();
			return btImg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 报错
	 * @param username
	 * @param password
	 * @param softId
	 * @param softKey
	 * @param error
	 * @return
	 */

	public  String error(String username, String password, String softId,
							   String softKey, String error) {
		// TODO Auto-generated method stub
		String ret="";
		String param = String.format("username=%s&password=%s&softid=%s&softkey=%s&id=%s",username,password,softId,softKey, error);
		try {
			ret=httpRequestData("http://api.ruokuai.com/reporterror.xml",param);
		} catch (Exception e) {
			// TODO: handle exception
			return "未知错误";
		}

		return ret;
	}

	/**
	 * 注册
	 * @param username
	 * @param password
	 * @param email
	 * @return
	 */

	public  String zhuce(String username, String password, String email) {
		// TODO Auto-generated method stub
		String ret="";
		String param = String.format("username=%s&password=%s&email=%s", username,password,email);
		try {
			ret=httpRequestData("http://api.ruokuai.com/register.xml", param);
		} catch (Exception e) {
			// TODO: handle exception
			return "未知错误";
		}
		return ret;
	}

	/**
	 * 充值
	 * @param username
	 * @param kid
	 * @param key
	 * @return
	 */

	public  String chongzhi(String username, String kid, String key) {
		// TODO Auto-generated method stub

		String ret="";
		String param = String.format("username=%s&password=%s&id=%s", username,key,kid);
		try {
			ret=httpRequestData("http://api.ruokuai.com/recharge.xml", param);
		} catch (Exception e) {
			// TODO: handle exception
			return "未知错误";
		}

		return ret;
	}

	public static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		// Android use X509 cert
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
										   String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
										   String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};
}
