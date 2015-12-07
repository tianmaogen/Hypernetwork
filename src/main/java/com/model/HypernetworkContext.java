package com.model;

/**
 * 超网络模型统计类
 * @author my
 *
 */
public class HypernetworkContext {
//	private String UserId; //预测的用户Id
//	private int trainMapSize; //训练集合大小
	private int testMapSize; //测试集合大小
//	private double trainAccuracy; //训练集合正确率
	private double testAccuracy; //测试集合正确率
	
	public int getTestMapSize() {
		return testMapSize;
	}
	public void setTestMapSize(int testMapSize) {
		this.testMapSize = testMapSize;
	}
	public double getTestAccuracy() {
		return testAccuracy;
	}
	public void setTestAccuracy(double testAccuracy) {
		this.testAccuracy = testAccuracy;
	}
	
	
}
