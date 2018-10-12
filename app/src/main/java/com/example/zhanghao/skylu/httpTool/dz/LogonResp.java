package com.example.zhanghao.skylu.httpTool.dz;

public class LogonResp {

	/**
	 * 调用状态
	 */
	private boolean state = false;
	private String uid;
	private String token;

	/**
	 * 接口响应结果
	 */
	private String result;
	
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
	
}
