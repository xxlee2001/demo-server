/**
 * diwork自定义登录校验示例
 * 实现开发免登陆
 */
package com.yonyou.iuap.demo.filter;

import com.yonyou.diwork.common.web.DiworkWebUtil;
import com.yonyou.diwork.exception.BusinessException;
import com.yonyou.diwork.verifier.DiworkSessionVerifierChain;
import com.yonyou.diwork.verifier.ILoginVerifier;
import com.yonyou.workbench.core.DiworkSessionBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
*@author haojh@yonyou.com
*@date  2019年6月25日---下午2:19:56
*@problem
*@answer
*@action
*/

/**
*@author haojh@yonyou.com
*/
@Component
@Order(9)
public class MockLoginVerifier implements ILoginVerifier {

	@Override
	public DiworkSessionBean getSession(HttpServletRequest request, HttpServletResponse response,
			DiworkSessionVerifierChain verifierChain) throws BusinessException {
		DiworkSessionBean sessionBean = new DiworkSessionBean();
		sessionBean.setClientIp(DiworkWebUtil.getRemortIP(request));
		if (StringUtils.isBlank(sessionBean.getLocale())) {
			Cookie cookie = WebUtils.getCookie((HttpServletRequest) request, "locale");
			if (cookie != null) {
				sessionBean.setLocale(StringUtils.isBlank(cookie.getValue()) ? "zh_CN" : cookie.getValue());
			} else {
				sessionBean.setLocale("zh_CN");
			}
		}
		// userid = "3ef1bd98-274f-494b-8e17-e8f3bac1a4b9"
		sessionBean.setUserId("3e487a24-5347-46eb-8d2e-d096638be8ea");
		sessionBean.setTenantId("d0jjxhhs");
		HashMap<String, Object > yht_access_token = new HashMap<String, Object>(){{
			put("yht_access_token","yht_access_token=bttZXRlc0g4SUxROU5lWEp4ZlNNdERIWDkrQ1lpZXNZOG0weW02cjlDa2ZWWkQ0Z21WZkFsMHk4eitWTzI1MWIvZFVBdDdkMmV4QUJmaGhFRDhqUmFRRmdCUW94Uld1cnVYV0RXdGZHTFM2TU09XzNlZjFiZDk4LTI3NGYtNDk0Yi04ZTE3LWU4ZjNiYWMxYTRiOV8xMC4xOTAuMjUzLjE4NTo4MF8xNTg3MDg2NTgwNDky__1587086580726");
		}};
		sessionBean.setExt(yht_access_token);
		return sessionBean;
	}

}
