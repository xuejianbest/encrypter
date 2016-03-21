package com.lwt.encryp;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
//		Encrypter.creatNewKeyFile(new File("E:\\BaiduYunDownload\\t\\key"));
		
		File key_file = new File("E:\\BaiduYunDownload\\t\\key");
		Encrypter encrypter = new Encrypter(key_file);
		
//		encrypter.encrypt(new File("E:\\BaiduYunDownload\\t\\src.mp4"));
		
		encrypter.decrypt(new File("E:\\BaiduYunDownload\\t\\src.mp4.rar"));
	}

}
