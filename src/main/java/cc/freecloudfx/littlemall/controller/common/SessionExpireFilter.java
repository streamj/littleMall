package cc.freecloudfx.littlemall.controller.common;

import cc.freecloudfx.littlemall.common.Const;
import cc.freecloudfx.littlemall.pojo.User;
import cc.freecloudfx.littlemall.util.CookieUtil;
import cc.freecloudfx.littlemall.util.JsonUtil;
import cc.freecloudfx.littlemall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author StReaM on 3/20/2018
 */
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String loginTOken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginTOken)) {
            /* 你倒是保持一致性啊，一会 not 的 */
            String userJsonStr = RedisShardedPoolUtil.get(loginTOken);
            User user = JsonUtil.string2Obj(userJsonStr, User.class);
            if (user != null) {
                // 重置 session 时常
                RedisShardedPoolUtil.expire(loginTOken, Const.RedisCacheTime.REDIS_SESSION_TIME);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
