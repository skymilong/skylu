package com.example.zhanghao.skylu.httpTool.dz;

public class AddIgnoreListResp {

	/**
	 * 调用状态
	 * 返回false 可能为未登录、无数据、调用异常等情况
	 */
	private boolean state = false;	 
	private int row;	
	private String result;
	
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
	
}
