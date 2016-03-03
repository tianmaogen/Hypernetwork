package com.match;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.io.FilePrintUtil;
import com.io.MatchingFileUtils;
import com.model.UserTestResultBean;
import com.v1.model.UserBean;
import com.v2.Hypernetworks;
import com.v2.UserBeanSet;

public class Match
{
	private String trainSourceFile = "src//main//resources//u1.base";
	private String testSourceFile = "src//main//resources//u1.test";
	private int order = 4;
	// 测试样集中用户对电影的评分数-数量
	private int testSamplesize = 20000;
	// 所有用户对电影的平均评分
	private double avgScore = 3.5299;
	// 未通过超网络模型预测的userId-itemId集合
	private Set<String> excludeUserIds = new HashSet<>();

	// 线程数
	private int THREAD_NUM = 5;
	// 初始化线程池
	private ExecutorService es;
	
	//总的超边数
	private int hyperedgeTotalCount;

	/**
	 * @param threadNum 开启多少个线程
	 * @param hyperedgeTotalCount 超边库中超边的数量
	 */
	public Match(int threadNum, int hyperedgeTotalCount)
	{
		this.hyperedgeTotalCount = hyperedgeTotalCount;
		this.THREAD_NUM = threadNum;
		es = Executors.newFixedThreadPool(THREAD_NUM);
//		es = Executors.newCachedThreadPool();
		FilePrintUtil.filePath = "src//main//resources//介数为" + order + "-2-29-matching-"+threadNum+"个线程"+hyperedgeTotalCount+"条超边.log";
	}

	// 多线程模型
	public void groupMatching()
	{

		long startTime = System.currentTimeMillis();
		FilePrintUtil.writeOneLine("开始时间为===========" + new Date());
		Set<String> userIdList = MatchingFileUtils.getTestUserIdList(testSourceFile);
		FilePrintUtil.writeOneLine("要预测的user大小为===========" + userIdList.size());

		List<Future<UserTestResultBean>> futures = new ArrayList<>();

		for (final String userId : userIdList)
		{
			Future<UserTestResultBean> future = es.submit(new Callable<UserTestResultBean>()
			{
				@Override
				public UserTestResultBean call()
				{
					List<String> printStrs = new ArrayList<>();
					printStrs.add("############################################");
					printStrs.add("开始预测userId为=====" + userId + "的准确率!");

					Map<String, UserBeanSet> trainMap = getItemUserBeanSetMap(userId, trainSourceFile, false);
					Map<String, UserBeanSet> testMap = getItemUserBeanSetMap(userId, testSourceFile, true);

					if (testMap.size() == 0)
						return null;
					
					printStrs.add("trainMapSize=====" + trainMap.size());
					printStrs.add("testMapSize======" + testMap.size());

					Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap, order, hyperedgeTotalCount);
					double trainAccuracy = hypernetworks.train();
					printStrs.add("迭代了" + hypernetworks.getIterationCount() + "次");
					printStrs.add("训练集的准确率为=================" + trainAccuracy);
					hypernetworks.setTestResult();
					
					printStrs.add("测试集的准确率为=================" + hypernetworks.getTestRightRate());
					printStrs.add("测试集的均方根误差分子为=================" + hypernetworks.getrMSEnumerator());
					printStrs.add("测试集的平均绝对误差分子为=================" + hypernetworks.getmAEnumerator());
					printStrs.add("############################################");
					
					FilePrintUtil.writeLines(printStrs);

					return new UserTestResultBean(trainAccuracy, hypernetworks.getTestRightRate(), 
							hypernetworks.getrMSEnumerator(), hypernetworks.getmAEnumerator());
				}
			});
			futures.add(future);
		}

		double totalAccuracy = 0.0;
		double totalRMSENumerator = 0.0;
		double totalMAENumerator = 0.0;
		
		for (Future<UserTestResultBean> future : futures)
		{
			UserTestResultBean userTestResultBean = null;
			try
			{
				userTestResultBean = future.get();
				totalAccuracy += userTestResultBean.getTestAccuracy();
				totalRMSENumerator += userTestResultBean.getNumeratorRMSE();
				totalMAENumerator += userTestResultBean.getNumeratorMAE();
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		double avgAccuracy = totalAccuracy / userIdList.size();

		Map<String, Double> excludeUserNumeratorMap = MatchingFileUtils.getExcludeUserIdsNumerator(avgScore, excludeUserIds, testSourceFile);
		totalRMSENumerator += excludeUserNumeratorMap.get("RMSE");
		totalMAENumerator += excludeUserNumeratorMap.get("MAE");

		double avgTestRMSE = Math.sqrt(totalRMSENumerator / testSamplesize);
		double avgTestMAE = totalMAENumerator / testSamplesize;
		
		long endTime = System.currentTimeMillis();
		
		FilePrintUtil.writeOneLine("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		FilePrintUtil.writeOneLine("总的准确率平均为:" + avgAccuracy);
		FilePrintUtil.writeOneLine("总的均方根误差为:" + avgTestRMSE);
		FilePrintUtil.writeOneLine("总的平均绝对误差为:" + avgTestMAE);
		FilePrintUtil.writeOneLine("测试集未进行超网络预测的数量为=================" + excludeUserIds.size());
		FilePrintUtil.writeOneLine("测试集未进行超网络预测的均方根误差分子为=================" + excludeUserNumeratorMap.get("RMSE"));
		FilePrintUtil.writeOneLine("测试集未进行超网络预测的平均绝对误差分子为=================" + excludeUserNumeratorMap.get("MAE"));
		FilePrintUtil.writeOneLine("结束时间为===========" + new Date());
		FilePrintUtil.writeOneLine("总耗时为 " + (endTime - startTime) / (1000 * 60) + " 分钟");

	}

	/**
	 * 获取该user的测试集map或者训练集map
	 * 
	 * @param userId 用户id
	 * @param filePath 测试集或训练集的文件路径
	 * @param isTest 是否是测试样集在调用
	 * @return
	 */
	public Map<String, UserBeanSet> getItemUserBeanSetMap(String userId, String filePath, boolean isTest)
	{

		Map<String, UserBeanSet> newMap = new HashMap<>();
		Map<String, List<UserBean>> map = MatchingFileUtils.getItemUserListMap(filePath);
		for (String itemId : map.keySet())
		{
			List<UserBean> userList = map.get(itemId);

			boolean hasUserId = false;
			Integer val = 0;
			for (UserBean userBean : userList)
			{
				if (userBean.getUserId().equals(userId))
				{
					hasUserId = true;
					val = userBean.getScore();
					break;
				}
			}
			if (hasUserId)
			{
				if (userList.size() < order)
				{
					// 如果是测试样集，将userId写进excludeUserIds，用于计算最后的误差平方根
					if (isTest)
					{
						for (UserBean userBean : userList)
						{
							String userIdItemId = userBean.getUserId() + "-" + itemId;
							excludeUserIds.add(userIdItemId);
						}
					}
					continue;
				}

				HashSet<String> userScoreSet = new HashSet<>();
				for (UserBean userBean : userList)
					userScoreSet.add(userBean.getUserId() + "-" + userBean.getScore());

				UserBeanSet userBeanSet = new UserBeanSet(val, userScoreSet);
				newMap.put(itemId, userBeanSet);

			}
		}

		return newMap;
	}

	public static void main(String[] args)
	{		
		
//		Match match = new Match(3,1000);
//		match.groupMatching();
		
		//开启4个线程
		for(int i=7;i<15;i++) 
		{
			int hyperedgeTotalCount = i * 10000;
			Match match = new Match(4,hyperedgeTotalCount);
			match.groupMatching();
		}
		
		//测试超网络的介数对RMSE的影响

		

		// String testSourceFile = "src//main//resources//u2.test";
		// 测试样集中用户对电影的评分数-数量
		// 所有用户对电影的平均评分
		// double avgScore = 3.5299;
		// 未通过超网络模型预测的userID集合
		// Set<String> excludeUserIds = new HashSet<>();
		// excludeUserIds.add("44");
		// double excludeUserIdsNumerator = MatchingFileUtils.getExcludeUserIdsNumerator(avgScore, excludeUserIds,
		// testSourceFile);
		// System.out.println(excludeUserIdsNumerator);
	}

}
