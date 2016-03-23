package com.lwt.encryp;

import java.io.File;


public class Main {
	
	public static void main(String[] args) throws Exception {
//		String argument = "-kf e:/BaiduYunDownload/out_f.txt e:/BaiduYunDownload/out_d.txt e:/BaiduYunDownload/out_d.txt";
//		CommandLineUtil command = new CommandLineUtil(argument.split("\\s+"));
		
		CommandLineUtil command = new CommandLineUtil(args);
		File[] files = command.getFiles();
		File[] dirs = command.getDirs();
		
//		System.setOut(new PrintStream(new File("e:/out_f.txt")));
		for(File f : files){
			System.out.println("file: " + f);
		}
		
//		System.setOut(new PrintStream(new File("e:/out_d.txt")));
		for(File d : dirs){
			System.out.println("dir: " + d);
		}
		
		File keyFile = command.getKeyFile();
//		System.out.println("key file: " + keyFile);
		Encrypter encrypter = new Encrypter(keyFile);
		for(File file : files){
			if(command.isEncrypt()){
				encrypter.encrypt(file);
			}else{
				encrypter.decrypt(file);
			}
		}
		
	} //main end
	
}
