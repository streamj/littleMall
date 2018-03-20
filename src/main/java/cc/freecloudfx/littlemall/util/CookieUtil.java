package cc.freecloudfx.littlemall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author StReaM on 3/20/2018
 */
@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMAIN = "littlemall.com"; /* .littlemall.com 这个 . 在高版本 tomcat 会吃屎 */
    private final static String COOKIE_NAME = "little_login_token";

    public static void writeLoginToken(HttpServletResponse servletResponse, String token) {
        Cookie cookie = new Cookie(COOKIE_NAME, token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setHttpOnly(true); // 防止脚本攻击，禁止脚本访问 cookie
        /* -1 means forever, if don't set this, cookie won't write to disk
         instead it writes to memory, only available in current page */
        log.info("write cookie Name:{}, cookie value:{}",
                cookie.getName(), cookie.getValue());
        servletResponse.addCookie(cookie);
    }

    public static String readLoginToken(HttpServletRequest servletRequest) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies!= null) {
            for (Cookie cookie : cookies) {
                log.info("read cookie Name:{}, cookie value:{}",
                        cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    log.info("return cookie Name:{}, cookie value:{}",
                            cookie.getName(), cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void deleteLoginToken(HttpServletRequest servletRequest,
                                        HttpServletResponse servletResponse) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME)) {
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    cookie.setMaxAge(0); // 0 表示删除 cookie
                    log.info("delete cookie Name:{}, cookie value:{}",
                            cookie.getName(), cookie.getValue());
                    servletResponse.addCookie(cookie); // add 一个有效期 0 的 cookie 返回给浏览器，浏览器立马发疯一样删掉他
                    return;
                }
            }
        }
    }
}
