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

import com.io.MyFileUtil;
import com.v1.Hypernetworks;
import com.v1.MyFileUtils;
import com.v1.model.ItemBean;
import com.v1.model.UserBean;
import com.v1.model.UserBeanListAndVal;

public class MovieForecastSingelThread {

	private static final String sourcePath = "src//main//resources//u.data";
	public static void main(String[] args)
			throws InterruptedException, ExecutionException {
		long startTime = System.currentTimeMillis();
		MyFileUtil.writeOneLine("开始时间为===========" + new Date());
		List<String> userIdList = findSuitUserIdList();

		for (String userId : userIdList) {
			List<String> printStrs = new ArrayList<>();
			Map<String, UserBeanListAndVal> map = getUserMap(userId);
			printStrs.add("############################################");
			printStrs.add("开始预测userId为=====" + userId + "的准确率!");
			Map<String, Map<String, UserBeanListAndVal>> testAndTrainMap = getTestAndTrainMap(map);
			Map<String, UserBeanListAndVal> trainMap = testAndTrainMap.get("trainMap");
			Map<String, UserBeanListAndVal> testMap = testAndTrainMap.get("testMap");

			printStrs.add("trainMapSize=====" + trainMap.size());
			printStrs.add("testMapSize======" + testMap.size());

			Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap);
			double trainAccuracy = hypernetworks.train();
			printStrs.add("训练集的准确率为=================" + trainAccuracy);
			double testAccuracy = hypernetworks.test();
			printStrs.add("测试集的准确率为=================" + testAccuracy);
			printStrs.add("############################################");
			MyFileUtil.writeLines(printStrs);

		}
		
		double totalAccuracy = 0.0;
		int size = userIdList.size();
		double avgAccuracy = totalAccuracy / size;
		long endTime = System.currentTimeMillis();
		MyFileUtil.writeOneLine("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		MyFileUtil.writeOneLine("总的预测准确率平均为:"+avgAccuracy);
		MyFileUtil.writeOneLine("结束时间为==========="+new Date());
		MyFileUtil.writeOneLine("总耗时为 "+(endTime-startTime)/(1000*60)+" 分钟");

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
		MyFileUtil.writeOneLine("测试数据总大小为===========" + map.size());
		for (String userId : map.keySet()) {
			// System.out.println("userId=====" + userId + "size============" +
			// map.get(userId).size());
			if (map.get(userId).size() > 300)
				userIdList.add(userId);
		}
		MyFileUtil.writeOneLine("要预测的user大小为===========" + userIdList.size());
		return userIdList;
	}

}
