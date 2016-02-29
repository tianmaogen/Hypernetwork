package com.v2;

public class Hyperedge {
	
	private String itemId;
	//userId-score
	private String[] content;
	private Integer val;
	
	private int rCount=0; //对于匹配且分类正确的适应值ֵ
	private int wCount=0; //对于匹配且分类错误的适应值ֵ
	
	public Hyperedge(String itemId, String[] content, Integer val) {
		super();
		this.itemId = itemId;
		this.content = content;
		this.val = val;
	}
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String[] getContent() {
		return content;
	}

	public void setContent(String[] content) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + rCount;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		result = prime * result + wCount;
		return result;
	}

	
	
}
