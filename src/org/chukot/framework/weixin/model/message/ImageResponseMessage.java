package org.chukot.framework.weixin.model.message;

/**
 * 图片消息
 * @author chukot
 *
 */
public class ImageResponseMessage extends BaseResponseMessage {

	// 图片
	private Image Image;

	public Image getImage() {
		return Image;
	}

	public void setImage(Image image) {
		Image = image;
	}

	public class Image {
		
		// 媒体文件ID
		private String MediaId;

		public String getMediaId() {
			return MediaId;
		}

		public void setMediaId(String mediaId) {
			MediaId = mediaId;
		}
	}
	
}