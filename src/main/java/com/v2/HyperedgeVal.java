package com.v2;

/**
 * 基于map超边的值，包括物品Id和基于决策用户和该物品的评分
 */
public class HyperedgeVal {
	
	private String itemId; //物品Id
	private int val; //基于决策用户和该物品的评分
	
	private int rNum; //计算正确的样本数
	private int wNun; //计算错误的样本数
	
	public HyperedgeVal(String itemId, int val) {
		super();
		this.itemId = itemId;
		this.val = val;
	}
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public int getVal() {
		return val;
	}
	public void setVal(int val) {
		this.val = val;
	}

	public int getrNum() {
		return rNum;
	}

	public void setrNum(int rNum) {
		this.rNum = rNum;
	}

	public int getwNun() {
		return wNun;
	}

	public void setwNun(int wNun) {
		this.wNun = wNun;
	}
	
}
