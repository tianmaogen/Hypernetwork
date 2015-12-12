package com.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.io.MyFileUtil;
import com.v1.forecast.UserMovieForecast;
/**
 * 
 */
public class Hypernetworks {
	private int maxIterationCount=400;
	private Map<String, UserBeanSet> trainMap;
	private Map<String, UserBeanSet> testMap;
//	private String userId;
	//超边的介数
	private int order=4;
	//总的超边库的数量
	private int hyperedgeTotalCount=100000;
	//每一个样本应该产生的超边数
	private int hyperedgeCount;
	private Random random = new Random();
//	private double weight = 0.5;
	//超边集合
	private List<Hyperedge> hyperedgeList;
	
    // 线程数
    private final int THREAD_NUM = 15;
    // 初始化线程池
    private ExecutorService es = Executors.newFixedThreadPool(THREAD_NUM);
	
	public Hypernetworks(Map<String, UserBeanSet> trainMap, Map<String, UserBeanSet> testMap) {
		super();
		this.trainMap = trainMap;
		this.testMap = testMap;
	}
	
	public Hypernetworks(Map<String, UserBeanSet> trainMap, Map<String, UserBeanSet> testMap, int order) {
		super();
		this.trainMap = trainMap;
		this.testMap = testMap;
		this.order = order;
	}
	
	public Hypernetworks(Map<String, UserBeanSet> trainMap, Map<String, UserBeanSet> testMap, int order, int hyperedgeTotalCount) {
		super();
		this.trainMap = trainMap;
		this.testMap = testMap;
		this.order = order;
		this.hyperedgeTotalCount = hyperedgeTotalCount;
	}

	public double train() {
//		long startTime = System.currentTimeMillis();
		initHyperdgeList();
//		long endTime = System.currentTimeMillis();
//		UserMovieForecast.printTime(startTime, endTime, "initHyperdgeList");
		
//		long startTime = System.currentTimeMillis();
		double rightRate = getRightRate(trainMap);
//		long endTime = System.currentTimeMillis();
//		UserMovieForecast.printTime(startTime, endTime, "getRightRate");
		
		int iterationCount = maxIterationCount;
		while(rightRate < 0.95 && iterationCount > 0) {
//			startTime = System.currentTimeMillis();
			//替换超边
			replaceHyperedge();
//			endTime = System.currentTimeMillis();
//			UserMovieForecast.printTime(startTime, endTime, "迭代第"+(maxIterationCount-iterationCount)+"次-replaceHyperedge-所用时间");
			
//			startTime = System.currentTimeMillis();
			rightRate = getRightRate(trainMap);
//			endTime = System.currentTimeMillis();
//			UserMovieForecast.printTime(startTime, endTime, "迭代第"+(maxIterationCount-iterationCount)+"次-getRightRate-所用时间");
			iterationCount--;
		}
		MyFileUtil.writeOneLine("迭代了"+(maxIterationCount-iterationCount)+"次");
		return rightRate;
	}
	
	public double test() {
		return getRightRate(testMap);
	}
	
	public double getRMSE() {
		return getRMSE(testMap);
	}
 
	private void replaceHyperedge() {
		//随机选取的item可以产生的超边数量
		int maxRandomCount = (int) (this.hyperedgeCount * 0.6);
		int randomCount = 0;
		String itemId = null;
		
		for(Hyperedge hyperedge : hyperedgeList) {
			if(hyperedge.getrCount() - hyperedge.getwCount() < 2) {
				//随机取一个item,该item可以产生maxRandomCount条超边
				if(randomCount > 0)
					randomCount--;
				else {
					itemId = getRandomItemId();
					randomCount = maxRandomCount;
				}

				UserBeanSet userBeanMapAndVal = trainMap.get(itemId);
				Integer val = userBeanMapAndVal.getVal();
				hyperedge.setVal(val);
				hyperedge.setItemId(itemId);
				
				String[] content = hyperedge.getContent();
				HashSet<String> userScoreSet = userBeanMapAndVal.getUserScoreSet();
				setHyperedgeContent(userScoreSet, content);
			}
			hyperedge.setwCount(0);
			hyperedge.setrCount(0);
		}
	}
	
	private String getRandomItemId() {
		Set<String> keySet = trainMap.keySet();
		int index = random.nextInt(keySet.size());
		int i=0;
		for(String str : keySet) {
			if(i==index)
				return str;
			else
				i++;
		}
		return null;
	}
	
	private double getRightRateSingel(Map<String, UserBeanSet> map) {
		int rightNum = 0;
		for (final Map.Entry<String, UserBeanSet> entry : map.entrySet()) {
			String itemId = entry.getKey();    
			UserBeanSet userBeanListAndVal = entry.getValue();
			Integer val = userBeanListAndVal.getVal();
			HashSet<String> userScoreSet = userBeanListAndVal.getUserScoreSet();
			Map<Integer, Integer> scoreCountMap = new HashMap<>();
			for(Hyperedge hyperedge : hyperedgeList) {
				//如果是由自己产生的超边，则忽略
				if(itemId.equals(hyperedge.getItemId()))
					continue;
				
				if(matching(hyperedge,userScoreSet)) {
					Integer score = hyperedge.getVal();
					if(scoreCountMap.containsKey(score)) {
						Integer count = scoreCountMap.get(score);
						count++;
						scoreCountMap.put(score, count);
					}
					else 
						scoreCountMap.put(score, 1);
					
					if(score == val) 
						hyperedge.setrCount(hyperedge.getrCount() + 1);
					else
						hyperedge.setwCount(hyperedge.getwCount() + 1);
				}
				
			}
			
			int maxScoreNum = 0;
			int maxScore = 3;
			for(int scoreKey : scoreCountMap.keySet()) {
				if(scoreCountMap.get(scoreKey) > maxScoreNum) {
					maxScore = scoreKey;
					maxScoreNum = scoreCountMap.get(scoreKey);
				}
			}
			if(maxScore == val) 
				rightNum++;
			
        }
		double rightRate = rightNum * 1.0/map.size();
		return rightRate;
	
	}
	private double getRightRate(Map<String, UserBeanSet> map) {
		List<Future<Boolean>> futures = new ArrayList<>();
		for (final Map.Entry<String, UserBeanSet> entry : map.entrySet()) {
			Future<Boolean> future = es.submit(new Callable<Boolean>(){
				@Override
				public Boolean call() {
					String itemId = entry.getKey();    
					UserBeanSet userBeanListAndVal = entry.getValue();
					Integer val = userBeanListAndVal.getVal();
					HashSet<String> userScoreSet = userBeanListAndVal.getUserScoreSet();
					Map<Integer, Integer> scoreCountMap = new HashMap<>();
					for(Hyperedge hyperedge : hyperedgeList) {
						//如果是由自己产生的超边，则忽略
						if(itemId.equals(hyperedge.getItemId()))
							continue;
						
						if(matching(hyperedge,userScoreSet)) {
							Integer score = hyperedge.getVal();
							if(scoreCountMap.containsKey(score)) {
								Integer count = scoreCountMap.get(score);
								count++;
								scoreCountMap.put(score, count);
							}
							else 
								scoreCountMap.put(score, 1);
							
							if(score == val) 
								hyperedge.setrCount(hyperedge.getrCount() + 1);
							else
								hyperedge.setwCount(hyperedge.getwCount() + 1);
						}
						
					}
					
					int maxScoreNum = 0;
					int maxScore = 3;
					for(int scoreKey : scoreCountMap.keySet()) {
						if(scoreCountMap.get(scoreKey) > maxScoreNum) {
							maxScore = scoreKey;
							maxScoreNum = scoreCountMap.get(scoreKey);
						}
					}
					if(maxScore == val) 
						return true;
					else
						return false;
				}
			});
			futures.add(future);
		}
		int rightNum = 0;
        for (Future<Boolean> future : futures) {
        	try {
				if(future.get())
					rightNum++;
			} 
        	catch (Exception e) {
				e.printStackTrace();
			}
        }
		double rightRate = rightNum * 1.0/map.size();
		return rightRate;
	}
	
	private boolean matching(Hyperedge hyperedge,HashSet<String> userScoreSet) {
		//userId,userId对该item的评分
		String[] content = hyperedge.getContent();
		for(String key : content) {
			if(!userScoreSet.contains(key))
				return false;
		}
		return true;
	}
	

	private double getRMSE(Map<String, UserBeanSet> map) {
		
		int rightVal = 0;
		
		for(String itemId : map.keySet()) {
			UserBeanSet userBeanListAndVal = map.get(itemId);
			Integer val = userBeanListAndVal.getVal();
			HashSet<String> userScoreSet = userBeanListAndVal.getUserScoreSet();
			Map<Integer, Integer> scoreCountMap = new HashMap<>();
			for(Hyperedge hyperedge : hyperedgeList) {
				if(itemId.equals(hyperedge.getItemId()))
					continue;
				if(matching(hyperedge,userScoreSet)) {
					Integer score = hyperedge.getVal();
					if(scoreCountMap.containsKey(score)) {
						Integer count = scoreCountMap.get(score);
						count++;
						scoreCountMap.put(score, count);
					}
					else 
						scoreCountMap.put(score, 1);
					if(score == val) 
						hyperedge.setrCount(hyperedge.getrCount() + 1);
					else
						hyperedge.setwCount(hyperedge.getwCount() + 1);
				}
			}
			
			int maxScoreNum = 0;
			int maxScore = 3;
			for(int scoreKey : scoreCountMap.keySet()) {
				if(scoreCountMap.get(scoreKey) > maxScoreNum) {
					maxScore = scoreKey;
					maxScoreNum = scoreCountMap.get(scoreKey);
				}
			}
			rightVal += (maxScore-val)*(maxScore-val);
		}
		double rightRate = rightVal * 1.0/map.size();
		return Math.sqrt(rightRate);
	}

	//初始化超边库
	private void initHyperdgeList() {
		hyperedgeList = new ArrayList<>();
		this.hyperedgeCount = hyperedgeTotalCount/trainMap.size();
		for(String itemId : trainMap.keySet()) {
			UserBeanSet userBeanListAndVal = trainMap.get(itemId);
			Integer val = userBeanListAndVal.getVal();
			HashSet<String> userScoreSet = userBeanListAndVal.getUserScoreSet();

			for(int i = 0; i < hyperedgeCount; i++) {
				String[] content = new String[order];
				setHyperedgeContent(userScoreSet, content);
				Hyperedge hyperedge = new Hyperedge(itemId, content, val);
				hyperedgeList.add(hyperedge);
			}
		}
	}

	public void setHyperedgeContent(HashSet<String> userScoreSet, String[] content) {
		for(int i=0; i<order; i++) {
			Integer index = null;
			try {
				index = random.nextInt((userScoreSet.size() - 1));
			} catch (Exception e) {
				System.out.println(1);
			}
			int j = 0;
			for(String key : userScoreSet) {
				if(j == index) {
					content[i] = key;
					break;
				}
				else
					j++;
			}
		}
	}
}
