package cc.freecloudfx.littlemall.controller.backend;

import cc.freecloudfx.littlemall.common.Const;
import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.pojo.User;
import cc.freecloudfx.littlemall.service.IUserService;
import cc.freecloudfx.littlemall.util.CookieUtil;
import cc.freecloudfx.littlemall.util.JsonUtil;
import cc.freecloudfx.littlemall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author StReaM on 2/27/2018
 */
@Controller
@RequestMapping("/manage/user")
public class UserManagerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody()
    public ServerResponse<User> login(String username, String password,
                                      HttpSession session, HttpServletResponse servletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                CookieUtil.writeLoginToken(servletResponse, session.getId());
                RedisShardedPoolUtil.setEx(session.getId(), Const.RedisCacheTime.REDIS_SESSION_TIME,
                        JsonUtil.obj2String(response.getData()));
                return response;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员，不能登录");
            }
        }
        return response;
    }
}
