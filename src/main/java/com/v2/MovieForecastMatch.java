package com.v2;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.io.FilePrintUtil;
import com.v1.MyFileUtils;
import com.v1.model.ItemBean;
import com.v1.model.UserBean;

/**
 * 使用官方提供的测试集和训练集来预测用户的评分
 * 
 * @author my
 *
 */
public class MovieForecastMatch
{

	private static final String trainSourcePath = "src//main//resources//u1.base";
	private static final String testSourcePath = "src//main//resources//u1.test";

	public static void main(String[] args) throws InterruptedException, ExecutionException
	{
		long startTime = System.currentTimeMillis();
		int order = 4;
		FilePrintUtil.filePath = "src//main//resources//介数为" + order + "-2-27-V2-modify.log";

		FilePrintUtil.writeOneLine("开始时间为===========" + new Date());
		List<String> userIdList = findUserIdList();
		double totalAccuracy = 0.0;
		double totalRMSE = 0.0;
		for (String userId : userIdList)
		{
			Map<String, UserBeanSet> trainMap = getUserMap(userId, trainSourcePath);
			Map<String, UserBeanSet> testMap = getUserMap(userId, testSourcePath);

			FilePrintUtil.writeOneLine("############################################");
			FilePrintUtil.writeOneLine("开始预测userId为=====" + userId + "的准确率!");
			// Map<String, Map<String, UserBeanSet>> testAndTrainMap = getTestAndTrainMap(map);
			// Map<String, UserBeanSet> trainMap = testAndTrainMap.get("trainMap");
			// Map<String, UserBeanSet> testMap = testAndTrainMap.get("testMap");

			FilePrintUtil.writeOneLine("trainMapSize=====" + trainMap.size());
			FilePrintUtil.writeOneLine("testMapSize======" + testMap.size());

			Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap, order);

			// startTime1 = System.currentTimeMillis();
			double trainAccuracy = hypernetworks.train();
			// endTime1 = System.currentTimeMillis();
			// printTime(startTime1, endTime1, "train");

			FilePrintUtil.writeOneLine("训练集的准确率为=================" + trainAccuracy);

			// startTime1 = System.currentTimeMillis();
			double testAccuracy = hypernetworks.test();
			// endTime1 = System.currentTimeMillis();
			// printTime(startTime1, endTime1, "test");

			FilePrintUtil.writeOneLine("测试集的准确率为=================" + testAccuracy);
			totalAccuracy += testAccuracy;
			// startTime1 = System.currentTimeMillis();
			double testRMSE = hypernetworks.getRMSEnumerator();
			// endTime1 = System.currentTimeMillis();
			// printTime(startTime1, endTime1, "getRMSE");

			totalRMSE += testRMSE;
			FilePrintUtil.writeOneLine("测试集的均方根误差为=================" + testRMSE);
			FilePrintUtil.writeOneLine("############################################");
			// MyFileUtil.writeLines(printStrs);
		}

		int size = userIdList.size();
		double avgAccuracy = totalAccuracy / size;
		double avgTestRMSE = Math.sqrt(totalRMSE / size);
		long endTime = System.currentTimeMillis();
		FilePrintUtil.writeOneLine("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		FilePrintUtil.writeOneLine("总的准确率平均为:" + avgAccuracy);
		FilePrintUtil.writeOneLine("总的均方根误差平均为:" + avgTestRMSE);
		FilePrintUtil.writeOneLine("结束时间为===========" + new Date());
		FilePrintUtil.writeOneLine("总耗时为 " + (endTime - startTime) / (1000 * 60) + " 分钟");

	}

	public static Map<String, Map<String, UserBeanSet>> getTestAndTrainMap(Map<String, UserBeanSet> map)
	{
		Map<String, Map<String, UserBeanSet>> returnMap = new HashMap<>();
		Map<String, UserBeanSet> trainMap = new HashMap<>();
		Map<String, UserBeanSet> testMap = new HashMap<>();
		returnMap.put("trainMap", trainMap);
		returnMap.put("testMap", testMap);
		Random r = new Random();
		int k = 8;
		for (String itemId : map.keySet())
		{
			if (r.nextInt(k) == 3)
				testMap.put(itemId, map.get(itemId));
			else
				trainMap.put(itemId, map.get(itemId));
		}
		return returnMap;
	}

	/**
	 * 获取超网络模型的x和y
	 * 
	 * @param userId 目标用户
	 * @param path 数据集的路径(训练集或测试集)
	 * @return 返回一个map 该map的key为itemId,value为UserBeanSet对象， 该对象中的userScoreSet是其他用户对该item的评分数据，该对象中的val时目标用户对该item的评分数据
	 */
	public static Map<String, UserBeanSet> getUserMap(String userId, String path)
	{

		Map<String, UserBeanSet> newMap = new HashMap<>();
		Map<String, List<UserBean>> iTemMap = MyFileUtils.getItemMap(path);
		for (String itemId : iTemMap.keySet())
		{
			List<UserBean> userList = iTemMap.get(itemId);

			if (userList.size() < 5)
				continue;

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
				HashSet<String> userScoreSet = new HashSet<>();
				for (UserBean userBean : userList)
					userScoreSet.add(userBean.getUserId() + "-" + userBean.getScore());

				UserBeanSet userBeanMapAndVal = new UserBeanSet(val, userScoreSet);
				newMap.put(itemId, userBeanMapAndVal);
			}
		}

		return newMap;
	}

	// 从测试集合中找到待预测的userId集合
	public static List<String> findUserIdList()
	{

		List<String> userIdList = new ArrayList<>();

		Map<String, List<ItemBean>> map = MyFileUtils.getUserMap(testSourcePath);
		FilePrintUtil.writeOneLine("测试数据总大小为===========" + map.size());
		for (String userId : map.keySet())
		{
			// if(map.get(userId).size() > 200)
			userIdList.add(userId);
		}
		FilePrintUtil.writeOneLine("要预测的user大小为===========" + userIdList.size());
		return userIdList;
	}

	public static void printTime(long startTime, long endTime, String str)
	{
		long time = (endTime - startTime);
		System.out.println(str + "耗时：" + String.valueOf(time) + "毫秒");
	}
}
