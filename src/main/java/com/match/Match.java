package com.match;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.io.FilePrintUtil;
import com.io.MatchingFileUtils;
import com.v1.MyFileUtils;
import com.v1.model.UserBean;
import com.v2.Hypernetworks;
import com.v2.UserBeanSet;

public class Match {
	private static final String trainSourceFile = "src//main//resources//u2.base";
	private static final String testSourceFile = "src//main//resources//u2.test";
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		int order = 4;
		FilePrintUtil.filePath = "src//main//resources//介数为"+order+"-12-12-matching.log";
		
		FilePrintUtil.writeOneLine("开始时间为===========" + new Date());
		Set<String> userIdList = MatchingFileUtils.getTestUserIdList(testSourceFile);

		FilePrintUtil.writeOneLine("要预测的user大小为==========="+userIdList.size());
		double totalAccuracy = 0.0;
		double totalNumerator = 0.0;
		for (String userId : userIdList) {
			FilePrintUtil.writeOneLine("############################################");
			FilePrintUtil.writeOneLine("开始预测userId为=====" + userId + "的准确率!");
			
			Map<String, UserBeanSet> trainMap = getUserMap(userId,trainSourceFile);
			Map<String, UserBeanSet> testMap = getUserMap(userId,testSourceFile);
			
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
		
		int size = 20000;
		double avgAccuracy = totalAccuracy / size;
		double avgTestRMSE = Math.sqrt(totalNumerator/size);
		long endTime = System.currentTimeMillis();
		FilePrintUtil.writeOneLine("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		FilePrintUtil.writeOneLine("总的准确率平均为:"+avgAccuracy);
		FilePrintUtil.writeOneLine("总的均方根误差平均为:"+avgTestRMSE);
		FilePrintUtil.writeOneLine("结束时间为==========="+new Date());
		FilePrintUtil.writeOneLine("总耗时为 "+(endTime-startTime)/(1000*60)+" 分钟");
	}
	
	/**
	 * 获取该user的测试集map或者训练集map
	 * @param userId 用户id
	 * @param filePath 测试集或训练集的文件路径
	 * @return
	 */
	public static Map<String, UserBeanSet> getUserMap(String userId, String filePath) {

		Map<String, UserBeanSet> newMap = new HashMap<>();
		Map<String, List<UserBean>> map = MyFileUtils.getUserMap(filePath);
		for (String itemId : map.keySet()) {
			List<UserBean> userList = map.get(itemId);
			
			if(userList.size() < 5)
				continue;
			
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
				//Map<String,Integer> userscoreMap
				HashSet<String> userScoreSet = new HashSet<>();
				for(UserBean userBean : userList) 
					userScoreSet.add(userBean.getUserId() + "-" + userBean.getScore());
				
				UserBeanSet userBeanMapAndVal = new UserBeanSet(val, userScoreSet);
				newMap.put(itemId, userBeanMapAndVal);
			}
		}

		return newMap;
	}
}
