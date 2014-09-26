package org.chukot.framework.weixin.model.advanced;

/**
 * 临时二维码信息
 * @author chukot
 *
 */
public class TemporaryQRCode {

	// 获取的二维码ticket
	private String ticket;
	// 二维码的有效时间，单位为秒，最长不超过1800秒
	private int expireSeconds;
	
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public int getExpireSeconds() {
		return expireSeconds;
	}
	public void setExpireSeconds(int expireSeconds) {
		this.expireSeconds = expireSeconds;
	}
	
}