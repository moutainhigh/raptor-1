package com.mo9.raptor.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BytesHexTransform {
	private static final Logger logger = LoggerFactory.getLogger(BytesHexTransform.class);
	
	private BytesHexTransform(){		
	}
	
	/**
	 * 把字节数组转换成16进制字符串
	 * @param bArray 传入的二进制数组
	 * @return 16进制的字符串
	 */
	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	
	/** 
	 * 十六进制字符串转化为二进制  
	 * @param hex 十六进制字符串
	 * @return 	     二进制数据
	 */  
	public static byte[] hexToByte(String hex) {  
		byte[] ret = new byte[hex.length()/2];  
		byte[] tmp = hex.getBytes();  
		for (int i = 0; i < hex.length()/2; i++) {  
			ret[i] = unitBytes(tmp[i * 2], tmp[i * 2 + 1]);  
		}  
		return ret;  
	} 
	
	/** 
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF 
	 * @param src0 byte 
	 * @param src1 byte 
	 * @return byte 
	 */  
	public static byte unitBytes(byte src0, byte src1) {  
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();  
		_b0 = (byte) (_b0 << 4);  
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();  
		byte ret = (byte) (_b0 ^ _b1);  
		return ret;  
	}  
}
