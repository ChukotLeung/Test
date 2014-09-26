package org.chukot.framework.weixin.model.message;

/**
 * 视频消息
 * @author chukot
 *
 */
public class VideoRequestMessage extends BaseRequestMessage {

	// 视频消息媒体ID
	private String MediaId;
	// 视频消息缩略图的媒体ID
	private String ThumbMediaId;
	
	public String getMediaId() {
		return MediaId;
	}
	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}
	public String getThumbMediaId() {
		return ThumbMediaId;
	}
	public void setThumbMediaId(String thumbMediaId) {
		ThumbMediaId = thumbMediaId;
	}
	
}