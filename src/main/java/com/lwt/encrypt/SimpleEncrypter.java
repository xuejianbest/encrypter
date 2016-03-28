package com.lwt.encrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.apache.log4j.Logger;

public class SimpleEncrypter {
	private final static int HEAD_SIZE = 1024 * 4;
	private final static byte[] KEY = { (byte) 0x52, (byte) 0x61, (byte) 0x72,
			(byte) 0x21, (byte) 0x1a, (byte) 0x07, (byte) 0x00, (byte) 0xce,
			(byte) 0x99, (byte) 0x73, (byte) 0x80, (byte) 0x00, (byte) 0x0d,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0xb7, (byte) 0xf2, (byte) 0xf4,
			(byte) 0xdb, (byte) 0xc7, (byte) 0x44, (byte) 0xd2, (byte) 0x8a,
			(byte) 0xcd, (byte) 0x31, (byte) 0x3d, (byte) 0xfb, (byte) 0x48,
			(byte) 0xaa, (byte) 0x34, (byte) 0xe3, (byte) 0x87, (byte) 0x0b,
			(byte) 0x02, (byte) 0xf4, (byte) 0x01, (byte) 0xfa, (byte) 0x0a,
			(byte) 0x13, (byte) 0xb0, (byte) 0x33, (byte) 0xef, (byte) 0x79,
			(byte) 0x58, (byte) 0x5b, (byte) 0xd5, (byte) 0x00, (byte) 0x0d,
			(byte) 0x6b, (byte) 0xe1, (byte) 0x03, (byte) 0xe8, (byte) 0x60,
			(byte) 0x43, (byte) 0xab, (byte) 0xcc, (byte) 0xd0, (byte) 0x71,
			(byte) 0xb6, (byte) 0xe8, (byte) 0xee, (byte) 0x79, (byte) 0x3d,
			(byte) 0xbd, (byte) 0x8d, (byte) 0xe7, (byte) 0x82, (byte) 0xa7,
			(byte) 0x22, (byte) 0xbf, (byte) 0xff, (byte) 0x0f, (byte) 0xb6,
			(byte) 0x09, (byte) 0x92, (byte) 0x50, (byte) 0x17, (byte) 0x5a,
			(byte) 0x5f, (byte) 0x2e, (byte) 0xcd, (byte) 0x02, (byte) 0x1c,
			(byte) 0x84, (byte) 0xa7, (byte) 0x8c, (byte) 0x51, (byte) 0x83,
			(byte) 0x49, (byte) 0xb5, (byte) 0x5a, (byte) 0xe7, (byte) 0x23,
			(byte) 0x18, (byte) 0x7b, (byte) 0xf5, (byte) 0x0e, (byte) 0x23,
			(byte) 0x44, (byte) 0x18, (byte) 0x5d, (byte) 0x36, (byte) 0x7f,
			(byte) 0xb7, (byte) 0xf2, (byte) 0xf4, (byte) 0xdb, (byte) 0xc7,
			(byte) 0x44, (byte) 0xd2, (byte) 0x8a, (byte) 0x3a, (byte) 0xdd,
			(byte) 0x16, (byte) 0xa5, (byte) 0x83, (byte) 0xc4, (byte) 0x0c,
			(byte) 0x56, (byte) 0x81, (byte) 0x38, (byte) 0xbd, (byte) 0x63,
			(byte) 0xb5, (byte) 0x7b, (byte) 0x1e, (byte) 0xe1 };
	private static final int KEY_LEN = KEY.length;

	private static Logger logger = Logger.getLogger(SimpleEncrypter.class);

	public File encrypt(File src_file) throws IOException {
		logger.info("Method encrypt():");
		if (!src_file.exists() || !src_file.isFile() || !src_file.canRead()
				|| !src_file.canWrite()) {
			logger.info("!src_file.exists() || !src_file.isFile() || !src_file.canRead()|| !src_file.canWrite()");
			return null;
		}
		
		logger.info(String.format("file: %s", src_file.getCanonicalPath()));
		byte[] head = new byte[HEAD_SIZE];
		RandomAccessFile rf_src_file = new RandomAccessFile(src_file, "rw");
		
		//从原文件头部尝试读取HEAD_SIZE字节，实际读取head_len字节
		rf_src_file.seek(0);
		int head_len = rf_src_file.read(head);
		//若加密文件为空文件则直接返回
		if(head_len == -1){
			logger.info("file is empty. return;");
			rf_src_file.close();
			return src_file.getCanonicalFile();
		}
		//对读取到的head进行加密换算
		for (int i = 0; i < head_len; i++) {
			head[i] = (byte) (head[i] + KEY[i % KEY_LEN]);
		}

		long file_len = rf_src_file.length();
		logger.info(String.format("file_len: %d, head_len: %d", file_len, head_len));
		//若head_len>=KEY_LEN,将加密head的前KEY_LEN字节写到文件尾部存储，并将KEY写到这一位置，最后将写入KEY头后的head写入文件头部覆盖原文件头。
		//若head_len<KEY_LEN, 将文件长度扩展到KEY_LEN，并将head的head_len字节全部写入扩展后的文件尾部，然后将KEY写入文件头部。新文件长度为(KEY_LEN+head_len)<2*KEY_LEN
		rf_src_file.seek(Math.max(KEY_LEN, file_len));
		rf_src_file.write(Arrays.copyOfRange(head, 0, Math.min(KEY_LEN, head_len)));
		System.arraycopy(KEY, 0, head, 0, KEY_LEN);
		rf_src_file.seek(0);
		rf_src_file.write(head, 0, Math.max(KEY_LEN, head_len));

		rf_src_file.close();

		File res = new File(src_file.getCanonicalPath());
		logger.info(String.format("new: file_name: %s, file_len: %d", res, res.length()));
		return res;
	}

	public File decrypt(File src_file) throws IOException {
		logger.info("Method decrypt():");
		if (!src_file.exists() || !src_file.isFile() || !src_file.canRead()
				|| !src_file.canWrite()) {
			logger.info("!src_file.exists() || !src_file.isFile() || !src_file.canRead()|| !src_file.canWrite()");
			return null;
		}
		
		logger.info(String.format("file: %s", src_file.getCanonicalPath()));
		if(!isEncrypt(src_file)){
			logger.info(String.format("file: %s is not a encrypted file", src_file.getCanonicalPath()));
			return src_file.getCanonicalFile();
		}
		
		byte[] head = new byte[HEAD_SIZE];
		byte[] tail = new byte[KEY_LEN];
		RandomAccessFile rf_src_file = new RandomAccessFile(src_file, "rw");
		long file_len = src_file.length();
		
		rf_src_file.seek(file_len - Math.min(KEY_LEN, file_len-KEY_LEN));
		int tail_len = rf_src_file.read(tail);
		
		rf_src_file.setLength(file_len - KEY_LEN);
		
		rf_src_file.seek(0);
		int head_len = rf_src_file.read(head);
		
		System.arraycopy(tail, 0, head, 0, tail_len);
		
		for(int i=0; i<head_len; i++){
			head[i] = (byte) (head[i] - KEY[i%KEY_LEN]);
		}
		
		rf_src_file.seek(0);
		rf_src_file.write(head, 0, head_len);
		
		rf_src_file.close();
		
		File res = new File(src_file.getCanonicalPath());
		logger.info(String.format("new: file_name: %s, file_len: %d", res, res.length()));
		return res;
	}
	
	private boolean isEncrypt(File src_file) throws IOException{
		if(src_file.length() <= KEY_LEN){
			return false;
		}
		byte[] head = new byte[KEY_LEN];
		FileInputStream is = new FileInputStream(src_file);
		is.read(head);
		is.close();
		
		return Arrays.equals(head, KEY);
	}
	
//	public File[] listEncryptedFile(File){
//		
//	}
}
