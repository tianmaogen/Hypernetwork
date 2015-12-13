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

public class MovieForecast {
	
	private static final String sourcePath = "src//main//resources//u.data";
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		long startTime = System.currentTimeMillis();
		FilePrintUtil.filePath = "src//main//resources//介数为4每个样本产生100条超边-12-9.log";
		FilePrintUtil.writeOneLine("开始时间为==========="+new Date());
		ApplicationContext ctx =  new ClassPathXmlApplicationContext("applicationContext.xml");
		ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor)ctx.getBean("threadPoolTaskExecutor");
		
		List<String> userIdList = findSuitUserIdList();
		
		List<Future<Map<String,Double>>> futures = new ArrayList<>();
		 
		for(final String userId : userIdList) {
			Future<Map<String,Double>> future = executor.submit(new Callable<Map<String,Double>>() {
				@Override
				public Map<String,Double> call() {
					List<String> printStrs = new ArrayList<>();
					Map<String, UserBeanListAndVal> map = getUserMap(userId);
					printStrs.add("############################################");
					printStrs.add("开始预测userId为====="+userId+"的准确率!");
					Map<String, Map<String, UserBeanListAndVal>> testAndTrainMap = getTestAndTrainMap(map);
					Map<String, UserBeanListAndVal> trainMap = testAndTrainMap.get("trainMap");
					Map<String, UserBeanListAndVal> testMap = testAndTrainMap.get("testMap");
					
					printStrs.add("trainMapSize====="+trainMap.size());
					printStrs.add("testMapSize======"+testMap.size());
					
					Hypernetworks hypernetworks = new Hypernetworks(trainMap, testMap);
					double trainAccuracy = hypernetworks.train();
					printStrs.add("训练集的准确率为================="+trainAccuracy);
					double testAccuracy = hypernetworks.test();
					printStrs.add("测试集的准确率为================="+testAccuracy);
					double testRMSE = hypernetworks.getRMSE();
					printStrs.add("测试集的均方根误差为=================" + testRMSE);
					printStrs.add("############################################");
					FilePrintUtil.writeLines(printStrs);
					Map<String,Double> returnMap = new HashMap<>();
					returnMap.put("testAccuracy", testAccuracy);
					returnMap.put("testRMSE", testRMSE);
					return returnMap;
				}
			});
			
			futures.add(future);
		}
		int count1=0,count2=0,count3=0,count4=0,count5=0;
		double totalAccuracy = 0.0;
		double totalRMSE = 0.0;
        for (Future<Map<String,Double>> future : futures) {
        	Map<String,Double> map = future.get();
        	Double testAccuracy = map.get("testAccuracy");
        	Double testRMSE = map.get("testRMSE");
            if(testAccuracy < 0.2)
            	count1++;
            else if(testAccuracy < 0.4)
            	count2++;
            else if(testAccuracy < 0.6)
            	count3++;
            else if(testAccuracy < 0.8)
            	count4++;
            else
            	count5++;
            
            totalAccuracy += testAccuracy;
            
            totalRMSE += testRMSE;
        }
        int size = userIdList.size();
        double avgAccuracy = totalAccuracy / size ;
        double avgRMSE = totalRMSE / size ;
        long endTime = System.currentTimeMillis();
        FilePrintUtil.writeOneLine("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        FilePrintUtil.writeOneLine("总的预测准确率平均为:" + avgAccuracy);
        FilePrintUtil.writeOneLine("		<0.2		0.2~0.4		0.4~0.6		0.6~0.8		0.8~1.0");
        FilePrintUtil.writeOneLine("		"+count1+"		"+count2+"		"+count3+"		"+count4+"		"+count5);
        FilePrintUtil.writeOneLine("总的均方根误差平均为:" + avgRMSE);
        FilePrintUtil.writeOneLine("结束时间为==========="+new Date());
        FilePrintUtil.writeOneLine("总耗时为 " + (endTime-startTime)/(1000*60) + " 分钟");
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
		FilePrintUtil.writeOneLine("测试数据总大小为==========="+map.size());
		for (String userId : map.keySet()) {
//			System.out.println("userId=====" + userId + "size============" 	+ map.get(userId).size());
			if(map.get(userId).size() > 200)
				userIdList.add(userId);
		}
		FilePrintUtil.writeOneLine("要预测的user大小为==========="+userIdList.size());
		return userIdList;
	}
	
}
