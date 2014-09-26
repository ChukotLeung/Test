package org.chukot.framework.weixin.model.message;

/**
 * 语音消息
 * @author chukot
 *
 */
public class VoiceResponseMessage extends BaseResponseMessage {

	// 语音
	private Voice Voice;
	
	public Voice getVoice() {
		return Voice;
	}

	public void setVoice(Voice voice) {
		Voice = voice;
	}

	public class Voice {
		
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