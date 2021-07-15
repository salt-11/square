package cn.hawy.quick.agent.core.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

public class AngWebSessionManager extends DefaultWebSessionManager {



    private static final Logger log = LoggerFactory.getLogger(DefaultWebSessionManager.class);

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        return this.getReferencedSessionId(request, response);
    }

    private Serializable getReferencedSessionId(ServletRequest request, ServletResponse response) {
        String id = this.getSessionIdCookieValue(request, response);
        if (id != null) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "cookie");
        } else {
            id = this.getUriPathSegmentParamValue(request, "JSESSIONID");
            if (id == null) {
                String name = this.getSessionIdName();
                id = request.getParameter(name);
                if (id == null) {
                    id = request.getParameter(name.toLowerCase());
                }
            }

            if (id != null) {
                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "url");
            }
        }

        if (id != null) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
        }

        request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED, this.isSessionIdUrlRewritingEnabled());
        return id;
    }

    private String getSessionIdCookieValue(ServletRequest request, ServletResponse response) {
        if (!this.isSessionIdCookieEnabled()) {
            log.debug("Session ID cookie is disabled - session id will not be acquired from a request cookie.");
            return null;
        } else if (!(request instanceof HttpServletRequest)) {
            log.debug("Current request is not an HttpServletRequest - cannot get session ID cookie.  Returning null.");
            return null;
        } else {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            return this.getSessionIdCookie().readValue(httpRequest, WebUtils.toHttp(response));
        }
    }

    private String getUriPathSegmentParamValue(ServletRequest servletRequest, String paramName) {
        if (!(servletRequest instanceof HttpServletRequest)) {
            return null;
        } else {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            String uri = request.getRequestURI();
            if (uri == null) {
                return null;
            } else {
                int queryStartIndex = uri.indexOf(63);
                if (queryStartIndex >= 0) {
                    uri = uri.substring(0, queryStartIndex);
                }

                int index = uri.indexOf(59);
                if (index < 0) {
                    return null;
                } else {
                    String TOKEN = paramName + "=";
                    uri = uri.substring(index + 1);
                    index = uri.lastIndexOf(TOKEN);
                    if (index < 0) {
                        return null;
                    } else {
                        uri = uri.substring(index + TOKEN.length());
                        index = uri.indexOf(59);
                        if (index >= 0) {
                            uri = uri.substring(0, index);
                        }

                        return uri;
                    }
                }
            }
        }
    }



    private String getSessionIdName() {
        String name = this.getSessionIdCookie() != null ? this.getSessionIdCookie().getName() : null;
        if (name == null) {
            name = "JSESSIONID";
        }
        return name;
    }

    /**
     * 重写onStart方法，该方法在生成session时可调用
     * 此时可对父类SessionIdCookie进行操作，更改其CookiseName的值
     * 默认为JSESSIONID，可由配置注入单个统一名称
     * 重写方法可实现多个名称，运用于不同系统拥有不同CookiseName
     * （未启用）
     * 使用方法替换注入DefaultWebSessionManager
     */
    @Override
    protected void onStart(Session session, SessionContext context) {
        String name = "";
        HttpServletRequest request = WebUtils.getHttpRequest(context);
        String url = request.getRequestURI();
        if(url.contains("/partner/")) {
            name = "shiroCookie_partner";
        }else if(url.contains("/agent/")) {
            name = "shiroCookie_agent";
        }else {
            name = "shiroCookie";
        }
        super.getSessionIdCookie().setName(name);
        super.onStart(session, context);
    }
}
