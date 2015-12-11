package com.v1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.v1.model.ItemBean;
import com.v1.model.UserBean;

public class MyFileUtils {
	
	/**
	 * @param filePath 文件路径
	 * @return  map<itemId,该item对应的userList集合>
	 */
	public static Map<String, List<UserBean>> getUserMap(String filePath) {
		
		Map<String,List<UserBean>> map = new HashMap<>();
		
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; 
		try {
			String str = "";
			fis = new FileInputStream(filePath);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
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
			System.out.println("�Ҳ���ָ���ļ�");
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ��");
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
	 * ��ȡ map<userId,���û����ڵ�itemList>
	 * @param filePath �ļ�·��
	 * @return
	 */
	public static Map<String, List<ItemBean>> getItemMap(String filePath) {
		Map<String, List<ItemBean>> map = new HashMap<>();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // ���ڰ�װInputStreamReader,��ߴ������ܡ���ΪBufferedReader�л���ģ���InputStreamReaderû�С�
		try {
			String str = "";
			fis = new FileInputStream(filePath);// FileInputStream
			// ���ļ�ϵͳ�е�ĳ���ļ��л�ȡ�ֽ�
			isr = new InputStreamReader(fis);// InputStreamReader ���ֽ���ͨ���ַ���������,
			br = new BufferedReader(isr);// ���ַ��������ж�ȡ�ļ��е�����,��װ��һ��new
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
			System.out.println("�Ҳ���ָ���ļ�");
		} catch (IOException e) {
			System.out.println("��ȡ�ļ�ʧ��");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// �رյ�ʱ����ð����Ⱥ�˳��ر���󿪵��ȹر������ȹ�s,�ٹ�n,����m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return map;
	}
}
