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
 * 预测用户的评分 V2版本
 * @author my
 *
 */
public class MovieForecastV2 {

	private static final String sourcePath = "src//main//resources//u.data";
	public static void main(String[] args)
			throws InterruptedException, ExecutionException {
		long startTime = System.currentTimeMillis();
		int order = 4;
		FilePrintUtil.filePath = "src//main//resources//介数为"+order+"-12-12-V2-modify.log";
		
		FilePrintUtil.writeOneLine("开始时间为===========" + new Date());
//		List<String> userIdList = new ArrayList<>();
//		userIdList.add("533");
//		userIdList.add("660");
//		userIdList.add("932");
//		userIdList.add("49");
//		userIdList.add("280");
//		userIdList.add("653");
//		userIdList.add("18");
//		userIdList.add("535");
//		userIdList.add("87");
//		userIdList.add("339");
//		userIdList.add("643");
//
//		userIdList.add("194");
//		userIdList.add("62");
//		userIdList.add("642");
//		userIdList.add("286");
//		userIdList.add("334");
//		userIdList.add("749");
//		userIdList.add("345");
//		userIdList.add("537");
//		userIdList.add("650");
//		userIdList.add("342");
		List<String> userIdList = findSuitUserIdList();
		double totalAccuracy = 0.0;
		double totalRMSE = 0.0;
		for (String userId : userIdList) {
//			List<String> printStrs = new ArrayList<>();
//			long startTime1 = System.currentTimeMillis();
			Map<String, UserBeanSet> map = getUserMap(userId);
//			long endTime1 = System.currentTimeMillis();
//			printTime(startTime1, endTime1, "getUserMap");
			
			FilePrintUtil.writeOneLine("############################################");
			FilePrintUtil.writeOneLine("开始预测userId为=====" + userId + "的准确率!");
//			startTime1 = System.currentTimeMillis();
			Map<String, Map<String, UserBeanSet>> testAndTrainMap = getTestAndTrainMap(map);
			Map<String, UserBeanSet> trainMap = testAndTrainMap.get("trainMap");
			Map<String, UserBeanSet> testMap = testAndTrainMap.get("testMap");
//			endTime1 = System.currentTimeMillis();
//			printTime(startTime1, endTime1, "getTestAndTrainMap");

			FilePrintUtil.writeOneLine("trainMapSize=====" + trainMap.size());
			FilePrintUtil.writeOneLine("testMapSize======" + testMap.size());

			Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap,order);
			
//			startTime1 = System.currentTimeMillis();
			double trainAccuracy = hypernetworks.train();
//			endTime1 = System.currentTimeMillis();
//			printTime(startTime1, endTime1, "train");
			
			FilePrintUtil.writeOneLine("训练集的准确率为=================" + trainAccuracy);
			
//			startTime1 = System.currentTimeMillis();
			double testAccuracy = hypernetworks.test();
//			endTime1 = System.currentTimeMillis();
//			printTime(startTime1, endTime1, "test");
			
			FilePrintUtil.writeOneLine("测试集的准确率为=================" + testAccuracy);
			totalAccuracy += testAccuracy;
//			startTime1 = System.currentTimeMillis();
			double testRMSE = hypernetworks.getRMSEnumerator();
//			endTime1 = System.currentTimeMillis();
//			printTime(startTime1, endTime1, "getRMSE");
			
			totalRMSE += testRMSE;
			FilePrintUtil.writeOneLine("测试集的均方根误差为=================" + testRMSE);
			FilePrintUtil.writeOneLine("############################################");
//			MyFileUtil.writeLines(printStrs);
		}
		
		int size = userIdList.size();
		double avgAccuracy = totalAccuracy / size;
		double avgTestRMSE = totalRMSE / size;
		long endTime = System.currentTimeMillis();
		FilePrintUtil.writeOneLine("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		FilePrintUtil.writeOneLine("总的准确率平均为:"+avgAccuracy);
		FilePrintUtil.writeOneLine("总的均方根误差平均为:"+avgTestRMSE);
		FilePrintUtil.writeOneLine("结束时间为==========="+new Date());
		FilePrintUtil.writeOneLine("总耗时为 "+(endTime-startTime)/(1000*60)+" 分钟");

	}

	public static Map<String,Map<String, UserBeanSet>> getTestAndTrainMap(Map<String, UserBeanSet> map) {
		Map<String,Map<String, UserBeanSet>> returnMap = new HashMap<>();
		Map<String, UserBeanSet> trainMap = new HashMap<>();
		Map<String, UserBeanSet> testMap = new HashMap<>();
		returnMap.put("trainMap", trainMap);
		returnMap.put("testMap", testMap);
		Random r = new Random();
		int k = 8;
		for(String itemId : map.keySet()) {
			if(r.nextInt(k) == 3) 
				testMap.put(itemId, map.get(itemId));
			else 
				trainMap.put(itemId, map.get(itemId));
		}
		return returnMap;
	}
	public static Map<String, UserBeanSet> getUserMap(String userId) {

		Map<String, UserBeanSet> newMap = new HashMap<>();
		Map<String, List<UserBean>> map = MyFileUtils.getItemMap(sourcePath);
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
	
	public static List<String> findSuitUserIdList() {
		
		List<String> userIdList = new ArrayList<>();
		
		Map<String, List<ItemBean>> map = MyFileUtils.getUserMap(sourcePath);
		FilePrintUtil.writeOneLine("测试数据总大小为==========="+map.size());
		for (String userId : map.keySet()) {
//			System.out.println("userId=====" + userId + "size============" 	+ map.get(userId).size());
			if(map.get(userId).size() > 200)
				userIdList.add(userId);
		}
		FilePrintUtil.writeOneLine("要预测的user大小为==========="+userIdList.size());
		return userIdList;
	}

	public static void printTime(long startTime, long endTime ,String str) {
		long time = (endTime - startTime) ;
		System.out.println(str + "耗时：" + String.valueOf(time) + "毫秒");
	}
}
