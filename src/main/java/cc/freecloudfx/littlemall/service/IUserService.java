package cc.freecloudfx.littlemall.service;

import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.pojo.User;

/**
 * @author StReaM on 2/24/2018
 */
public interface IUserService {
    ServerResponse checkAdminRole(User user);

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken);

    ServerResponse<String> resetPassword(String oldPassword, String newPassword, User user);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<User> getInformation(Integer userId);
}
