package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.model.ItemBean;
import com.model.UserBean;
import com.model.UserBeanListAndVal;

public class MovieForecast {
	private static final String sourcePath = "src//main//resources//u.data";
	public static void main(String[] args) {
		
		List<String> userIdList = findSuitUserIdList();
		
		for(String userId : userIdList) {
			Map<String, UserBeanListAndVal> map = getUserMap(userId);
			MyFileUtils.printToFile("############################################");
			MyFileUtils.printToFile("开始预测userId为====="+userId+"的准确率！");
			Map<String, Map<String, UserBeanListAndVal>> testAndTrainMap = getTestAndTrainMap(map);
			Map<String, UserBeanListAndVal> trainMap = testAndTrainMap.get("trainMap");
			Map<String, UserBeanListAndVal> testMap = testAndTrainMap.get("testMap");
			
			MyFileUtils.printToFile("trainMapSize====="+trainMap.size());
			MyFileUtils.printToFile("testMapSize======"+testMap.size());
			
			Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap);
			double trainRate = hypernetworks.train();
			MyFileUtils.printToFile("训练集的准确率为================="+trainRate);
			double testRate = hypernetworks.test();
			MyFileUtils.printToFile("测试集的准确率为================="+testRate);
			MyFileUtils.printToFile("############################################");
		}

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
	public static Map<String,UserBeanListAndVal> getUserMap(String userId) {

		Map<String,UserBeanListAndVal> newMap = new HashMap<>();
		Map<String, List<UserBean>> map = MyFileUtils.getUserMap(sourcePath);
		for(String itemId : map.keySet()) {
			List<UserBean> userList = map.get(itemId);
			boolean hasMaxUserId = false;
			Integer val=0;
			for(UserBean userBean : userList) {
				if(userBean.getUserId().equals(userId)) {
					hasMaxUserId = true;
					val = userBean.getScore();
					break;
				}
			}
			if(hasMaxUserId && userList.size() >= 40) {
				UserBeanListAndVal userBeanListAndVal = new UserBeanListAndVal(val,userList);
				newMap.put(itemId, userBeanListAndVal);
//				System.out.println("itemId===="+itemId+"userSize======"+userList.size());
			}
		}
		
		return newMap;
	}
	
	public static List<String> findSuitUserIdList() {
		
		List<String> userIdList = new ArrayList<>();
		
		Map<String, List<ItemBean>> map = MyFileUtils.getItemMap(sourcePath);
		MyFileUtils.printToFile("测试数据总大小为==========="+map.size());
		for (String userId : map.keySet()) {
//			System.out.println("userId=====" + userId + "size============" 	+ map.get(userId).size());
			if(map.get(userId).size() > 100)
				userIdList.add(userId);
		}
		MyFileUtils.printToFile("要预测的user大小为==========="+userIdList.size());
		return userIdList;
	}
	
}
