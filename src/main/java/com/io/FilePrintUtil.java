package com.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * 工具类：将文件输出到路径为filePath的文件中
 *
 */
public class FilePrintUtil {

	public static String filePath = "src//main//resources//介数为5每个样本产生100条超边.log";
	/**
	 * �����ļ�����Ŀ¼,����ǰ���ļ���ȫһ��
	 * 
	 * @param resFilePath
	 *            Դ�ļ�·��
	 * @param distFolder
	 *            Ŀ���ļ���
	 * @IOException �����������쳣ʱ�׳�
	 */
	public static void copyFile(String resFilePath, String distFolder)
			throws IOException {
		File resFile = new File(resFilePath);
		File distFile = new File(distFolder);
		if (resFile.isDirectory()) { // Ŀ¼ʱ
			FileUtils.copyDirectoryToDirectory(resFile, distFile);
		} else if (resFile.isFile()) { // �ļ�ʱ
			// FileUtils.copyFileToDirectory(resFile, distFile, true);
			FileUtils.copyFileToDirectory(resFile, distFile);
		}
	}

	/**
	 * ɾ��һ���ļ�����Ŀ¼
	 * 
	 * @param targetPath
	 *            �ļ�����Ŀ¼·��
	 * @IOException �����������쳣ʱ�׳�
	 */
	public static void deleteFile(String targetPath) throws IOException {
		File targetFile = new File(targetPath);
		if (targetFile.isDirectory()) {
			FileUtils.deleteDirectory(targetFile);
		} else if (targetFile.isFile()) {
			targetFile.delete();
		}
	}

	/**
	 * ���ַ�д��ָ���ļ�(��ָ���ĸ�·�����ļ��в�����ʱ��������޶�ȥ�������Ա�֤����ɹ���)
	 * 
	 * @param res
	 *            ԭ�ַ�
	 * @param filePath
	 *            �ļ�·��
	 * @return �ɹ����
	 * @throws IOException
	 */
	public static boolean string2File(String res, String filePath)
			throws IOException {
		boolean flag = true;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			File distFile = new File(filePath);
			if (!distFile.getParentFile().exists()) {// ������ʱ����
				distFile.getParentFile().mkdirs();
			}
			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024]; // �ַ����
			int len;
			while ((len = bufferedReader.read(buf)) != -1) {
				bufferedWriter.write(buf, 0, len);
			}
			bufferedWriter.flush();
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			flag = false;
			throw e;
		}
		return flag;
	}

	/**
	 * ȡ��ָ���ļ�����
	 * 
	 * @param res
	 *            ԭ�ַ�
	 * @param filePath
	 *            �ļ�·��
	 * @return �ɹ����
	 * @throws IOException
	 */
	public static List<String> getContentFromFile(String filePath) throws IOException {
		List<String> lists = null;
		try {
			if (!(new File(filePath).exists())) {
				return new ArrayList<String>();
			}
			lists = FileUtils.readLines(new File(filePath),
					Charset.defaultCharset());
		} catch (IOException e) {
			throw e;
		}
		return lists;
	}

	/**
	 * ��ָ���ļ�׷������
	 * 
	 * @param filePath
	 * @param contents
	 */
	public static void addContent(String filePath, List<String> contents)
			throws IOException {
		try {
			FileUtils.writeLines(new File(filePath), contents);
		} catch (IOException e) {
			throw e;
		}
	}
	/**
	 * ��ָ�����ļ�������һ�м�¼
	 * @param filePath �ļ�·��
	 * @param content ����
	 */
	public synchronized static void writeOneLine(String content) {
		List<String> lines = new ArrayList<String>();
		lines.add(content);
		writeLines(lines);
	}
	
	public synchronized static void writeLines(String theFilePath, List<String> lines) {
		File distFile = new File(theFilePath);
		if (!distFile.getParentFile().exists()) {
			distFile.getParentFile().mkdirs();
		}
		try {
			FileUtils.writeLines(distFile, lines, null , true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static void writeLines(List<String> lines) {
		writeLines(filePath, lines);
	}
	
	public static void main(String[] args) throws IOException {
		writeOneLine("大会上电话!");
	}
}
