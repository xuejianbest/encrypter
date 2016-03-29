package com.lwt.encrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.lwt.util.DataUtil;

public class FileNameEncrypter {
	private static final byte[] ID = { (byte) 0, (byte) 1, (byte) 1, (byte) 2,
			(byte) 3, (byte) 5, (byte) 8, (byte) 13, (byte) 21, (byte) 34,
			(byte) 55, (byte) 89, (byte) 144, (byte) 233, (byte) 377,
			(byte) 610 };
	private static Logger logger = Logger.getLogger(FileNameEncrypter.class);

	/** 加密文件名
	 *  原理为将文件名取反写入文件尾部，并写入4字节表示的文件名长度，最后写入ID标识
	 * 
	 * */
	public static void encrypt(File ofileSrc) throws IOException {
		//检查文件是否为普通文件
		if(!ofileSrc.isFile()){
			logger.error(ofileSrc + " 不存在或者是目录");
			throw new IllegalArgumentException(ofileSrc + " 不存在或者是目录");
		}
		
		byte[] abyFileName = ofileSrc.getName().getBytes();
		for(int i=0; i<abyFileName.length; i++){
			abyFileName[i] = (byte)~abyFileName[i];
		}
		byte[] abyFileNameLen = DataUtil.int2ByteArr_le(abyFileName.length);

		RandomAccessFile orafSrc;
		try {
			orafSrc = new RandomAccessFile(ofileSrc, "rw");
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
			e.printStackTrace();
			return;
		}
		
		long lSrcLength = ofileSrc.length();
		orafSrc.seek(lSrcLength);
		byte[] abyFileTail = DataUtil.combinationArray(abyFileName, abyFileNameLen, ID);
		orafSrc.write(abyFileTail);
		orafSrc.close();

		String szSrcPath = ofileSrc.getParent() + File.separator;
		File ofileRandomName = new File(szSrcPath + DataUtil.random_int(0, Integer.MAX_VALUE) + ".rar");
		if (ofileRandomName.getAbsoluteFile().exists()) {
			ofileRandomName = new File(szSrcPath + DataUtil.random_int(0, Integer.MAX_VALUE) + ".rar");
		} else {
			ofileSrc.renameTo(ofileRandomName);
		}
	}

	public static void decrypt(File ofileSrc) throws IOException {
		//检查文件是否为普通文件
		if(!ofileSrc.isFile()){
			logger.error(ofileSrc + " 不存在或者是目录");
			throw new IllegalArgumentException(ofileSrc + " 不存在或者是目录");
		}
				
		if (!isEncrypt(ofileSrc)) {
			logger.error(ofileSrc + " 文件名并未进行加密");
			return;
		}
		
		long lSrcLen = ofileSrc.length();
		int iFileNameLenByteNum = 4; //存储文件名长度的字节数，4字节
		
		RandomAccessFile orafSrc;
		try {
			orafSrc = new RandomAccessFile(ofileSrc, "rw");
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
			e.printStackTrace();
			return;
		}
		
		orafSrc.seek(lSrcLen - ID.length - iFileNameLenByteNum);
		byte[] abyFileNameLen = new byte[iFileNameLenByteNum];
		orafSrc.read(abyFileNameLen);

		int iFileNameLen = DataUtil.byteArr2Int_le(abyFileNameLen);

		orafSrc.seek(lSrcLen - ID.length - iFileNameLenByteNum - iFileNameLen);
		byte[] abyFileName = new byte[iFileNameLen];
		orafSrc.read(abyFileName);

		orafSrc.setLength(lSrcLen - ID.length - iFileNameLenByteNum - iFileNameLen);
		orafSrc.close();

		for(int i=0; i<abyFileName.length; i++){
			abyFileName[i] = (byte)~abyFileName[i];
		}
		String szFileName = new String(abyFileName);
		ofileSrc.renameTo(new File(ofileSrc.getParent() + File.separator + szFileName));
	}

	public static boolean isEncrypt(File ofileSrc) throws IOException{
		//检查文件是否为普通文件
		if(!ofileSrc.isFile()){
			logger.error(ofileSrc + " 不存在或者是目录");
			throw new IllegalArgumentException(ofileSrc + " 不存在或者是目录");
		}
		long lSrcLen = ofileSrc.length();
		if(lSrcLen < 20){
			return false;
		}
		
		RandomAccessFile orafSrc;
		try {
			orafSrc = new RandomAccessFile(ofileSrc, "rw");
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
			e.printStackTrace();
			return false;
		}
		
		orafSrc.seek(lSrcLen - ID.length);
		byte[] tail = new byte[ID.length];
		orafSrc.read(tail);
		try{
			orafSrc.close();
		}catch(IOException e){
			logger.error(e.toString());
			e.printStackTrace();
		}
		return Arrays.equals(ID, tail);
	}

}
