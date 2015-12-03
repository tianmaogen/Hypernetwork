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

public class MyFileUtil {

	/**
	 * 复制文件或者目录,复制前后文件完全一样。
	 * 
	 * @param resFilePath
	 *            源文件路径
	 * @param distFolder
	 *            目标文件夹
	 * @IOException 当操作发生异常时抛出
	 */
	public static void copyFile(String resFilePath, String distFolder)
			throws IOException {
		File resFile = new File(resFilePath);
		File distFile = new File(distFolder);
		if (resFile.isDirectory()) { // 目录时
			FileUtils.copyDirectoryToDirectory(resFile, distFile);
		} else if (resFile.isFile()) { // 文件时
			// FileUtils.copyFileToDirectory(resFile, distFile, true);
			FileUtils.copyFileToDirectory(resFile, distFile);
		}
	}

	/**
	 * 删除一个文件或者目录
	 * 
	 * @param targetPath
	 *            文件或者目录路径
	 * @IOException 当操作发生异常时抛出
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
	 * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
	 * 
	 * @param res
	 *            原字符串
	 * @param filePath
	 *            文件路径
	 * @return 成功标记
	 * @throws IOException
	 */
	public static boolean string2File(String res, String filePath)
			throws IOException {
		boolean flag = true;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		try {
			File distFile = new File(filePath);
			if (!distFile.getParentFile().exists()) {// 不存在时创建
				distFile.getParentFile().mkdirs();
			}
			bufferedReader = new BufferedReader(new StringReader(res));
			bufferedWriter = new BufferedWriter(new FileWriter(distFile));
			char buf[] = new char[1024]; // 字符缓冲区
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
	 * 取得指定文件内容
	 * 
	 * @param res
	 *            原字符串
	 * @param filePath
	 *            文件路径
	 * @return 成功标记
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
	 * 给指定文件追加内容
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
	 * 往指定的文件最后添加一行记录
	 * @param filePath 文件路径
	 * @param content 内容
	 */
	public static void writeOneLine(String filePath,String content) {
		List<String> lines = new ArrayList<String>();
		lines.add(content);
		writeLines(filePath, lines);
	}
	
	/**
	 * 往指定的文件最后添加多行记录
	 * @param filePath 文件路径
	 * @param lines 内容
	 */
	public static void writeLines(String filePath,List<String> lines) {
		File distFile = new File(filePath);
		// 不存在时创建
		if (!distFile.getParentFile().exists()) {
			distFile.getParentFile().mkdirs();
		}
		try {
			FileUtils.writeLines(distFile, lines, null , true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		writeOneLine("src//main//resources//11.log", "加加加!");
	}
}
