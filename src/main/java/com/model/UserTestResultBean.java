package com.model;

/**
 * 多线程模型下超网络运算的结果
 */
public class UserTestResultBean {
	//训练集合的准确率
	private double trainAccuracy;
	//测试集合的准确率
	private double testAccuracy;
	//均方根误差分子
	private double numerator;
	
	public UserTestResultBean(double trainAccuracy, double testAccuracy, double numerator) {
		super();
		this.trainAccuracy = trainAccuracy;
		this.testAccuracy = testAccuracy;
		this.numerator = numerator;
	}
	public double getTrainAccuracy() {
		return trainAccuracy;
	}
	public void setTrainAccuracy(double trainAccuracy) {
		this.trainAccuracy = trainAccuracy;
	}
	public double getTestAccuracy() {
		return testAccuracy;
	}
	public void setTestAccuracy(double testAccuracy) {
		this.testAccuracy = testAccuracy;
	}
	public double getNumerator() {
		return numerator;
	}
	public void setNumerator(double numerator) {
		this.numerator = numerator;
	}
	
	
}
