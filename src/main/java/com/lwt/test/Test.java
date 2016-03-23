package com.lwt.test;

import java.io.File;
import java.lang.reflect.Field;


public class Test {
	public static void main(String[] args) throws Exception {
		File file = new File("sRc");
		File file2 = new File("./Src");
		
		System.out.println(file.getAbsolutePath());
		System.out.println(file2.getAbsolutePath());
		System.out.println(file2.getCanonicalPath());
		System.out.println(file.getCanonicalPath());
		System.out.println(file.equals(file2));
		System.out.println(file.compareTo(file2));
	}
	
	
}
