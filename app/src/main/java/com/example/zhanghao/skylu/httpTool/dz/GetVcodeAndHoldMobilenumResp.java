package com.example.zhanghao.skylu.httpTool.dz;

public class GetVcodeAndHoldMobilenumResp {
	private boolean state = false;	 //返回false 可能为未登录、无数据、调用异常等情况
	private String mobile;
	private String verifyCode;
	private String result;
	
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getVerifyCode() {
		return verifyCode;
	}
	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}
	
	
	
	
}
