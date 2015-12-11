package com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.io.MyFileUtil;
import com.model.Hyperedge;
import com.model.UserBean;
import com.model.UserBeanListAndVal;

public class Hypernetworks {
	private int maxIterationCount=400;
	private Map<String, UserBeanListAndVal> trainMap;
	private Map<String, UserBeanListAndVal> testMap;
//	private String userId;
	//超边的介数
	private int order=4;
	private int hyperedgeCount=100;
	private Random random = new Random();
//	private double weight = 0.5;
	//超边集合
	private List<Hyperedge> hyperedgeList;
	
	public Hypernetworks(Map<String, UserBeanListAndVal> trainMap, Map<String, UserBeanListAndVal> testMap) {
		super();
		this.trainMap = trainMap;
		this.testMap = testMap;
	}
	
	public Hypernetworks(Map<String, UserBeanListAndVal> trainMap, Map<String, UserBeanListAndVal> testMap, int order) {
		super();
		this.trainMap = trainMap;
		this.testMap = testMap;
		this.order = order;
	}
	
	public Hypernetworks(Map<String, UserBeanListAndVal> trainMap, Map<String, UserBeanListAndVal> testMap, int order, int hyperedgeCount) {
		super();
		this.trainMap = trainMap;
		this.testMap = testMap;
		this.order = order;
		this.hyperedgeCount = hyperedgeCount;
	}

	public double train() {
		initHyperdgeList();
		double rightRate = getRightRate(trainMap);
		
		int iterationCount = maxIterationCount;
		while(rightRate < 0.95 && iterationCount > 0) {
			//替换超边
			replaceHyperedge(hyperedgeList);
			rightRate = getRightRate(trainMap);
			
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
 
	private void replaceHyperedge(List<Hyperedge> hyperedgeList) {
		//随机选取的item可以产生的超边数量
		int maxRandomCount = (int) (this.hyperedgeCount * 0.6);
		int randomCount = 0;
		String itemId = null;
		for(int i=0; i<hyperedgeList.size(); i++) {
			Hyperedge hyperedge = hyperedgeList.get(i);
			if(hyperedge.getrCount() - hyperedge.getwCount() < 2) {
				//随机取一个item,该item可以产生maxRandomCount条超边
				if(randomCount > 0)
					randomCount--;
				else {
					itemId = getRandomItemId();
					randomCount = maxRandomCount;
				}
					
				UserBeanListAndVal userBeanListAndVal = trainMap.get(itemId);
				hyperedge.setItemId(itemId);
				hyperedge.setVal(userBeanListAndVal.getVal());
				List<UserBean> content  = new ArrayList<>();
				for(int o = 0; o < order; o++) {
					int index = random.nextInt(userBeanListAndVal.getUserBeanList().size());
					UserBean userBean = userBeanListAndVal.getUserBeanList().get(index);
					content.add(userBean);
				}
				hyperedge.setContent(content);
			}
			hyperedge.setwCount(0);
			hyperedge.setrCount(0);
		}
		
		
//		//������ȷ�ʸߵ���������λ��
//		bubbling(hyperedgeList);
//		//�滻ǰ hyperedgeCount * 10��
//		int replaceHyperedgeCount = (int) ((1.0 - rightRate) * hyperedgeList.size() * weight);
//		for(int i = 0;i < replaceHyperedgeCount;i++) {
//			//���ѡ��һ��ѵ����
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
	
	
	//��������
	private void quickSort(List<Hyperedge> list, int low, int high) {
		if (low < high) { 
			int middle = getMiddle(list, low, high);  //��list�������һ��Ϊ��
			quickSort(list, low, middle - 1);        //�Ե��ֱ���еݹ�����
			quickSort(list, middle + 1, high);       //�Ը��ֱ���еݹ�����
		}
	}
	
//	int tmp = list[low];    //����ĵ�һ����Ϊ����
//	while (low < high) {
//		while (low < high && list[high] > tmp) {
//			high--;
//		}
//		list[low] = list[high];   //������С�ļ�¼�Ƶ��Ͷ�
//		while (low < high && list[low] < tmp) {
//			low++;
//		}
//		list[high] = list[low];   //�������ļ�¼�Ƶ��߶�
//	}
//	list[low] = tmp;              //�����¼��β
//	return low;                   //���������λ��
	private int getMiddle(List<Hyperedge> list, int low, int high) {
		Hyperedge tmp = list.get(low);    //����ĵ�һ����Ϊ����
		while (low < high) {
			while (low < high && list.get(high).moreRight(tmp)) {
				high--;
			}
			Hyperedge highBean = list.get(high);
			Hyperedge lowBean = list.get(low);
			lowBean = highBean;   //������С�ļ�¼�Ƶ��Ͷ�
			while (low < high && tmp.moreRight(list.get(low))) {
				low++;
			}
			Hyperedge highBean1 = list.get(high);
			Hyperedge lowBean1 = list.get(low);
			highBean = lowBean;   //�������ļ�¼�Ƶ��߶�
		}
		Hyperedge lowBean2 = list.get(low);
		lowBean2 = tmp;              //�����¼��β
		return low;                   //���������λ��
	}
	
	private double getRightRate(Map<String, UserBeanListAndVal> map) {
		
		int rightNum = 0;
		
		for(String itemId : map.keySet()) {
			UserBeanListAndVal userBeanListAndVal = map.get(itemId);
			Integer val = userBeanListAndVal.getVal();
			List<UserBean> userBeanList = userBeanListAndVal.getUserBeanList();
			Map<Integer, Integer> scoreCountMap = new HashMap<>();
			for(Hyperedge hyperedge : hyperedgeList) {
				if(itemId.equals(hyperedge.getItemId()))
					continue;
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
					if(score == val) 
						hyperedge.setrCount(hyperedge.getrCount() + 1);
					else
						hyperedge.setwCount(hyperedge.getwCount() + 1);
				}
			}
			
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
		return rightRate;
	}
	
	private double getRMSE(Map<String, UserBeanListAndVal> map) {
		
		int rightVal = 0;
		
		for(String itemId : map.keySet()) {
			UserBeanListAndVal userBeanListAndVal = map.get(itemId);
			Integer val = userBeanListAndVal.getVal();
			List<UserBean> userBeanList = userBeanListAndVal.getUserBeanList();
			Map<Integer, Integer> scoreCountMap = new HashMap<>();
			for(Hyperedge hyperedge : hyperedgeList) {
				if(itemId.equals(hyperedge.getItemId()))
					continue;
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
					if(score == val) 
						hyperedge.setrCount(hyperedge.getrCount() + 1);
					else
						hyperedge.setwCount(hyperedge.getwCount() + 1);
				}
			}
			
			int maxScoreNum = 0;
			int maxScore = -1;
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
	private List<Hyperedge> initHyperdgeList() {
		//1.��ʼ�����߿�,ÿ�������100������
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
