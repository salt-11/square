package cn.hawy.quick.partner.core.filter;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Administrator
 */
public class MyAuthenticationFilter extends FormAuthenticationFilter {

    private static final String USR_LOGIN_URL = "/login";
    private static final String PARTNER_LOGIN_URL = "/partner/login";
    private static final String AGENT_LOGIN_URL = "/agent/login";



    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();

        if(url.contains("/partner/")) {
            WebUtils.issueRedirect(request, response, PARTNER_LOGIN_URL);
        }else if(url.contains("/agent/")) {
            WebUtils.issueRedirect(request, response, AGENT_LOGIN_URL);
        }else {
            WebUtils.issueRedirect(request, response, USR_LOGIN_URL);
        }
    }

}
