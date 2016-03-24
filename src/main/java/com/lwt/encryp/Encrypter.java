package com.lwt.encryp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import com.lwt.util.DataUtil;

public class Encrypter {
	private File keyFile; //密钥文件
	private static final int KEY_DATA_LEN = 2048; //密钥内容字节数
	private static final byte[] RAR_HEAD = {
		(byte)0x52, (byte)0x61, (byte)0x72, (byte)0x21, (byte)0x1a, (byte)0x07, (byte)0x00, (byte)0xce, (byte)0x99, (byte)0x73, 
		(byte)0x80, (byte)0x00, (byte)0x0d, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 
		(byte)0x1c, (byte)0x3a, (byte)0x9a, (byte)0xf5, (byte)0xbd, (byte)0xa6, (byte)0x08, (byte)0x70, (byte)0x78, (byte)0xb6, 
		(byte)0x0c, (byte)0x46, (byte)0x22, (byte)0x05, (byte)0x3f, (byte)0xf0, (byte)0xf9, (byte)0x02, (byte)0x94, (byte)0x3a, 
		(byte)0x88, (byte)0xb8, (byte)0x8c, (byte)0x37, (byte)0x23, (byte)0xaa, (byte)0xb5, (byte)0xe0, (byte)0x4d, (byte)0xc0, 
		(byte)0x83, (byte)0x59, (byte)0x58, (byte)0x05, (byte)0xb7, (byte)0xfe, (byte)0x25, (byte)0x65, (byte)0x07, (byte)0x31, 
		(byte)0xf1, (byte)0xae, (byte)0x0c, (byte)0x8f, (byte)0xea, (byte)0x56, (byte)0x65, (byte)0x5d, (byte)0x5c, (byte)0xb8, 
		(byte)0x28, (byte)0x71, (byte)0xa9, (byte)0x19, (byte)0xb1, (byte)0xfa, (byte)0x97, (byte)0x31, (byte)0xcf, (byte)0x80, 
		(byte)0x86, (byte)0x71, (byte)0x7b, (byte)0xb1, (byte)0xeb, (byte)0xcb, (byte)0x2d, (byte)0x0c, (byte)0x1a, (byte)0x99, 
		(byte)0x86, (byte)0xd8, (byte)0xd2, (byte)0x2a, (byte)0x48, (byte)0xc2, (byte)0x79, (byte)0x43, (byte)0x5b, (byte)0x69, 
		(byte)0x4c, (byte)0x2c, (byte)0x3e, (byte)0xa8, (byte)0x22, (byte)0x38, (byte)0x82, (byte)0xd2, (byte)0x1c, (byte)0x3a, 
		(byte)0x9a, (byte)0xf5, (byte)0xbd, (byte)0xa6, (byte)0x08, (byte)0x70, (byte)0xaa, (byte)0xaa, (byte)0x42, (byte)0x6e, 
		(byte)0x87, (byte)0x4f, (byte)0x7d, (byte)0xe2, (byte)0x16, (byte)0x97, (byte)0xe5, (byte)0xcc, (byte)0x04, (byte)0xcd, 
		(byte)0xb8, (byte)0x51 }; //RAR头
	public static final int FILE_HEAD_LEN = 1024; //替换文件头的长度
	
	public Encrypter(File keyFile){
		this.keyFile = keyFile;
	}
	
	//加密文件方法
	public File encrypt(File src_file) throws IOException {
		RandomAccessFile rf_src_file = new RandomAccessFile(src_file, "rw");
		RandomAccessFile rf_key_file = new RandomAccessFile(keyFile, "rw");
		
		//读取文件头,并截断
		byte[] file_head = new byte[FILE_HEAD_LEN];
		int file_head_len = rf_src_file.read(file_head);
		if(file_head_len == -1){
			rf_src_file.close();
			rf_key_file.close();
			System.out.println("Skipped file: "+ src_file + " ,because the file is empty.");
			return src_file;
		}
		file_head = Arrays.copyOf(file_head, file_head_len);
		
		//读取密钥
		byte[] key_data = new byte[KEY_DATA_LEN];
		rf_key_file.read(key_data);
		
		//获取密钥偏移量
		int file_head_key_off = DataUtil.random_int(0, KEY_DATA_LEN/2 - 1);
		int file_name_key_off = DataUtil.random_int(0, KEY_DATA_LEN/2 - 1);
		//加密文件头
		file_head = bytesAdd(file_head, key_data, file_head_key_off);
		//写入文件头到密钥文件尾部
		long file_head_off = rf_key_file.length();
		rf_key_file.seek(file_head_off);
		rf_key_file.write(file_head);
		
		//读取文件名
		byte[] file_name = src_file.getName().getBytes();
		//加密文件名
		file_name = bytesAdd(file_name, key_data, file_name_key_off);
		//写入文件名到密钥文件尾部
		long file_name_off = rf_key_file.length();
		rf_key_file.seek(file_name_off);
		rf_key_file.write(file_name);
		
		byte[] new_head = Arrays.copyOf(RAR_HEAD, FILE_HEAD_LEN);
		
		byte[] file_head_off_bytes = DataUtil.int2ByteArr_le((int)file_head_off);
		byte[] file_head_len_bytes = DataUtil.int2ByteArr_le((int)file_head_len);
		byte[] file_head_key_off_bytes = DataUtil.int2ByteArr_le((int)file_head_key_off);
		byte[] file_name_off_bytes = DataUtil.int2ByteArr_le((int)file_name_off);
		byte[] file_name_len_bytes = DataUtil.int2ByteArr_le((int)file_name.length);
		byte[] file_name_key_off_bytes = DataUtil.int2ByteArr_le((int)file_name_key_off);
		
		System.arraycopy(file_head_off_bytes, 0, new_head, FILE_HEAD_LEN-24, 4);
		System.arraycopy(file_head_len_bytes, 0, new_head, FILE_HEAD_LEN-20, 4);
		System.arraycopy(file_head_key_off_bytes, 0, new_head, FILE_HEAD_LEN-16, 4);
		System.arraycopy(file_name_off_bytes, 0, new_head, FILE_HEAD_LEN-12, 4);
		System.arraycopy(file_name_len_bytes, 0, new_head, FILE_HEAD_LEN-8, 4);
		System.arraycopy(file_name_key_off_bytes, 0, new_head, FILE_HEAD_LEN-4, 4);
		
		//写入key文件前16字节用于解密校验
		System.arraycopy(key_data, 0, new_head, FILE_HEAD_LEN-24-16, 16);
		
		rf_src_file.seek(0);
		rf_src_file.write(new_head);
		
		rf_src_file.close();
		rf_key_file.close();
		
		//更改文件名
		File res_file = new File(src_file.toString() + ".rar");
		src_file.renameTo(res_file);
		return res_file;
	}
	
	//解密文件方法
	public File decrypt(File src_file) throws IOException {
		RandomAccessFile rf_src_file = new RandomAccessFile(src_file, "rw");
		RandomAccessFile rf_key_file = new RandomAccessFile(keyFile, "rw");
		
		//读取文件头,并解析
		byte[] new_head = new byte[FILE_HEAD_LEN];
		rf_src_file.seek(0);
		rf_src_file.read(new_head);
		
		//读取密钥
		byte[] key_data = new byte[KEY_DATA_LEN];
		rf_key_file.read(key_data);
				
		//校验key文件有效性
		byte[] checksum_head = Arrays.copyOfRange(new_head, FILE_HEAD_LEN-24-16, FILE_HEAD_LEN-24);
		byte[] checksum_key = Arrays.copyOfRange(key_data, 0, 16);
		if(!Arrays.equals(checksum_head, checksum_key)){
			rf_src_file.close();
			rf_key_file.close();
			System.out.println("Skipped file: "+ src_file + " ,because the file is not encrypt with the key file: "+ keyFile + '.');
			return src_file;
		}
		
		//解析文件头
		byte[] file_head_off_bytes = new byte[4];
		byte[] file_head_len_bytes = new byte[4];
		byte[] file_head_key_off_bytes = new byte[4];
		byte[] file_name_off_bytes = new byte[4];
		byte[] file_name_len_bytes = new byte[4];
		byte[] file_name_key_off_bytes = new byte[4];
		System.arraycopy(new_head, FILE_HEAD_LEN-24, file_head_off_bytes, 0, 4);
		System.arraycopy(new_head, FILE_HEAD_LEN-20, file_head_len_bytes, 0, 4);
		System.arraycopy(new_head, FILE_HEAD_LEN-16, file_head_key_off_bytes, 0, 4);
		System.arraycopy(new_head, FILE_HEAD_LEN-12, file_name_off_bytes, 0, 4);
		System.arraycopy(new_head, FILE_HEAD_LEN-8, file_name_len_bytes, 0, 4);
		System.arraycopy(new_head, FILE_HEAD_LEN-4, file_name_key_off_bytes, 0, 4);
		int file_head_off = DataUtil.byteArr2Int_le(file_head_off_bytes);
		int file_head_len = DataUtil.byteArr2Int_le(file_head_len_bytes);
		int file_head_key_off = DataUtil.byteArr2Int_le(file_head_key_off_bytes);
		int file_name_off = DataUtil.byteArr2Int_le(file_name_off_bytes);
		int file_name_len = DataUtil.byteArr2Int_le(file_name_len_bytes);
		int file_name_key_off = DataUtil.byteArr2Int_le(file_name_key_off_bytes);
		
		//读取文件头密文
		byte[] file_head = new byte[file_head_len];
		rf_key_file.seek(file_head_off);
		rf_key_file.read(file_head);
		//解密文件头
		file_head = bytesDec(file_head, key_data, file_head_key_off);
		
		//将文件头写入文件
		rf_src_file.seek(0);
		rf_src_file.write(file_head);
		if(file_head_len < FILE_HEAD_LEN){
			rf_src_file.setLength(file_head_len);
		}
		
		//读取文件名密文
		byte[] file_name = new byte[file_name_len];
		rf_key_file.seek(file_name_off);
		rf_key_file.read(file_name);
		//解密文件名
		file_name = bytesDec(file_name, key_data, file_name_key_off);
		
		rf_src_file.close();
		rf_key_file.close();
		
		//恢复文件名
		String file_path = src_file.getParent() + File.separator;
		File res_file = new File(file_path + new String(file_name));
		src_file.renameTo(res_file);
		return res_file;
	}
			
	//多表替换加密算法
	private byte[] bytesAdd(byte[] src, byte[] table, int off){
		byte[] desc = new byte[src.length];
		for(int i=0; i<src.length; i++){
			desc[i] = (byte)(src[i] + table[off+i]);
		}
		return desc;
	}
	//多表替换解密算法
	private byte[] bytesDec(byte[] src, byte[] table, int off){
		byte[] desc = new byte[src.length];
		for(int i=0; i<src.length; i++){
			desc[i] = (byte)(src[i] - table[off+i]);
		}
		return desc;
	}
	
	
	//生成密钥文件方法
	public static void creatNewKeyFile(File newKeyFile) throws IOException{
		if(newKeyFile.isFile()){
			System.err.println(newKeyFile + " is alread exists.");
		}
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newKeyFile));
		byte[] bytes = new byte[KEY_DATA_LEN];
		for(int i=0; i<KEY_DATA_LEN; i++){
			bytes[i] = (byte)DataUtil.random_int(0, 256);
		}
		writer.write(bytes);
		writer.flush();
		writer.close();
	}
	
}
