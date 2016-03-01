package com.model;

/**
 * 多线程模型下超网络运算的结果
 */
public class UserTestResultBean
{
	// 训练集合的准确率
	private double trainAccuracy;
	// 测试集合的准确率
	private double testAccuracy;
	// 均方根误差分子
	private double numeratorRMSE;
	// 平均绝对误差分子
	private double numeratorMAE;

	public UserTestResultBean(double trainAccuracy, double testAccuracy, double numeratorRMSE, double numeratorMAE)
	{
		super();
		this.trainAccuracy = trainAccuracy;
		this.testAccuracy = testAccuracy;
		this.numeratorRMSE = numeratorRMSE;
		this.numeratorMAE = numeratorMAE;
	}

	public double getTrainAccuracy()
	{
		return trainAccuracy;
	}

	public void setTrainAccuracy(double trainAccuracy)
	{
		this.trainAccuracy = trainAccuracy;
	}

	public double getTestAccuracy()
	{
		return testAccuracy;
	}

	public void setTestAccuracy(double testAccuracy)
	{
		this.testAccuracy = testAccuracy;
	}

	public double getNumeratorRMSE()
	{
		return numeratorRMSE;
	}

	public void setNumeratorRMSE(double numeratorRMSE)
	{
		this.numeratorRMSE = numeratorRMSE;
	}

	public double getNumeratorMAE()
	{
		return numeratorMAE;
	}

	public void setNumeratorMAE(double numeratorMAE)
	{
		this.numeratorMAE = numeratorMAE;
	}
	

}
