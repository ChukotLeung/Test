package org.chukot.framework.weixin.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringUtils;
import org.chukot.framework.weixin.model.advanced.Oauth2Token;
import org.chukot.framework.weixin.model.advanced.SNSUserInfo;
import org.chukot.framework.weixin.model.advanced.TemporaryQRCode;
import org.chukot.framework.weixin.model.advanced.UserInfo;
import org.chukot.framework.weixin.model.advanced.UserList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class AdvancedUtil {
	
	private static Logger log = LoggerFactory.getLogger(AdvancedUtil.class);
	
	// 获取access_token
	public final static String oauth2_access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	// 刷新access_token
	public final static String oauth2_refresh_token_url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
	// 通过网页授权获取用户信息
	public final static String oauth2_userinfo_url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	// 创建二维码
	public final static String qrcode_create_url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN";
	// 换取二维码
	public final static String qrcode_show_url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=TICKET";
	// 获取用户信息
	public final static String userinfo_url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	// 获取关注者列表
	public final static String userlist_url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
	
	/**
	 * 获取网页授权凭证
	 * @param appId
	 * @param appSecret
	 * @param code
	 * @return
	 */
	public static Oauth2Token getOauth2AccessToken(String appId, String appSecret, String code) {
		Oauth2Token at = null;
		String requestUrl = oauth2_access_token_url.replace("APPID", appId).replace("SECRET", appSecret).replace("CODE", code);
		// 获取网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("access_token"))) {
				at = new Oauth2Token();
				at.setAccessToken(jsonObject.getString("access_token"));
				at.setExpiresIn(jsonObject.getIntValue("expires_in"));
				at.setRefreshToken(jsonObject.getString("refresh_token"));
				at.setOpenId(jsonObject.getString("openid"));
				at.setScope(jsonObject.getString("scope"));
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return at;
	}
	
	/**
	 * 刷新网页授权凭证
	 * @param appId
	 * @param appSecret
	 * @param code
	 * @return
	 */
	public static Oauth2Token refreshOauth2AccessToken(String appId, String refreshToken) {
		Oauth2Token at = null;
		String requestUrl = oauth2_refresh_token_url.replace("APPID", appId).replace("REFRESH_TOKEN", refreshToken);
		// 刷新网页授权凭证
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("access_token"))) {
				at = new Oauth2Token();
				at.setAccessToken(jsonObject.getString("access_token"));
				at.setExpiresIn(jsonObject.getIntValue("expires_in"));
				at.setRefreshToken(jsonObject.getString("refresh_token"));
				at.setOpenId(jsonObject.getString("openid"));
				at.setScope(jsonObject.getString("scope"));
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("刷新网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return at;
	}
	
	/**
	 * 通过网页授权获取用户信息
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public static SNSUserInfo getSNSUserInfo(String accessToken, String openId) {
		SNSUserInfo snsUserInfo = null;
		String requestUrl = oauth2_userinfo_url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		// 通过网页授权获取用户信息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("openid"))) {
				snsUserInfo = new SNSUserInfo();
				snsUserInfo.setOpenId(jsonObject.getString("openid"));
				snsUserInfo.setNickname(jsonObject.getString("nickname"));
				snsUserInfo.setSex(jsonObject.getIntValue("sex"));
				snsUserInfo.setCountry(jsonObject.getString("country"));
				snsUserInfo.setProvince(jsonObject.getString("province"));
				snsUserInfo.setCity(jsonObject.getString("city"));
				snsUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
				snsUserInfo.setPrivilegeList(JSON.parseArray(jsonObject.getString("privilege"), String.class));
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return snsUserInfo;
	}
	
	/**
	 * 获取用户信息
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public static UserInfo getUserInfo(String accessToken, String openId) {
		UserInfo userInfo = null;
		String requestUrl = userinfo_url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
		// 获取用户信息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("openid"))) {
				userInfo = new UserInfo();
				userInfo.setSubscribe(jsonObject.getIntValue("subscribe"));
				userInfo.setOpenId(jsonObject.getString("openid"));
				userInfo.setNickname(jsonObject.getString("nickname"));
				userInfo.setSex(jsonObject.getIntValue("sex"));
				userInfo.setCountry(jsonObject.getString("country"));
				userInfo.setProvince(jsonObject.getString("province"));
				userInfo.setCity(jsonObject.getString("city"));
				userInfo.setLanguage(jsonObject.getString("language"));
				userInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
				userInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
				userInfo.setUnionId(jsonObject.getString("unionid"));
				
				if (0 == userInfo.getSubscribe()) {
					log.info("用户{}已取消关注", userInfo.getOpenId());
				}
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("获取用户信息失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return userInfo;
	}
	
	/**
	 * 获取关注者列表
	 * @param accessToken
	 * @param nextOpenId
	 * @return
	 */
	public static UserList getUserList(String accessToken, String nextOpenId) {
		UserList userList = null;
		String requestUrl = userlist_url.replace("ACCESS_TOKEN", accessToken).replace("NEXT_OPENID", nextOpenId);
		// 获取关注者列表
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("data"))) {
				userList = new UserList();
				userList.setTotal(jsonObject.getIntValue("total"));
				userList.setCount(jsonObject.getIntValue("count"));
				userList.setNextOpenId(jsonObject.getString("next_openid"));
				JSONObject dataObject = jsonObject.getJSONObject("data");
				userList.setOpenIdList(JSON.parseArray(dataObject.getString("openid"), String.class));
				
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("获取关注者列表失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return userList;
	}
	
	/**
	 * 创建临时带参数二维码
	 * @param accessToken
	 * @param expireSeconds
	 * @param sceneId
	 * @return
	 */
	public static TemporaryQRCode createTemporaryQRCode(String accessToken, int expireSeconds, int sceneId) {
		TemporaryQRCode tQRCode = null;
		String requestUrl = qrcode_create_url.replace("TOKEN", accessToken);
		// 需要提交的JSON数据
		String jsonMsg = "{\"expire_seconds\": %d, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";
		// 创建临时带参数二维码
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonMsg, expireSeconds, sceneId));
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("ticket"))) {
				tQRCode = new TemporaryQRCode();
				tQRCode.setTicket(jsonObject.getString("ticket"));
				tQRCode.setExpireSeconds(jsonObject.getIntValue("expire_seconds"));
				log.info("创建临时带参数二维码成功 ticket:{}", tQRCode.getTicket());
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("创建临时带参数二维码失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return tQRCode;
	}
	
	/**
	 * 创建永久带参数二维码
	 * @param accessToken
	 * @param sceneId
	 * @return
	 */
	public static String createPermanentQRCode(String accessToken, int sceneId) {
		String ticket = null;
		String requestUrl = qrcode_create_url.replace("TOKEN", accessToken);
		// 需要提交的JSON数据
		String jsonMsg = "{\"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": %d}}}";
		// 创建永久带参数二维码
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonMsg, sceneId));
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("ticket"))) {
				ticket = jsonObject.getString("ticket");
				log.info("创建永久带参数二维码成功 ticket:{}", ticket);
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("创建永久带参数二维码失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return ticket;
	}
	
	/**
	 * 根据ticket换取二维码
	 * @param ticket
	 * @param savePath
	 * @return
	 */
	public static String getQRCode(String ticket, String savePath) {
		String filePath = null;
		try {
			String requestUrl = qrcode_show_url.replace("TICKET", URLEncoder.encode(ticket, "UTF-8"));
			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			
			if (!savePath.endsWith("/")) {
				savePath += "/";
			}
			filePath = savePath + ticket + ".jpg";
			
			// 输入流写文件
			BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			
			fos.close();
			bis.close();
			conn.disconnect();
			log.info("根据ticket换取二维码成功，filePath=" + filePath);
			
		} catch (Exception e) {
			filePath = null;
			log.error("根据ticket换取二维码失败: {}", e);
		}
		
		return filePath;
	}
	
}