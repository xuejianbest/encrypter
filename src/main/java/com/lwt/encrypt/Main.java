package com.lwt.encrypt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.lwt.util.DataUtil;

public class Main {

	public static void main(String[] args) {
		CommandLineUtil command = new CommandLineUtil(args);

		File[] files = command.getFiles();
//		File[] dirs = command.getDirs();
		File keyFile = command.getKeyFile();

		if (command.isCreate()) {
			try {
				Encrypter.creatNewKeyFile(keyFile);
				System.out.println("key file is created: " + keyFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}

		Encrypter encrypter = new Encrypter(keyFile);

		for (File file : files) {
			try {// 跳过密钥文件
				if (file.getCanonicalPath().equals(keyFile.getCanonicalPath())) {
					System.out
							.println("Skipped the key file: " + keyFile + ".");
					continue;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (command.isEncrypt()) {//加密文件
					File en_file = encrypter.encrypt(file);
					if (command.isEnName()) {
						File file_t = new File(en_file.getParent()
								+ File.separator
								+ DataUtil.random_int(0, Integer.MAX_VALUE)
								+ ".rar");
						if (file_t.exists()) {
							file_t = new File(en_file.getParent()
									+ File.separator
									+ DataUtil.random_int(0, Integer.MAX_VALUE)
									+ ".rar");
						} else {
							en_file.renameTo(file_t);
						}
					}
				}
				else {//解密文件
					encrypter.decrypt(file);
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("\nGo on?(y/n)");
				Scanner in = new Scanner(new BufferedInputStream(System.in));
				String str = in.next();
				if("n".equals(str)){
					System.exit(1);
				}
				in.close();
			}
		}//处理一个文件结束，进入下一个文件的处理
		
		System.out.println("Done!");
	} // main end

}
