package com.lwt.test;

import java.io.File;

import com.lwt.encrypt.FileNameEncrypter;

public class Test {
	
	public static void main(String[] args) throws Exception{
		File ofile = new File("e:/encrypt/1a");
		
		FileNameEncrypter.encrypt(ofile);
		
		
		System.out.println(ofile.length());
	}
}
