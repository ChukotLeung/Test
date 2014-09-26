package org.chukot.framework.weixin.model.message;

/**
 * 文本消息
 * @author chukot
 *
 */
public class TextResponseMessage extends BaseResponseMessage {

	// 回复的消息内容
	private String Content;

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		this.Content = content;
	}
	
}