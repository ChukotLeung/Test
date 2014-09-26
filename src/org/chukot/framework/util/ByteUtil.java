package org.chukot.framework.util;

public class ByteUtil {
	
	/**
	 * 将字节数组转换为十六进制字符串
	 * @param byteArray
	 * @return
	 */
	public static String byteToStr(byte[] byteArray) {
		StringBuffer strDigest = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			strDigest.append(byteToHexStr(byteArray[i]));
		}
		return strDigest.toString();
	}

	/**
	 * 将字节转换为十六进制字符串
	 * @param mByte
	 * @return
	 */
	public static String byteToHexStr(byte mByte) {
		char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = digit[(mByte >>> 4) & 0x0F];
		tempArr[1] = digit[mByte & 0x0F];
		
		return new String(tempArr);
	}
	
}