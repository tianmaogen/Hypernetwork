package com.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.v1.model.UserBean;
import com.v2.UserBeanSet;

public class MatchingFileUtils {
	
	/**
	 * @param filePath 文件路径
	 * @return  map<itemId,该item对应的userList集合>
	 */
	public static Map<String, List<UserBean>> getItemUserListMap(String filePath) {
		
		Map<String,List<UserBean>> map = new HashMap<>();
		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; 
		try {
			String str = "";
			fis = new FileInputStream(filePath);// FileInputStream
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("	");
				String userId = strs[0];
				String itemId = strs[1];
				Integer score = Integer.parseInt(strs[2]);
				UserBean userBean = new UserBean(userId, score);
				if (map.containsKey(itemId)) {
					List<UserBean> userBeanList = map.get(itemId);
					userBeanList.add(userBean);
				} else {
					List<UserBean> userBeanList = new ArrayList<UserBean>();
					userBeanList.add(userBean);
					map.put(itemId, userBeanList);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("文件路径不存在!");
		} catch (IOException e) {
			System.out.println("文件读取异常。");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	
	public synchronized static void printToFile(String str) {
		System.out.println(str);
	}
	/**
	 * 获取测试集中要预测的userId List
	 * @param testFilePath 测试集合的文件路径
	 * @return
	 */
	public static Set<String> getTestUserIdList(String testFilePath) {
		Set<String> set = new HashSet<>();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; 
		try {
			String str = "";
			fis = new FileInputStream(testFilePath);// FileInputStream
			isr = new InputStreamReader(fis);// InputStreamReader
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("	");
				String userId = strs[0];
				set.add(userId);
			}

		} catch (FileNotFoundException e) {
			System.out.println("文件路径不存在!");
		} catch (IOException e) {
			System.out.println("文件读取异常！");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return set;
	}
	
	/**
	 * 获取未通过超网络模型预测的userId中的均方根误差分子，用平均分预测
	 * @param avgScore 平均分
	 * @param excludeUserIds 未通过超网络模型预测的userID集合
	 * @param testFilePath 测试集合的文件路径
	 * @return
	 */
	public static double getExcludeUserIdsNumerator(double avgScore,Set<String> excludeUserIds,String testFilePath) {
		double excludeUserIdsNumerator = 0.0;
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; 
		try {
			String str = "";
			fis = new FileInputStream(testFilePath);// FileInputStream
			isr = new InputStreamReader(fis);// InputStreamReader
			br = new BufferedReader(isr);
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("	");
				String userId = strs[0];
				if(excludeUserIds.contains(userId)) {
					Integer score = Integer.parseInt(strs[2]);
					double numerator = (avgScore - score) * (avgScore - score);
					excludeUserIdsNumerator += numerator;
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("文件路径不存在!");
		} catch (IOException e) {
			System.out.println("文件读取异常！");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return excludeUserIdsNumerator;
	}
}