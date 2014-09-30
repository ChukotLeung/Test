package org.chukot.framework.weixin.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.chukot.framework.service.MyX509TrustManager;
import org.chukot.framework.weixin.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 通用工具类
 * @author chukot
 *
 */
public class CommonUtil {

	private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
	
	// 凭证获取（GET）
	public final static String token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	/**
	 * 发送https请求
	 * @param requestUrl
	 * @param requestMethod
	 * @param outputStr
	 * @return
	 */
	public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		JSONObject jsonObject = null;
		
		try {
			// 创建SSLContext对象，并使用指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod(requestMethod);
			
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			
			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			
			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			
			jsonObject = JSON.parseObject(buffer.toString());
		} catch (ConnectException ce) {
			log.error("连接超时：{}", ce);
		} catch (Exception e) {
			log.error("https请求异常：{}", e);
		}
		
		return jsonObject;
	}
	
	
	public static Token getToken(String appid, String appsecret) {
		Token token = null;
		String requestUrl = token_url.replace("APPID", appid).replace("APPSECRET", appsecret);
		// 发起GET请求获取凭证
		JSONObject jsonObject = httpsRequest(requestUrl, "GET", null);
		
		if (null != jsonObject) {
			token = new Token();
			token.setAccessToken(jsonObject.getString("access_token"));
			token.setExpiresIn(jsonObject.getIntValue("expires_in"));
		}
		
		return token;
	}
	
	public static String getFileExt(String contentType) {
		String fileExt = "";
		if ("image/jpeg".equals(contentType)) {
			fileExt = ".jpg";
		} else if ("audio/mpeg".equals(contentType)) {
			fileExt = ".mp3";
		} else if ("audio/amr".equals(contentType)) {
			fileExt = ".amr";
		} else if ("video/mp4".equals(contentType)) {
			fileExt = ".mp4";
		} else if ("video/mpeg4".equals(contentType)) {
			fileExt = ".mp4";
		}
		return fileExt;
	}
	
	public static String emoji(int codePoint) {
		return String.valueOf(Character.toChars(codePoint));
	}
	
	/**
	 * 判断是否为微信浏览器
	 * @param request
	 * @return
	 */
	public static boolean isMicroMessenger(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		return userAgent.contains("MicroMessenger");
	}
	
}