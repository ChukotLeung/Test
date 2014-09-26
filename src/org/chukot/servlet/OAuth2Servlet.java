package org.chukot.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.chukot.framework.weixin.model.advanced.Oauth2Token;
import org.chukot.framework.weixin.model.advanced.SNSUserInfo;
import org.chukot.framework.weixin.util.AdvancedUtil;

/**
 * 授权后的回调请求处理
 */
@WebServlet("/OAuth2Servlet")
public class OAuth2Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String code = request.getParameter("code");
		// 用户同意授权
		if (StringUtils.isNotEmpty(code)) {
			Oauth2Token oauth2Token = AdvancedUtil.getOauth2AccessToken("APPID", "APPSECRET", code);
			String accessToken = oauth2Token.getAccessToken();
			String openId = oauth2Token.getOpenId();
			SNSUserInfo snsUserInfo = AdvancedUtil.getSNSUserInfo(accessToken, openId);
			request.setAttribute("snsUserInfo", snsUserInfo);
			// ..
		}
		// ..
	}

}