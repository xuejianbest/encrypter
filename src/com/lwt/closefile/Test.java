package com.lwt.closefile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

public class Test {
	public static void main(String[] args) throws IOException {
		File f = new File("E:\\BaiduYunDownload\\t\\key");
		
		System.out.println(f.getName());
		System.out.println(f.getParent());
		System.out.println(f.getParentFile().toString());
		System.out.println(File.separator);
		System.out.println(File.pathSeparator);
	}
	
}

