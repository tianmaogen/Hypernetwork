package com.v1.model;

import java.util.List;

public class Hyperedge {
	
	private String itemId;
	private List<UserBean> content;
	private Integer val;
	
	private int rCount=0; //对于匹配且分类正确的适应值ֵ
	private int wCount=0; //对于匹配且分类错误的适应值ֵ
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public List<UserBean> getContent() {
		return content;
	}
	public void setContent(List<UserBean> content) {
		this.content = content;
	}
	public Integer getVal() {
		return val;
	}
	public void setVal(Integer val) {
		this.val = val;
	}
	public int getrCount() {
		return rCount;
	}
	public void setrCount(int rCount) {
		this.rCount = rCount;
	}
	public int getwCount() {
		return wCount;
	}
	public void setwCount(int wCount) {
		this.wCount = wCount;
	}
	
	public boolean moreRight(Hyperedge target) {
		if(this.wCount < target.wCount)
			return true;
		
		if(this.wCount == target.wCount && this.rCount > target.rCount)
			return true;
		
		return false;
	}
	
}
