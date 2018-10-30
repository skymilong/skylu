package com.example.zhanghao.skylu.httpTool.dz;

import android.util.Log;

import com.example.zhanghao.skylu.httpTool.APICommonJM;

/**
 * API接口
 *
 * @author Administrator
 */
public class API implements APICommonJM {

	private HttpTool send = new HttpTool();
	private static String logName = "dz";

	private static final API instance = new API();
	private String url = "http://api.jmyzm.com/http.do";

	public static API
	getInstance() {
		return instance;
	}

	private API() {

	}

	/**
	 * 登录
	 *
	 * @param uid
	 * @param pwd
	 * @return
	 */
	public LogonResp loginIn(String uid, String pwd) {
		LogonResp resp = new LogonResp();
		String result = "";
		try {
			result = send.httGet(url, "action=loginIn&uid="
					+ uid + "&pwd=" + pwd, "UTF-8");
			resp.setResult(result);
			String reset[] = result.split("\\|");

			if (reset.length >= 2 && uid.equals(reset[0])) {

				resp.setState(true);
				resp.setUid(reset[0]);
				resp.setToken(reset[1]);
			} else {
				resp.setState(false);
				resp.setResult(result);
			}

			Log.i(logName, "登录，账号：" + uid + ",返回:" + result);
		} catch (Exception e) {
			Log.e(logName, "登录异常，账号：" + uid +  ",e:"
					+ PrintLog.printStack(e));
			resp.setState(false);
		}
		return resp;
	}

	/**
	 * 获取一个手机号
	 *
	 * @pid
	 * @param uid
	 * @param token
	 * @return
	 */
	public GetMobilenumResp getMobilenum(int pid, String uid, String token) {
		return getMobilenum(pid, uid, token,"","","","","");
	}

	/**
	 *  获取多个手机号
	 * @param pid
	 * @param uid
	 * @param token
	 * @param size
	 * @return
	 */
	public GetMultiMobilenumResp getMobilenum(int pid, String uid,
											  String token, int size) {

		return getMobilenum(pid, uid, token, size, "", "", "", "", "");
	}


	/**
	 * 获取一个手机号
	 * @param pid
	 * @param uid
	 * @param token
	 * @param province 筛选省份；可以不填
	 * @param operator 运营商；可以不填，取值:CMCC(移动)|UNICOM(联通)|TELECOM(电信)
	 * @param notProvince 排除省份；可以不填
	 * @param vno  指定或排除虚拟运营商； 可以不填 ， vno=0 表示排除过滤虚拟运营商 vno=1 表示指定只取虚拟运营商。
	 * @return
	 */
	public GetMobilenumResp getMobilenum(int pid, String uid, String token,String province,String operator,String notProvince,String vno) {
		return getMobilenum(pid, uid, token,province,operator,notProvince,vno,"");
	}

	/**
	 * 获取一个手机号
	 * @param pid
	 * @param uid
	 * @param token
	 * @param province 筛选省份；可以不填
	 * @param operator  运营商；可以不填，取值:CMCC(移动)|UNICOM(联通)|TELECOM(电信)
	 * @param notProvince  排除省份；可以不填
	 * @param vno  指定或排除虚拟运营商； 可以不填 ， vno=0 表示排除过滤虚拟运营商 vno=1 表示指定只取虚拟运营商。
	 * @param city 筛选市；可以不填
	 * @return
	 */
	public GetMobilenumResp getMobilenum(int pid, String uid, String token,String province,String operator,String notProvince,String vno,String city) {
		GetMobilenumResp resp = new GetMobilenumResp();
		String result = "";
		try {
			result = send.httGet(url, "action=getMobilenum&uid="
					+ uid + "&token=" + token + "&pid=" + pid + "&province="+ java.net.URLEncoder.encode(province,"UTF-8")+"&operator="+operator+"&notProvince="+java.net.URLEncoder.encode(notProvince,"UTF-8")+"&vno="+vno+"&city="+java.net.URLEncoder.encode(city,"UTF-8") , "UTF-8");
			Log.i(logName, "获取一个手机号，账号：" + uid  + ",pid:"
					+ pid + ",返回:" + result);

			String reset[] = result.split("\\|");
			if (reset.length >= 2 && reset[0].matches("\\d+")) {
				resp.setState(true);
				resp.setMobile(reset[0]);
				resp.setResult(result);

			} else {
				resp.setState(false);
				resp.setResult(result);
			}

		} catch (Exception e) {
			Log.e(logName, "获取一个手机号，账号：" + uid
					+ ",pid:" + pid + ",e=" + PrintLog.printStack(e));
			resp.setState(false);
		}
		return resp;
	}

	/**
	 * 获取多个手机号
	 * @param pid
	 * @param uid
	 * @param token
	 * @param size
	 * @param province 筛选省份；可以不填
	 * @param operator 运营商；可以不填，取值:CMCC(移动)|UNICOM(联通)|TELECOM(电信)
	 * @param notProvince  排除省份；可以不填
	 * @param vno 指定或排除虚拟运营商； 可以不填 ， vno=0 表示排除过滤虚拟运营商 vno=1 表示指定只取虚拟运营商。
	 * @return
	 */
	public GetMultiMobilenumResp getMobilenum(int pid, String uid,
											  String token, int size, String province, String operator,
											  String notProvince, String vno) {
		return getMobilenum(pid, uid, token, size, province, operator,
				notProvince, vno, "");
	}

	/**
	 * 获取多个手机号
	 * @param pid
	 * @param uid
	 * @param token
	 * @param size
	 * @param province 筛选省份；可以不填
	 * @param operator 运营商；可以不填，取值:CMCC(移动)|UNICOM(联通)|TELECOM(电信)
	 * @param notProvince 排除省份；可以不填
	 * @param vno 指定或排除虚拟运营商； 可以不填 ， vno=0 表示排除过滤虚拟运营商 vno=1 表示指定只取虚拟运营商。
	 * @return
	 */
	public GetMultiMobilenumResp getMobilenum(int pid, String uid,
											  String token, int size,String province,String operator,String notProvince,String vno,String city) {
		GetMultiMobilenumResp resp = new GetMultiMobilenumResp();
		String result = "";
		try {
			result = send.httGet(url,
					"action=getMobilenum&uid=" + uid + "&token=" + token
							+ "&pid=" + pid + "&size=" + size +"&province="+ java.net.URLEncoder.encode(province,"UTF-8")+"&operator="+operator+"&notProvince="+java.net.URLEncoder.encode(notProvince,"UTF-8")+"&vno="+vno+"&city="+java.net.URLEncoder.encode(city,"UTF-8"), "UTF-8");
			Log.i(logName, "获取一个手机号，账号：" + uid  + ",pid:"
					+ pid + ",返回:" + result);

			String reset[] = result.split("\\|");
			if (reset.length >= 2) {
				resp.setState(true);
				String[] mobile = reset[0].split(";");
				resp.setMobile(mobile);
				resp.setResult(result);

			} else {
				resp.setState(false);
				resp.setResult(result);
			}

		} catch (Exception e) {
			Log.e(logName, "获取一个手机号，账号：" + uid
					+ ",pid:" + pid + ",e=" + PrintLog.printStack(e));
			resp.setState(false);
		}
		return resp;
	}


	/**
	 * 获取验证码并继续使用这个手机号
	 *
	 * @param mobile
	 * @param uid
	 * @param token
	 * @param next_pid
	 * @param author_uid
	 * @return
	 */
	public GetVcodeAndHoldMobilenumResp getVcodeAndHoldMobilenum(String mobile,
																 String uid, String token, String next_pid, String author_uid) {
		GetVcodeAndHoldMobilenumResp resp = new GetVcodeAndHoldMobilenumResp();
		String result = "";
		try {
			result = send.httGet(url,
					"action=getVcodeAndHoldMobilenum&uid=" + uid + "&token="
							+ token + "&mobile=" + mobile + "&next_pid="
							+ next_pid + "&author_uid=" + author_uid, "UTF-8");
			Log.i(logName, "获取验证码并继续使用这个手机号，mobile:" + mobile + ",uid："
					+ uid  + ",next_pid:" + next_pid
					+ ",author_uid:" + author_uid + ",返回:" + result);

			// 返回值：发送号码|验证码|下次获取验证码的token(暂时无用)
			String reset[] = result.split("\\|");
			if (reset.length >= 2 && reset[0].matches("\\d+")) {
				resp.setState(true);
				resp.setMobile(reset[0]);
				resp.setVerifyCode(reset[1]);
				resp.setResult(result);

			} else {
				resp.setState(false);
				resp.setResult(result);
			}

		} catch (Exception e) {
			Log.e(logName, "获取验证码并继续使用这个手机号，mobile:" + mobile + ",uid："
					+ uid  + ",next_pid:" + next_pid
					+ ",author_uid:" + author_uid + ",e:"
					+ PrintLog.printStack(e));
			resp.setState(false);
		}
		return resp;
	}

	/**
	 * 获取验证码并不再使用这个手机号
	 *
	 * @param mobile
	 * @param uid
	 * @param token
	 * @param author_uid
	 * @return
	 */
	public GetVcodeAndReleaseMobileResp getVcodeAndReleaseMobile(String mobile,
																 String uid, String token, String author_uid) {
		GetVcodeAndReleaseMobileResp resp = new GetVcodeAndReleaseMobileResp();
		String result = "";
		try {
			result = send.httGet(url,
					"action=getVcodeAndReleaseMobile&uid=" + uid + "&token="
							+ token + "&mobile=" + mobile + "&author_uid="
							+ author_uid, "UTF-8");

			String reset[] = result.split("\\|");
			if (reset.length >= 2 && reset[0].matches("\\d+")) {
				resp.setState(true);
				resp.setMobile(reset[0]);
				resp.setVerifyCode(reset[1]);
				resp.setResult(result);

			} else {
				resp.setState(false);
				resp.setResult(result);
			}

			Log.i(logName, "获取验证码并不再使用这个手机号，mobile:" + mobile + ",uid："
					+ uid  + ",author_uid:" + author_uid
					+ ",返回:" + result + ",state:" + resp.isState() + ",mobile:"
					+ resp.getMobile());

		} catch (Exception e) {
			Log.e(logName, "获取验证码并不再使用这个手机号，mobile:" + mobile + ",uid："
					+ uid  + ",author_uid:" + author_uid
					+ ",e:" + PrintLog.printStack(e));
			resp.setState(false);
		}
		return resp;
	}

	/**
	 * 添加若干手机号到黑名单,可用于网站对此手机号的使用次数进行了限制
	 *
	 * @param pid
	 * @param mobiles
	 * @param uid
	 * @param token
	 * @return
	 */
	public AddIgnoreListResp addIgnoreList(int pid, String mobiles, String uid,
										   String token) {
		AddIgnoreListResp resp = new AddIgnoreListResp();
		String result = "";

		try {
			result = send.httGet(url,
					"action=addIgnoreList&uid=" + uid + "&token=" + token
							+ "&mobiles=" + mobiles + "&pid=" + pid, "UTF-8");
			Log.i(logName, "添加若干手机号到黑名单，mobiles:" + mobiles + ",uid：" + uid
					+ ",pid:" + pid + ",返回:" + result);

			if (result.matches("\\d+")) {
				resp.setState(true);
				resp.setRow(Integer.parseInt(result));
				resp.setResult(result);

			} else {
				resp.setState(false);
				resp.setResult(result);
			}

		} catch (Exception e) {
			Log.e(logName, "添加若干手机号到黑名单，mobiles:" + mobiles + ",uid：" + uid
					+ ",pid:" + pid + ",e:"
					+ PrintLog.printStack(e));
			resp.setState(false);
		}
		return resp;
	}

	/**
	 * 获取当前用户正在使用的号码列表
	 * @param pid
	 * @param uid
	 * @param token
	 * @return 返回值：列表json数据(Pid=项目ID,Recnum=获取的号码,Timeout=超时时间秒,Start_time=获取时间)
	 */
	public String getRecvingInfo(String pid, String uid, String token) {
		// cancelSMSRecvResp resp = new cancelSMSRecvResp();
		String result = "";
		try {
			result = send.httGet(url,
					"action=getRecvingInfo&uid=" + uid + "&token=" + token
							+ "&pid=" + pid, "UTF-8");
			Log.i(logName + "GetRecvingInfo", "获取当前用户正在使用的号码列表，pid:" + pid
					+ ",uid：" + uid  + ",返回:" + result);

		} catch (Exception e) {
			Log.e(logName + "GetRecvingInfo", "获取当前用户正在使用的号码列表，pid:" + pid
					+ ",uid：" + uid  + ",e:"
					+ PrintLog.printStack(e));
			// resp.setState(false);
		}
		return result;
	}


	/**
	 * 用户获取个人用户信息
	 * @param uid
	 * @param token
	 * @return 用户信息
	 */
	public GetUserInfos getUserInfos(String uid, String token) {

		GetUserInfos resp = new GetUserInfos();
		String result = "";
		try {
			result = send.httGet(url, "action=getUserInfos&uid="
					+ uid + "&token=" + token, "UTF-8");
			Log.i(logName, "获取用户获取个人用户信息，账号：" + uid
					+ ",返回:" + result);

			String[] reset = result.split(";");
			if (reset.length >= 4 && reset[1].matches("\\d+")
					&& reset[2].matches("\\d+") && reset[3].matches("\\d+")) {
				resp.setState(true);
				resp.setUid(reset[0]);
				resp.setScore(Integer.parseInt(reset[1]));
				resp.setBalance(Integer.parseInt(reset[2]));
				resp.setPhoneMaxSize(Integer.parseInt(reset[3]));
				resp.setResult(result);

			} else {
				resp.setState(false);
				resp.setResult(result);
			}

		} catch (Exception e) {
			Log.e(logName, "获取用户获取个人用户信息，账号：" + uid
					+ ",e=" + PrintLog.printStack(e));
			resp.setState(false);
		}
		return resp;

	}

}
