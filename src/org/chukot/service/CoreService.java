package org.chukot.service;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.chukot.framework.weixin.model.message.TextResponseMessage;
import org.chukot.framework.weixin.util.MessageUtil;

/**
 * 核心服务类
 * @author chukot
 *
 */
public class CoreService {

	/**
	 * 处理微信发来的请求
	 * @param request
	 * @return
	 */
	public static String processRequest(HttpServletRequest request) {
		// XML格式的消息数据
		String respXml = null;
		
		try {
			// 调用parseXml方法解析请求消息
			Map<String, String> requestMap = MessageUtil.parseXml(request);
			// 发送方账号
			String fromUserName = requestMap.get("FromUserName");
			// 开发者微信号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			
			// 回复文本消息
			TextResponseMessage textResponseMessage = new TextResponseMessage();
			textResponseMessage.setToUserName(fromUserName);
			textResponseMessage.setFromUserName(toUserName);
			textResponseMessage.setCreateTime(new Date().getTime());
			textResponseMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
			
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				String eventType = requestMap.get("Event");
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					textResponseMessage.setContent("谢谢您的关注！");
					respXml = MessageUtil.messageToXml(textResponseMessage);
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
					
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_LOCATION)) {
					
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {

				} else if (eventType.equals(MessageUtil.EVENT_TYPE_VIEW)) {

				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return respXml;
	}
	
}