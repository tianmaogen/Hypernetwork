package com.db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 将u.data中的数据写入到数据库
 */
public class DBUtils {
	private static final int COMMIT_SIZE = 500;
	public static void main(String[] args) {
		String sourcePath = "src//main//resources//u.data";
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String str = "";
			fis = new FileInputStream(sourcePath);// FileInputStream
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			List<DBBean> list = new ArrayList<>();
			while ((str = br.readLine()) != null) {
				String[] strs = str.split("	");
				Integer userId = Integer.parseInt(strs[0]);
				Integer itemId = Integer.parseInt(strs[1]);
				Integer score = Integer.parseInt(strs[2]);
				list.add(new DBBean(userId, itemId, score));
			}
			insertDB(list);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void insertDB(List<DBBean> list) {
		Connection conn = null;
		String sql = "insert into usermovie (userId,itemId,score) values(?,?,?)";
		PreparedStatement pstmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/movielens?user=root&password=root");
			conn.setAutoCommit(false);
			
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			int i=0;
			for(DBBean bean : list) {
				pstmt.setInt(1, bean.getUserId());
				pstmt.setInt(2, bean.getItemId());
				pstmt.setInt(3, bean.getScore());
				pstmt.addBatch();
//				pstmt.execute();
				
				if (i % COMMIT_SIZE == 0) {
					pstmt.executeBatch();
					conn.commit();
				}
				i++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.executeBatch();
				conn.commit();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
class DBBean {
	
	private Integer userId;
	private Integer itemId;
	private Integer score;
	
	public DBBean(Integer userId, Integer itemId, Integer score) {
		this.userId = userId;
		this.itemId = itemId;
		this.score = score;
	}
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	
}