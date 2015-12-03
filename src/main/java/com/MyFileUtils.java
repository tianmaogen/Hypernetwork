package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.ItemBean;
import com.model.UserBean;

public class MyFileUtils {
	
	/**
	 * 获取 map<itemId,该item对于的userList>
	 * @param filePath 文件路径
	 * @return
	 */
	public static Map<String, List<UserBean>> getUserMap(String filePath) {
		
		Map<String,List<UserBean>> map = new HashMap<>();
		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			fis = new FileInputStream(filePath);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("	");
//				System.out.println("userId===" + strs[0] + "itemId========" + strs[1] + "score=======" + strs[2]);
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
//			System.out.println("newMapSize==============="+newMap.keySet().size());
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return map;
	}
	
	public static void printToFile(String str) {
		System.out.println(str);
	}
	/**
	 * 获取 map<userId,该用户对于的itemList>
	 * @param filePath 文件路径
	 * @return
	 */
	public static Map<String, List<ItemBean>> getItemMap(String filePath) {
		Map<String, List<ItemBean>> map = new HashMap<>();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			fis = new FileInputStream(filePath);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("	");
				String userId = strs[0];
				String itemId = strs[1];
				Integer score = Integer.parseInt(strs[2]);
				ItemBean itemBean = new ItemBean(itemId, score);
				if (map.containsKey(userId)) {
					List<ItemBean> userBeanList = map.get(userId);
					userBeanList.add(itemBean);
				} else {
					List<ItemBean> userBeanList = new ArrayList<>();
					userBeanList.add(itemBean);
					map.put(userId, userBeanList);
				}
			}

		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return map;
	}
}
