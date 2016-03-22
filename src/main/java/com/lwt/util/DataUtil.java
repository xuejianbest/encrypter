package com.lwt.util;

public class DataUtil {
	
	/* 将一个int转化为长度为4的byte数组，小端字节序方式。
	 * 
	 * */
	public static byte[] int2ByteArr_le(int i){
		byte[] b = new byte[4];
		for (int j = 0; j < 4; j++) {
			b[j] = (byte)i;
			i = i >>> 8;
		}
		return b;
	}
	
	/* 将一个长度为4的byte数组转化为一个int，小端字节序方式。
	 * 
	 * */
	public static int byteArr2Int_le(byte[] b){
		int i = 0;
		for (int j = 0; j < 4; j++) {
			i = i << 8;
			i += (b[3-j] & 0xff);
		}
		return i;
	}
	
	/* 获取start到end的随机数，包括end，不包含end
	 * 
	 * */
	public static int random_int(int start, int end){
		if(!(start < end)){
			throw new ArithmeticException("指定上界必须大于指定下界。");
		}
		return (int)(Math.random()*(end - start)) + start;
	}
	
	
	
}
