package com.lwt.encryp;

import java.io.File;

import com.lwt.util.DataUtil;

public class Main {
	
	public static void main(String[] args) throws Exception {
		CommandLineUtil command = new CommandLineUtil(args);
		
		File[] files = command.getFiles();
		File[] dirs = command.getDirs();
		File keyFile = command.getKeyFile();
		
		if(command.isCreate()){
			Encrypter.creatNewKeyFile(keyFile);
			System.out.println("key file is created: " + keyFile);
			System.exit(0);
		}
		
		Encrypter encrypter = new Encrypter(keyFile);
		
		for(File file : files){
			if(file.getCanonicalPath().equals(keyFile.getCanonicalPath())){
				System.out.println("Skipped the key file: " + keyFile + ".");
				continue;
			}
			if(command.isEncrypt()){
				File en_file = encrypter.encrypt(file);
				if(command.isEnName()){
					File file_t = new File(en_file.getParent() + DataUtil.random_int(0, Integer.MAX_VALUE) + ".rar");
					if(file_t.exists()){
						file_t = new File(en_file.getParent() + DataUtil.random_int(0, Integer.MAX_VALUE) + ".rar");
					}else{
						en_file.renameTo(file_t);
					}
				}
				System.out.println("Encrypted: " + file);
			}else{
				File de_file = encrypter.decrypt(file);
				if(!de_file.equals(file)){
					System.out.println("Decrypted: " + file);
				}
			}
		}
		
		System.out.println("Done!");
	} //main end
	
}
