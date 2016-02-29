package com.v2;

import java.util.HashSet;

public class UserBeanSet {
	//目标用户对特定的item的评分
	private Integer val;
	//其他用户对特定的item的评分集合，集合中的每个元素由userId+score组成的string字符串组成
	private HashSet<String> userScoreSet;
	
	public UserBeanSet(Integer val, HashSet<String> userScoreSet) {
		super();
		this.val = val;
		this.userScoreSet = userScoreSet;
	}
	
	public UserBeanSet() {
		// TODO Auto-generated constructor stub
	}

	public Integer getVal() {
		return val;
	}
	public void setVal(Integer val) {
		this.val = val;
	}

	public HashSet<String> getUserScoreSet() {
		return userScoreSet;
	}

	public void setUserScoreSet(HashSet<String> userScoreSet) {
		this.userScoreSet = userScoreSet;
	}

	
	
	
}
