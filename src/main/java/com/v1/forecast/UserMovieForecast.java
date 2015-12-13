package com.v1.forecast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.io.FilePrintUtil;
import com.v1.Hypernetworks;
import com.v1.MyFileUtils;
import com.v1.model.ItemBean;
import com.v1.model.UserBean;
import com.v1.model.UserBeanListAndVal;

/**
 * 预测单个用户的评分
 * @author my
 *
 */
public class UserMovieForecast {

	private static final String sourcePath = "src//main//resources//u.data";
	public static void main(String[] args)
			throws InterruptedException, ExecutionException {
		long startTime = System.currentTimeMillis();
		int order = 4;
		int hyperedgeCount = 100;
		FilePrintUtil.filePath = "src//main//resources//介数为"+order+"每个样本产生"+hyperedgeCount+"条超边-12-11.log";
		
		FilePrintUtil.writeOneLine("开始时间为===========" + new Date());
		List<String> userIdList = new ArrayList<>();
		userIdList.add("533");
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
//		List<String> userIdList = findSuitUserIdList();
		double totalAccuracy = 0.0;
		double totalRMSE = 0.0;
		for (String userId : userIdList) {
//			List<String> printStrs = new ArrayList<>();
			long startTime1 = System.currentTimeMillis();
			Map<String, UserBeanListAndVal> map = getUserMap(userId);
			long endTime1 = System.currentTimeMillis();
			printTime(startTime1, endTime1, "getUserMap");
			
			FilePrintUtil.writeOneLine("############################################");
			FilePrintUtil.writeOneLine("开始预测userId为=====" + userId + "的准确率!");
			startTime1 = System.currentTimeMillis();
			Map<String, Map<String, UserBeanListAndVal>> testAndTrainMap = getTestAndTrainMap(map);
			Map<String, UserBeanListAndVal> trainMap = testAndTrainMap.get("trainMap");
			Map<String, UserBeanListAndVal> testMap = testAndTrainMap.get("testMap");
			endTime1 = System.currentTimeMillis();
			printTime(startTime1, endTime1, "getTestAndTrainMap");

			FilePrintUtil.writeOneLine("trainMapSize=====" + trainMap.size());
			FilePrintUtil.writeOneLine("testMapSize======" + testMap.size());

			Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap,order, hyperedgeCount);
			
			startTime1 = System.currentTimeMillis();
			double trainAccuracy = hypernetworks.train();
			endTime1 = System.currentTimeMillis();
			printTime(startTime1, endTime1, "train");
			
			FilePrintUtil.writeOneLine("训练集的准确率为=================" + trainAccuracy);
			
			startTime1 = System.currentTimeMillis();
			double testAccuracy = hypernetworks.test();
			endTime1 = System.currentTimeMillis();
			printTime(startTime1, endTime1, "test");
			
			FilePrintUtil.writeOneLine("测试集的准确率为=================" + testAccuracy);
			totalAccuracy += testAccuracy;
			startTime1 = System.currentTimeMillis();
			double testRMSE = hypernetworks.getRMSE();
			endTime1 = System.currentTimeMillis();
			printTime(startTime1, endTime1, "getRMSE");
			
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

	public static Map<String,Map<String, UserBeanListAndVal>> getTestAndTrainMap(Map<String, UserBeanListAndVal> map) {
		Map<String,Map<String, UserBeanListAndVal>> returnMap = new HashMap<>();
		Map<String, UserBeanListAndVal> trainMap = new HashMap<>();
		Map<String, UserBeanListAndVal> testMap = new HashMap<>();
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
	public static Map<String, UserBeanListAndVal> getUserMap(String userId) {

		Map<String, UserBeanListAndVal> newMap = new HashMap<>();
		Map<String, List<UserBean>> map = MyFileUtils.getUserMap(sourcePath);
		for (String itemId : map.keySet()) {
			List<UserBean> userList = map.get(itemId);
			boolean hasMaxUserId = false;
			Integer val = 0;
			for (UserBean userBean : userList) {
				if (userBean.getUserId().equals(userId)) {
					hasMaxUserId = true;
					val = userBean.getScore();
					break;
				}
			}
			if (hasMaxUserId && userList.size() >= 40) {
				UserBeanListAndVal userBeanListAndVal = new UserBeanListAndVal(
						val, userList);
				newMap.put(itemId, userBeanListAndVal);
				// System.out.println("itemId===="+itemId+"userSize======"+userList.size());
			}
		}

		return newMap;
	}
	
	public static List<String> findSuitUserIdList() {
		
		List<String> userIdList = new ArrayList<>();
		
		Map<String, List<ItemBean>> map = MyFileUtils.getItemMap(sourcePath);
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
