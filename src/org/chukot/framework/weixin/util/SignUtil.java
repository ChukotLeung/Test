package org.chukot.framework.weixin.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.chukot.framework.util.ByteUtil;

/**
 * 请求校验工具类
 * @author chukot
 *
 */
public class SignUtil {

	// 与开发模式接口配置信息中的Token保持一致
	private static String TOKEN = "weixinChukot";
	
	/**
	 * 校验签名
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean checkSignature(String signature, String timestamp, String nonce) {
		// 对token、timestamp和nonce按字典排序
		String[] paramArr = new String[] { TOKEN, timestamp, nonce };
		Arrays.sort(paramArr);
		
		// 将排序后的结果拼接成一个字符串
		String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);
		
		String ciphertext = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			// 对拼接后的字符串进行sha1加密
			byte[] digest = md.digest(content.getBytes());
			ciphertext = ByteUtil.byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		// 将sha1加密后的字符串与signature进行对比
		return ciphertext != null ? ciphertext.equals(signature.toUpperCase()) : false;
	}
	
}