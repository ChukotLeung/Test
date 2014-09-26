package org.chukot.framework.weixin.model.event;

/**
 * 自定义菜单事件
 * @author chukot
 *
 */
public class MenuEvent extends BaseEvent {

	// 事件KEY值，与自定义菜单接口中的KEY值对应
	private String EventKey;

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}
	
}