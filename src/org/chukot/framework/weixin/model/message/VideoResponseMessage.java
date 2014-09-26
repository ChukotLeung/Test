package org.chukot.framework.weixin.model.message;

/**
 * 视频消息
 * @author chukot
 *
 */
public class VideoResponseMessage extends BaseResponseMessage {

	// 视频
	private Video Video;
	
	public Video getVideo() {
		return Video;
	}

	public void setVideo(Video video) {
		Video = video;
	}

	public class Video {
		// 媒体ID
		private String MediaId;
		// 缩略图的媒体ID
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
	
}