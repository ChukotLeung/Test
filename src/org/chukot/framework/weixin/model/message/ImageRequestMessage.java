package org.chukot.framework.weixin.model.message;

/**
 * 图片消息
 * @author chukot
 *
 */
public class ImageRequestMessage extends BaseRequestMessage {

	// 图片链接
	private String PicUrl;

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}
	
}