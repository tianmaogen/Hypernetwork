package com.model;

import java.util.List;

public class UserBeanListAndVal {
	private Integer val;
	private List<UserBean> userBeanList;
	
	public UserBeanListAndVal(Integer val, List<UserBean> userBeanList) {
		super();
		//½«1,2===1  3===2  4,5====3
//		if(val == 1 || val == 2)
//			this.val = 1;
//		else if(val == 3)
//			this.val = 2;
//		else
//			this.val = 3;
		this.val = val;
		this.userBeanList = userBeanList;
	}
	
	public Integer getVal() {
		return val;
	}
	public void setVal(Integer val) {
		this.val = val;
	}
	public List<UserBean> getUserBeanList() {
		return userBeanList;
	}
	public void setUserBeanList(List<UserBean> userBeanList) {
		this.userBeanList = userBeanList;
	}
	
}
