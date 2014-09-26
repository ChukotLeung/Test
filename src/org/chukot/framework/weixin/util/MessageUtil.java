package org.chukot.framework.weixin.util;

import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.chukot.framework.weixin.model.message.ImageResponseMessage;
import org.chukot.framework.weixin.model.message.MusicResponseMessage;
import org.chukot.framework.weixin.model.message.MusicResponseMessage.Music;
import org.chukot.framework.weixin.model.message.NewsResponseMessage;
import org.chukot.framework.weixin.model.message.NewsResponseMessage.Article;
import org.chukot.framework.weixin.model.message.TextResponseMessage;
import org.chukot.framework.weixin.model.message.VideoResponseMessage;
import org.chukot.framework.weixin.model.message.VoiceResponseMessage;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * 消息处理工具类
 * @author chukot
 *
 */
public class MessageUtil {
	
	private static Logger log = LoggerFactory.getLogger(MessageUtil.class);
	
	// 发送客服消息
	public final static String custom_send_url = "https://api.weixin.qq.com/cgi-bin/custom/send?access_token=ACCESS_TOKEN";

	// 请求消息类型：文本
	public static final String REQ_MESSAGE_TYPE_TEXT = "text";
	// 请求消息类型：图片
	public static final String REQ_MESSAGE_TYPE_IMAGE = "image";
	// 请求消息类型：语音
	public static final String REQ_MESSAGE_TYPE_VOICE = "voice";
	// 请求消息类型：视频
	public static final String REQ_MESSAGE_TYPE_VIDEO = "video";
	// 请求消息类型：地理位置
	public static final String REQ_MESSAGE_TYPE_LOCATION = "location";
	// 请求消息类型：链接
	public static final String REQ_MESSAGE_TYPE_LINK = "link";
	// 请求消息类型：事件推送
	public static final String REQ_MESSAGE_TYPE_EVENT = "event";
	
	// 事件类型：订阅
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	// 事件类型：取消订阅
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
	// 事件类型：关注用户扫描带参数二维码
	public static final String EVENT_TYPE_SCAN = "SCAN";
	// 事件类型：上报地理位置
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	// 事件类型：自定义菜单 - 点击菜单拉取消息时的事件推送
	public static final String EVENT_TYPE_CLICK = "CLICK";
	// 事件类型：自定义菜单 - 点击菜单跳转链接时的事件推送
	public static final String EVENT_TYPE_VIEW = "VIEW";
	
	// 响应消息类型：文本
	public static final String RESP_MESSAGE_TYPE_TEXT = "text";
	// 响应消息类型：图片
	public static final String RESP_MESSAGE_TYPE_IMAGE = "image";
	// 响应消息类型：语音
	public static final String RESP_MESSAGE_TYPE_VOICE = "voice";
	// 响应消息类型：视频
	public static final String RESP_MESSAGE_TYPE_VIDEO = "video";
	// 响应消息类型：音乐
	public static final String RESP_MESSAGE_TYPE_MUSIC = "music";
	// 响应消息类型：图文
	public static final String RESP_MESSAGE_TYPE_NEWS = "news";

	/**
	 * 解析微信发来的请求（XML)
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> parseXml(HttpServletRequest request) throws Exception {
		// 将解析结果存储在HashMap中
		Map<String, String> map = new HashMap<String, String>();
		// 从request中取得输入流
		InputStream inputStream = request.getInputStream();
		// 读取输入流
		SAXReader reader = new SAXReader();
		Document document = reader.read(inputStream);
		// 得到XML根元素
		Element root = document.getRootElement();
		// 得到根元素的所有子节点
		List<Element> elementList = root.elements();
		// 遍历所有子节点
		for (Element e : elementList) {
			map.put(e.getName(), e.getText());
		}
		// 释放资源
		inputStream.close();
		inputStream = null;
		
		return map;
	}
	
	/**
	 * 扩展xstream使其支持CDATA
	 */
	private static XStream xstream = new XStream(new XppDriver() {
		public HierarchicalStreamWriter createWriter(Writer out) {
			return new PrettyPrintWriter(out) {
				// 对所有XML节点的转换都增加CDATA标记
				boolean cdata = true;
				
				@SuppressWarnings("rawtypes")
				public void startNode(String name, Class clazz) {
					super.startNode(name, clazz);
				}
				
				protected void writeText(QuickWriter writer, String text) {
					if (cdata) {
						writer.write("<![CDATA[");
						writer.write(text);
						writer.write("]]>");
					} else {
						writer.write(text);
					}
				}
			};
		}
	});
	
	/**
	 * 文本消息对象转换成XML
	 * @param textResponseMessage
	 * @return
	 */
	public static String messageToXml(TextResponseMessage textResponseMessage) {
		xstream.alias("xml", textResponseMessage.getClass());
		return xstream.toXML(textResponseMessage);
	}
	
	/**
	 * 图片消息对象转换成XML
	 * @param imageResponseMessage
	 * @return
	 */
	public static String messageToXml(ImageResponseMessage imageResponseMessage) {
		xstream.alias("xml", imageResponseMessage.getClass());
		return xstream.toXML(imageResponseMessage);
	}
	
	/**
	 * 语音消息对象转换成XML
	 * @param voiceResponseMessage
	 * @return
	 */
	public static String messageToXml(VoiceResponseMessage voiceResponseMessage) {
		xstream.alias("xml", voiceResponseMessage.getClass());
		return xstream.toXML(voiceResponseMessage);
	}
	
	/**
	 * 视频消息对象转换成XML
	 * @param videoResponseMessage
	 * @return
	 */
	public static String messageToXml(VideoResponseMessage videoResponseMessage) {
		xstream.alias("xml", videoResponseMessage.getClass());
		return xstream.toXML(videoResponseMessage);
	}

	/**
	 * 音乐消息对象转换成XML
	 * @param musicResponseMessage
	 * @return
	 */
	public static String messageToXml(MusicResponseMessage musicResponseMessage) {
		xstream.alias("xml", musicResponseMessage.getClass());
		return xstream.toXML(musicResponseMessage);
	}
	
	/**
	 * 图文消息对象转换成XML
	 * @param newsResponseMessage
	 * @return
	 */
	public static String messageToXml(NewsResponseMessage newsResponseMessage) {
		xstream.alias("xml", newsResponseMessage.getClass());
		xstream.alias("item", Article.class);
		return xstream.toXML(newsResponseMessage);
	}
	
	/**
	 * 组装文本客服消息
	 * @param openId
	 * @param content
	 * @return
	 */
	public static String makeTextCustomMessage(String openId, String content) {
		// 对消息内容中的双引号进行转义
		content = content.replace("\"", "\\\"");
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
		return String.format(jsonMsg, openId, content);
	}
	
	/**
	 * 组装图片客服消息
	 * @param openId
	 * @param mediaId
	 * @return
	 */
	public static String makeImageCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"image\",\"image\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}
	
	/**
	 * 组装语音客服消息
	 * @param openId
	 * @param mediaId
	 * @return
	 */
	public static String makeVoiceCustomMessage(String openId, String mediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"voice\",\"voice\":{\"media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId);
	}
	
	/**
	 * 组装视频客服消息
	 * @param openId
	 * @param mediaId
	 * @param thumbMediaId
	 * @return
	 */
	public static String makeVideoCustomMessage(String openId, String mediaId, String thumbMediaId) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"video\",\"video\":{\"media_id\":\"%s\",\"thumb_media_id\":\"%s\"}}";
		return String.format(jsonMsg, openId, mediaId, thumbMediaId);
	}
	
	/**
	 * 组装音乐客服消息
	 * @param openId
	 * @param music
	 * @return
	 */
	public static String makeMusicCustomMessage(String openId, Music music) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"music\",\"music\":%s}";
		jsonMsg = String.format(jsonMsg, openId, JSON.toJSONString(music));
		// 将jsonMsg中的thumbmediaid替换为thumb_media_id
		jsonMsg = jsonMsg.replace("thumbmediaid", "thumb_media_id");
		return jsonMsg;
	}
	
	/**
	 * 组装图文客服消息
	 * @param openId
	 * @param articleList
	 * @return
	 */
	public static String makeNewsCustomMessage(String openId, List<Article> articleList) {
		String jsonMsg = "{\"touser\":\"%s\",\"msgtype\":\"news\",\"news\":{\"articles\":%s}}";
		jsonMsg = String.format(jsonMsg, openId, JSON.toJSONString(articleList).replaceAll("\"", "\\\""));
		// 将jsonMsg中的picUrl替换为picurl
		jsonMsg = jsonMsg.replace("picUrl", "picurl");
		return jsonMsg;
	}
	
	/**
	 * 发送客服消息
	 * @param accessToken
	 * @param jsonMsg
	 * @return
	 */
	public static boolean sendCustomMessage(String accessToken, String jsonMsg) {
		log.info("消息内容：{}", jsonMsg);
		boolean result = false;
		String requestUrl = custom_send_url.replace("ACCESS_TOKEN", accessToken);
		// 发送客服消息
		JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "POST", jsonMsg);
		
		if (null != jsonObject) {
			int errorCode = jsonObject.getIntValue("errcode");
			String errorMsg = jsonObject.getString("errmsg");
			if (0 == errorCode) {
				result = true;
				log.info("客服消息发送成功 errorcode:{} errmsg:{}", errorCode, errorMsg);
			} else {
				log.error("客服消息发送失败 errorcode:{} errmsg:{}", errorCode, errorMsg);

			}
		}
		
		return result;
	}
	
}