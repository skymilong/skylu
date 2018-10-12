package com.example.zhanghao.skylu.httpTool.dz;

public class GetUserInfos {
	private boolean state = false; // 返回false 可能为未登录、无数据、调用异常等情况
	private String result;
	private String uid;
	private int score = 0; // 积分
	private int balance = 0; // 卓码币
	private int phoneMaxSize = 0; //最大号码数

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getPhoneMaxSize() {
		return phoneMaxSize;
	}

	public void setPhoneMaxSize(int phoneMaxSize) {
		this.phoneMaxSize = phoneMaxSize;
	}

}
