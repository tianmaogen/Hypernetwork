package com.v2;

import java.util.HashSet;

public class UserBeanSet {
	private Integer val;
	private HashSet<String> userScoreSet;
	
	public UserBeanSet(Integer val, HashSet<String> userScoreSet) {
		super();
		this.val = val;
		this.userScoreSet = userScoreSet;
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
