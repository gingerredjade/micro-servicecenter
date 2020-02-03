package com.micro.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件上传工具类
 *
 * @since 1.0.0 2019年11月18日
 * @author <a href="https://126.com">Hongyu Jiang</a>
 */
public class UploadFileUtils {

	private static Logger _logger = LoggerFactory.getLogger(UploadFileUtils.class);

	/**
	 * 获取上传图片的文件夹
	 *
	 * @param filePath 文件目标存储路径
	 * @return 返回值File
	 */
	public static File getUploadDirFile(String filePath) {
		File targetFileDir = new File(filePath);
		if (!targetFileDir.exists()) {
			// 递归生成文件夹
			targetFileDir.mkdirs();
		}
		return targetFileDir;
	}

	/**
	 * 上传指定文件
	 *
	 * @param jarFile MultipartFile
	 * @param destPath 目标存储路径
	 * @return 返回值 文件存储的绝对路径
	 * @throws IOException 异常
	 */
	public static String uploadFile(MultipartFile jarFile, String destPath) throws IOException {

		// 获取文件名
		String filename = jarFile.getOriginalFilename();

		// 目标存储路径
		File destFileDir = UploadFileUtils.getUploadDirFile(destPath);
		String targetPath = destFileDir.getAbsolutePath() + File.separator + filename;

		File newFile = new File(targetPath);

		// 上传文件到绝对路径
		jarFile.transferTo(newFile);
		return targetPath;
	}

	/**
	 * 上传指定文件
	 *
	 * @param file 文件二进制流
	 * @param filePath 目标存储路径
	 * @param fileName 目标存储文件名
	 * @return 返回值 文件存储的绝对路径
	 * @throws IOException 异常
	 */
	public static String uploadFile(byte[] file, String filePath, String fileName)
		throws IOException {

		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}

		String targetPath = filePath + fileName;
		FileOutputStream outputStream = new FileOutputStream(targetPath);
		outputStream.write(file);
		outputStream.flush();
		outputStream.close();

		return targetPath;
	}


}
