package com.lwt.encryp;

import java.io.File;

public class Main {
	
	public static void main(String[] args) throws Exception {
		CommandLineUtil command = new CommandLineUtil(args);
		
		File[] files = command.getFiles();
		File[] dirs = command.getDirs();
		File keyFile = command.getKeyFile();
		
		if(command.isCreate()){
			Encrypter.creatNewKeyFile(keyFile);
			System.out.println("key file is created: " + keyFile);
		}
		
		Encrypter encrypter = new Encrypter(keyFile);
		
		for(File file : files){
			if(file.getCanonicalPath().equals(keyFile.getCanonicalPath())){
				System.out.println("Skipped the key file: keyFile");
				continue;
			}
			if(command.isEncrypt()){
				encrypter.encrypt(file);
				System.out.println("Encrypted: " + file);
			}else{
				encrypter.decrypt(file);
				System.out.println("Decrypted: " + file);
			}
		}
		
		System.out.println("Done!");
	} //main end
	
}
