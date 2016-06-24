package com.lenovo.sdimobileclient.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.foreveross.cache.utility.FileUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;

/**
 * 文件工具类
 * 
 * @author zhangshaofang
 *
 */
public class FileUtil {
	private static final String FILENAME_SEQUENCE_SEPARATOR = "-";
	public static Random sRandom = new Random(SystemClock.uptimeMillis());

	/**
	 * 创建文件
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	static public boolean createFile(File file) throws IOException {
		if (file.exists()) {
			deleteFile(file);
		}
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		return file.createNewFile();
	}

	/**
	 * 删除文件或目录
	 * 
	 * @param file
	 * @return
	 */
	public static final boolean deleteFile(File file) {
		boolean result = true;
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					result &= deleteFile(children[i]);
				}
			}
		}
		result &= file.delete();
		return result;
	}

	public static boolean delete(Uri uri) {
		if (uri != null) {
			String scheme = uri.getScheme();
			if (ContentResolver.SCHEME_FILE.equals(scheme)) {
				return deleteFile(new File(uri.getPath()));
			}
		}
		return false;
	}

	/**
	 * 文件复制
	 * 
	 * @param cr
	 * @param src
	 * @param file
	 * @return
	 */
	public static boolean copy(ContentResolver cr, Uri src, File file) {
		boolean result = false;
		if (src != null && cr != null && file != null) {
			InputStream in = null;
			try {
				file.createNewFile();

				in = cr.openInputStream(src);
				result = FileUtils.copyToFile(in, file);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (Throwable t) {
					// do nothing
				}
			}
		}

		return result;

	}

	private static final String TEMP_DIR_NAME = "temp";
	private static final String TEMP_FILE_NAME = "file.jpg";

	/**
	 * 拷贝工程中产生的临时文件
	 * 
	 * @param context
	 * @return
	 */
	public static File getTempFile(Context context) {
		
		

		String time = Utils.datetimeFormatPre(System.currentTimeMillis());
		File file = new File(getTempDir(context), time);

		return new File(chooseUniqueFilename(file.getAbsolutePath()));
	}

	/**
	 * 获取缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getTempDir(Context context) {
		return context.getDir(TEMP_DIR_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
	}

	/**
	 * 创建唯一文件名
	 * 
	 * @param filename
	 * @return
	 */
	public static String chooseUniqueFilename(String filename) {

		final File file = new File(filename);
		filename = file.getName();
		final int index = filename.lastIndexOf('.');
		final String extension;
		if (index >= 0) {
			extension = filename.substring(index);
		} else {
			extension = "";
		}
		filename = file.getAbsolutePath();
		final String basename = filename.substring(0, filename.length() - extension.length());
		return chooseUniqueFilename(basename, extension);
	}

	private static String chooseUniqueFilename(String filename, String extension) {
		String fullFilename = filename + extension;
		if (!new File(fullFilename).exists()) {
			return fullFilename;
		}
		filename = filename + FILENAME_SEQUENCE_SEPARATOR;
		/*
		 * This number is used to generate partially randomized filenames to
		 * avoid collisions. It starts at 1. The next 9 iterations increment it
		 * by 1 at a time (up to 10). The next 9 iterations increment it by 1 to
		 * 10 (random) at a time. The next 9 iterations increment it by 1 to 100
		 * (random) at a time. ... Up to the point where it increases by
		 * 100000000 at a time. (the maximum value that can be reached is
		 * 1000000000) As soon as a number is reached that generates a filename
		 * that doesn't exist, that filename is used. If the filename coming in
		 * is [base].[ext], the generated filenames are [base]-[sequence].[ext].
		 */
		int sequence = 1;
		for (int magnitude = 1; magnitude < 1000000000; magnitude *= 10) {
			for (int iteration = 0; iteration < 9; ++iteration) {


				fullFilename = filename + sequence + extension;
				if (!new File(fullFilename).exists()) {
					return fullFilename;
				}
				sequence += sRandom.nextInt(magnitude) + 1;
			}
		}
		return null;
	}
}
