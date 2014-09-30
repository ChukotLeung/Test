package org.chukot.framework.weixin.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.chukot.framework.weixin.model.advanced.Group;
import org.chukot.framework.weixin.model.advanced.Media;
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

	// 创建分组
	public final static String groups_create_url = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=ACCESS_TOKEN";
	// 查询所有分组
	public final static String groups_get_url = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=ACCESS_TOKEN";
	// 查询用户所在分组
	public final static String groups_getid_url = "https://api.weixin.qq.com/cgi-bin/groups/getid?access_token=ACCESS_TOKEN";
	// 移动用户分组
	public final static String groups_member_update_url = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=ACCESS_TOKEN";
	// 修改分组名
	public final static String groups_update_url = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=ACCESS_TOKEN";
	// 下载多媒体文件
	public final static String media_get_url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	// 上传多媒体文件
	public final static String media_upload_url = "http://file.api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
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
	 * 创建分组
	 * @param accessToken
	 * @param groupName
	 * @return
	 */
	public static Group createGroup(String accessToken, String groupName) {
		Group group = null;
		String requestUrl = groups_create_url.replace("ACCESS_TOKEN", accessToken);
		String jsonData = "{\"group\":{\"name\":\"%s\"}}";
		// 创建分组
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, groupName));
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("group"))) {
				group = JSON.parseObject(jsonObject.getString("group"), Group.class);
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("创建分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return group;
	}
	
	/**
	 * 修改分组名
	 * @param accessToken
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public static boolean updateGroup(String accessToken, int groupId, String groupName) {
		boolean result = false;
		String requestUrl = groups_update_url.replace("ACCESS_TOKEN", accessToken);
		String jsonData = "{\"group\":{\"id\":%d,\"name\":\"%s\"}}";
		// 修改分组名
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, groupId, groupName));
		
		if (null != jsonObject) {
			int errorCode = jsonObject.getIntValue("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("修改分组名成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("修改分组名失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return result;
	}
	
	/**
	 * 移动用户分组
	 * @param accessToken
	 * @param openId
	 * @param groupId
	 * @return
	 */
	public static boolean updateMemberGroup(String accessToken, String openId, int groupId) {
		boolean result = false;
		String requestUrl = groups_member_update_url.replace("ACCESS_TOKEN", accessToken);
		String jsonData = "{\"openid\":\"%s\",\"to_groupid\":%d}";
		// 移动用户分组
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, openId, groupId));
		
		if (null != jsonObject) {
			int errorCode = jsonObject.getIntValue("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("移动用户分组成功 errcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("移动用户分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return result;
	}
	
	/**
	 * 查询用户所在分组
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public static int getGroupId(String accessToken, String openId) {
		int groupId = -1;
		String requestUrl = groups_getid_url.replace("ACCESS_TOKEN", accessToken);
		String jsonData = "{\"openid\":\"%s\"}";
		// 查询用户所在分组
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", String.format(jsonData, openId));
		
		if (null != jsonObject) {
			if (null != jsonObject.getInteger("groupid")) {
				groupId = jsonObject.getIntValue("groupid");
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("查询用户所在分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return groupId;
	}
	
	/**
	 * 查询所有分组
	 * @param accessToken
	 * @return
	 */
	public static List<Group> getGroups(String accessToken) {
		List<Group> groupList = null;
		String requestUrl = groups_get_url.replace("ACCESS_TOKEN", accessToken);
		// 查询所有分组
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);
		
		if (null != jsonObject) {
			if (StringUtils.isNotEmpty(jsonObject.getString("groups"))) {
				groupList = JSON.parseArray(jsonObject.getString("groups"), Group.class);
			} else {
				int errorCode = jsonObject.getIntValue("errcode");
				String errorMsg = jsonObject.getString("errmsg");
				log.error("查询所有分组失败 errcode:{} errmsg:{}", errorCode, errorMsg);
			}
		}
		
		return groupList;
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
	
	/**
	 * 上传媒体文件
	 * @param accessToken
	 * @param type
	 * @param mediaFileUrl
	 * @return
	 */
	public static Media uploadMedia(String accessToken, String type, String mediaFileUrl) {
		Media media = null;
		String uploadMediaUrl = media_upload_url.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
		// 定义数据分隔符
		String boundary = "----" + RandomStringUtils.random(15, true, true).toLowerCase();
		
		try {
			URL uploadUrl = new URL(uploadMediaUrl);
			HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
			uploadConn.setDoInput(true);
			uploadConn.setDoOutput(true);
			uploadConn.setRequestMethod("POST");
			uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			// 获取媒体文件上传的输出流（往微信服务器写数据）
			OutputStream outputStream = uploadConn.getOutputStream();
			URL mediaUrl = new URL(mediaFileUrl);
			HttpURLConnection mediaConn = (HttpURLConnection) mediaUrl.openConnection();
			mediaConn.setDoOutput(true);
			mediaConn.setRequestMethod("GET");
			// 从请求头中的获取内容类型
			String contentType = mediaConn.getHeaderField("Content-Type");
			// 根据内容类型判断文件扩展名
			String fileExt = CommonUtil.getFileExt(contentType);
			// 请求体开始
			outputStream.write(("--" + boundary + "\r\n").getBytes());
			outputStream.write(String.format("Content-Disposition: form-data; name=\"media\";filename=\"file%s\"\r\n", fileExt).getBytes());
			outputStream.write(String.format("Content-Type: %s\r\n\r\n", contentType).getBytes());
			// 获取媒体文件的输入流（读取文件）
			BufferedInputStream bis = new BufferedInputStream(mediaConn.getInputStream());
			byte[] buf = new byte[8096];
			int size = 0;
			while ((size = bis.read(buf)) != -1) {
				outputStream.write(buf, 0, size);
			}
			// 请求体结束
			outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
			outputStream.close();
			bis.close();
			mediaConn.disconnect();
			// 获取媒体文件上传的输入流（从微信服务器读数据）
			InputStream inputStream = uploadConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuffer buffer = new StringBuffer();
			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			uploadConn.disconnect();
			
			media = JSON.parseObject(buffer.toString(), Media.class);
		} catch (Exception e) {
			media = null;
			log.error("上传媒体文件失败：{}", e);
		}
		
		return media;
	}
	
	/**
	 * 下载多媒体文件
	 * @param accessToken
	 * @param mediaId
	 * @param savePath
	 * @return
	 */
	public static String getMedia(String accessToken, String mediaId, String savePath) {
		String filePath = null;
		String requestUrl = media_get_url.replace("ACCESS_TOKEN", accessToken).replace("MEDIA_ID", mediaId);
		
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			
			if (!savePath.endsWith("/")) {
				savePath += "/";
			}
			String fileExt = CommonUtil.getFileExt(conn.getHeaderField("Content-Type"));
			filePath = savePath + mediaId + fileExt;
			
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
			log.info("下载多媒体文件成功，filePath=" + filePath);
		} catch (Exception e) {
			filePath = null;
			log.error("下载多媒体文件失败：{}", e);
		}
		
		return filePath;
	}
}