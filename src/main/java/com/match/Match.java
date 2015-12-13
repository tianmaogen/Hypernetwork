package com.match;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.io.FilePrintUtil;
import com.io.MatchingFileUtils;
import com.v1.model.UserBean;
import com.v2.Hypernetworks;
import com.v2.UserBeanSet;

public class Match {
	private String trainSourceFile = "src//main//resources//u2.base";
	private String testSourceFile = "src//main//resources//u2.test";
	private int order = 4;
	//测试样集中用户对电影的评分数-数量
	private int testSamplesize = 20000;
	//所有用户对电影的平均评分
	private double avgScore = 3.5299;
	//未通过超网络模型预测的userID集合
	private Set<String> excludeUserIds = new HashSet<>();
	
	public void matching() {

		long startTime = System.currentTimeMillis();
		
		FilePrintUtil.filePath = "src//main//resources//介数为" + order + "-12-12-matching.log";
		
		FilePrintUtil.writeOneLine("开始时间为===========" + new Date());
		Set<String> userIdList = MatchingFileUtils.getTestUserIdList(testSourceFile);

		FilePrintUtil.writeOneLine("要预测的user大小为==========="+userIdList.size());
		double totalAccuracy = 0.0;
		double totalNumerator = 0.0;
		for (String userId : userIdList) {
			FilePrintUtil.writeOneLine("############################################");
			FilePrintUtil.writeOneLine("开始预测userId为=====" + userId + "的准确率!");
			
			Map<String, UserBeanSet> trainMap = getItemUserBeanSetMap(userId,trainSourceFile, false);
			Map<String, UserBeanSet> testMap = getItemUserBeanSetMap(userId,testSourceFile, true);
			
			if(testMap.size() == 0)
				continue;
			
			FilePrintUtil.writeOneLine("trainMapSize=====" + trainMap.size());
			FilePrintUtil.writeOneLine("testMapSize======" + testMap.size());

			Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap,order);
			double trainAccuracy = hypernetworks.train();
			FilePrintUtil.writeOneLine("训练集的准确率为=================" + trainAccuracy);
			double testAccuracy = hypernetworks.test();
			FilePrintUtil.writeOneLine("测试集的准确率为=================" + testAccuracy);
			totalAccuracy += testAccuracy;
			double numerator = hypernetworks.getRMSE();
			totalNumerator += numerator;
			FilePrintUtil.writeOneLine("测试集的均方根误差分子为=================" + numerator);
			FilePrintUtil.writeOneLine("############################################");
		}
		
		double avgAccuracy = totalAccuracy / userIdList.size();
		
		double excludeUserIdsNumerator = MatchingFileUtils.getExcludeUserIdsNumerator(avgScore, excludeUserIds, testSourceFile);
		totalNumerator += excludeUserIdsNumerator;
		
		double avgTestRMSE = Math.sqrt(totalNumerator / testSamplesize);
		long endTime = System.currentTimeMillis();
		FilePrintUtil.writeOneLine("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		FilePrintUtil.writeOneLine("总的准确率平均为:"+avgAccuracy);
		FilePrintUtil.writeOneLine("总的均方根误差平均为:"+avgTestRMSE);
		FilePrintUtil.writeOneLine("测试集未进行超网络预测的均方根误差分子为=================" + excludeUserIdsNumerator);
		FilePrintUtil.writeOneLine("结束时间为==========="+new Date());
		FilePrintUtil.writeOneLine("总耗时为 "+(endTime-startTime)/(1000*60)+" 分钟");
	
	}
	/**
	 * 获取该user的测试集map或者训练集map
	 * @param userId 用户id
	 * @param filePath 测试集或训练集的文件路径
	 * @param isTest 是否是测试样集在调用
	 * @return
	 */
	public Map<String, UserBeanSet> getItemUserBeanSetMap(String userId, String filePath, boolean isTest) {

		Map<String, UserBeanSet> newMap = new HashMap<>();
		Map<String, List<UserBean>> map = MatchingFileUtils.getItemUserListMap(filePath);
		for (String itemId : map.keySet()) {
			List<UserBean> userList = map.get(itemId);
			
			if(userList.size() < 5) {
				//如果是测试样集，将userId写进excludeUserIds，用于计算最后的误差平方根
				if(isTest)
					excludeUserIds.add(userId);
				continue;
			}
				
			
			boolean hasUserId = false;
			Integer val = 0;
			for (UserBean userBean : userList) {
				if (userBean.getUserId().equals(userId)) {
					hasUserId = true;
					val = userBean.getScore();
					break;
				}
			}
			if (hasUserId) {
				HashSet<String> userScoreSet = new HashSet<>();
				for(UserBean userBean : userList) 
					userScoreSet.add(userBean.getUserId() + "-" + userBean.getScore());
				
				UserBeanSet userBeanSet = new UserBeanSet(val, userScoreSet);
				newMap.put(itemId, userBeanSet);
			}
		}

		return newMap;
	}
	
	public static void main(String[] args) {
		Match match = new Match();
		match.matching();
	}
	
}
