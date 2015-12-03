package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.model.Hyperedge;
import com.model.UserBean;
import com.model.UserBeanListAndVal;

public class Hypernetworks {
	private int maxIterationCount=30;
	private Map<String, UserBeanListAndVal> trainMap;
	private Map<String, UserBeanListAndVal> testMap;
//	private String userId;
	//超标的介数
	private int order=4;
	private int hyperedgeCount=100;
	private Random random = new Random();
//	private double weight = 0.5;
	//超边库L
	private List<Hyperedge> hyperedgeList;
	
	public Hypernetworks(Map<String, UserBeanListAndVal> trainMap, Map<String, UserBeanListAndVal> testMap) {
		super();
		this.trainMap = trainMap;
		this.testMap = testMap;
	}

	public double train() {
		initHyperdgeList();
		double rightRate = getRightRate(trainMap);
		
		int iterationCount = maxIterationCount;
		while(rightRate < 0.9 && iterationCount > 0) {
			//替换超边
			replaceHyperedge(hyperedgeList,rightRate);
			rightRate = getRightRate(trainMap);
			
			iterationCount--;
		}
		
		return rightRate;
	}
	
	public double test() {
		return getRightRate(testMap);
	}
 
	private void replaceHyperedge(List<Hyperedge> hyperedgeList, double rightRate) {
		
		for(int i=0; i<hyperedgeList.size(); i++) {
			Hyperedge hyperedge = hyperedgeList.get(i);
			//替换掉正确个数与错误个数相等或者正确个数小于错误个数的超边
			if(hyperedge.getrCount() - hyperedge.getwCount() <= 1) {
				//随机选择一条训练样本
				String itemId = getRandomItemId();
				UserBeanListAndVal userBeanListAndVal = trainMap.get(itemId);
				hyperedge.setItemId(itemId);
				hyperedge.setVal(userBeanListAndVal.getVal());
				List<UserBean> content  = new ArrayList<>();
				for(int o = 0; o < order; o++) {
					Integer index = random.nextInt((userBeanListAndVal.getUserBeanList().size()-1));
					UserBean userBean = userBeanListAndVal.getUserBeanList().get(index);
					content.add(userBean);
				}
				hyperedge.setContent(content);
			}
			hyperedge.setwCount(0);
			hyperedge.setrCount(0);
		}
		
		
//		//排序，正确率高的排在最后的位置
//		bubbling(hyperedgeList);
//		//替换前 hyperedgeCount * 10条
//		int replaceHyperedgeCount = (int) ((1.0 - rightRate) * hyperedgeList.size() * weight);
//		for(int i = 0;i < replaceHyperedgeCount;i++) {
//			//随机选择一条训练样本
//			String itemId = getRandomItemId();
//			UserBeanListAndVal userBeanListAndVal = trainMap.get(itemId);
//			Hyperedge hyperedge = new Hyperedge();
//			hyperedge.setItemId(itemId);
//			hyperedge.setVal(userBeanListAndVal.getVal());
//			List<UserBean> content  = new ArrayList<>();
//			for(int o = 0; o < order; o++) {
//				Integer index = random.nextInt((userBeanListAndVal.getUserBeanList().size()-1));
//				UserBean userBean = userBeanListAndVal.getUserBeanList().get(index);
//				content.add(userBean);
//			}
//			hyperedge.setContent(content);
//			
//			hyperedgeList.set(i, hyperedge);
//		}
		
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
	private void bubbling(List<Hyperedge> list) {
		int remainSize = list.size();
		for(int i=0;i<remainSize;i++) {
			Hyperedge maxBean = list.get(i);
			int maxIndex = 0;
			for(int j=1;j<remainSize;j++) {
				Hyperedge bean2 = list.get(j);
				if(bean2.moreRight(maxBean)) {
					maxIndex = j;
					maxBean = bean2;
				}
			}
			Hyperedge tmp = list.get(remainSize-1);
			list.set(remainSize-1, maxBean);
			list.set(maxIndex, tmp);
			remainSize--;
		}
	}
	
	
	//快速排序
	private void quickSort(List<Hyperedge> list, int low, int high) {
		if (low < high) { 
			int middle = getMiddle(list, low, high);  //将list数组进行一分为二
			quickSort(list, low, middle - 1);        //对低字表进行递归排序
			quickSort(list, middle + 1, high);       //对高字表进行递归排序
		}
	}
	
//	int tmp = list[low];    //数组的第一个作为中轴
//	while (low < high) {
//		while (low < high && list[high] > tmp) {
//			high--;
//		}
//		list[low] = list[high];   //比中轴小的记录移到低端
//		while (low < high && list[low] < tmp) {
//			low++;
//		}
//		list[high] = list[low];   //比中轴大的记录移到高端
//	}
//	list[low] = tmp;              //中轴记录到尾
//	return low;                   //返回中轴的位置
	private int getMiddle(List<Hyperedge> list, int low, int high) {
		Hyperedge tmp = list.get(low);    //数组的第一个作为中轴
		while (low < high) {
			while (low < high && list.get(high).moreRight(tmp)) {
				high--;
			}
			Hyperedge highBean = list.get(high);
			Hyperedge lowBean = list.get(low);
			lowBean = highBean;   //比中轴小的记录移到低端
			while (low < high && tmp.moreRight(list.get(low))) {
				low++;
			}
			Hyperedge highBean1 = list.get(high);
			Hyperedge lowBean1 = list.get(low);
			highBean = lowBean;   //比中轴大的记录移到高端
		}
		Hyperedge lowBean2 = list.get(low);
		lowBean2 = tmp;              //中轴记录到尾
		return low;                   //返回中轴的位置
	}
	
	private double getRightRate(Map<String, UserBeanListAndVal> map) {
		//2.用超边库L对训练样本进行分类
		//对分数统计的map
		int rightNum = 0;
		for(String itemId : map.keySet()) {
			UserBeanListAndVal userBeanListAndVal = map.get(itemId);
			Integer val = userBeanListAndVal.getVal();
			List<UserBean> userBeanList = userBeanListAndVal.getUserBeanList();
			Map<Integer, Integer> scoreCountMap = new HashMap<>();
			//遍历每一条超边
			for(Hyperedge hyperedge : hyperedgeList) {
				//如果是训练样本自己生成的超边，直接忽略
				if(itemId.equals(hyperedge.getItemId()))
					continue;
				//是否匹配
				List<UserBean> hyperedgeContent = hyperedge.getContent();
				if(matching(hyperedgeContent,userBeanList)) {
					Integer score = hyperedge.getVal();
					if(scoreCountMap.containsKey(score)) {
						Integer count = scoreCountMap.get(score);
						count++;
						scoreCountMap.put(score, count);
					}
					else 
						scoreCountMap.put(score, 1);
					//计算适应值
					if(score == val) 
						hyperedge.setrCount(hyperedge.getrCount() + 1);
					else
						hyperedge.setwCount(hyperedge.getwCount() + 1);
				}
			}
			
//			System.out.println(scoreCountMap + "trainVal=====" + val);
			int maxScoreNum = 0;
			int maxScore = -1;
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
//		System.out.println("正确数为==============="+rightNum);
//		System.out.println("正确率============"+rightRate);
		return rightRate;
	}

	//初始化超边库
	private List<Hyperedge> initHyperdgeList() {
		//1.初始化超边库,每个样本产生100条超边
		hyperedgeList = new ArrayList<>();
		for(String itemId : trainMap.keySet()) {
			UserBeanListAndVal userBeanListAndVal = trainMap.get(itemId);
			Integer val = userBeanListAndVal.getVal();
			List<UserBean> userBeanList = userBeanListAndVal.getUserBeanList();
			for(int i = 0;i < hyperedgeCount;i++) {
				Hyperedge hyperedge = new Hyperedge();
				hyperedge.setItemId(itemId);
				hyperedge.setVal(val);
				List<UserBean> content  = new ArrayList<>();
				for(int o = 0; o < order; o++) {
					Integer index = random.nextInt((userBeanList.size()-1));
					UserBean userBean = userBeanList.get(index);
					content.add(userBean);
				}
				hyperedge.setContent(content);
				hyperedgeList.add(hyperedge);
			}
		}
		return hyperedgeList;
	}
	
	//匹配
	private boolean matching(List<UserBean> hyperedgeContent,List<UserBean> trainList) {
		for(UserBean hyperedgeBean : hyperedgeContent) {
			boolean thisHyperedgeMatching = false;
			for(UserBean trainBean : trainList) {
				if(trainBean.equals(hyperedgeBean)) {
					thisHyperedgeMatching = true;
					break;
				}
			}
			if(!thisHyperedgeMatching)
				return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		int size = 50;
		String[] strs = new String[100];
		Random r = new Random();
		for(int i=0;i<100;i++) {
			StringBuilder key = new StringBuilder();
			for(int o=0;o<3;o++) {
				key.append(r.nextInt(size)).append("-");
			}
			strs[i] = key.toString();
		}
		System.out.println(strs);
	}
}
