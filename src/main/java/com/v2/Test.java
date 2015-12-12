package com.v2;

import java.util.Random;

public class Test {

	public static void main(String[] args) {
//		List<UserBean> content  = new ArrayList<>();
//		content.add(new UserBean("1",1));
//		content.add(new UserBean("3",1));
//		content.add(new UserBean("5",3));
//		content.add(new UserBean("4",5));
//		content.add(new UserBean("2",3));
		
//		Set<UserBean> set = new TreeSet<>();
//		set.add(new UserBean("111",1));
//		set.add(new UserBean("33",1));
//		set.add(new UserBean("4",5));
//		set.add(new UserBean("1112",3));
//		for(UserBean u : set) {
//			System.out.println(u.getUserId());
//		}
//		
//		System.out.println(set.toString());
		
//		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
//		map.put(1, 1);
//		map.put(2, 2);
//		map.put(3, 3);
//		map.put(4, 4);
		
//		Iterator<Map.Entry<Integer, Integer>> entries = map.entrySet().iterator();
//		while (entries.hasNext()) {
//		    Map.Entry<Integer, Integer> entry = entries.next();
//		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//		    if(entry.getKey() == 1)
//		    	map.remove(entry.getKey());
//
//		}
		
//		Set<Integer> keySet = new HashSet<>();
//		//遍历map中的键
//		for (Integer key : map.keySet()) {
//		    System.out.println("Key = " + key);
//		    if(key == 1)
//		    	keySet.add(key);
//		}
//		for(Integer key : keySet) {
//			map.remove(key);
//		}
//		for (Integer key : map.keySet()) {
//		    System.out.println("Key = " + key);
//		}
		
//		Map<String, Integer> content = new HashMap<>();
//		content.put("12", 162);
//		content.put("13", 152);
//		content.put("2", 132);
//		content.put("1", 112);
//		long startTime = System.currentTimeMillis();
//		Iterator it = content.entrySet().iterator();    
//	    while (it.hasNext()) {   
//	    	it.next(); 
//	    }
////		for(String key : content.keySet()) {
////			if(!userScoreMap.containsKey(key))
////				return false;
////			if(userScoreMap.get(key) != content.get(key))
////				return false;
////		}
//		long endTime = System.currentTimeMillis();
//		System.out.println(endTime - startTime);
		
        long start = System.currentTimeMillis();
        for (int i = 0; i < 299; i++) {
            for (int j = 0; j < 99999; j++) {
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("2 layer is " + (end - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 299; j++) {
                for (int k = 0; k < 99999; k++) {

                }
            }
        }
        end = System.currentTimeMillis();
        System.out.println("3 layer is " + (end - start) + "ms");
        
        Random random = new Random();
        random.nextInt(0);
	}

}
