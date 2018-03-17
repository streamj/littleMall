package cc.freecloudfx.littlemall.dao;

import cc.freecloudfx.littlemall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    /**
     * 多个参数的时候需要 Param 注解，而且查询的时候，是看注解参数里的那个字符串
     * @param username
     * @param password
     * @return
     */
    User selectLogin(@Param("username") String username, @Param("password")String password);

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username")String username, @Param("question")String question,
                    @Param("answer")String answer);

    int updatePasswordByUsername(@Param("username")String username, @Param("newPassword")String newPassword);

    int checkPassword(@Param("password")String password, @Param("userId")Integer userId);

    int checkEmailByUserId(@Param("email")String email, @Param("userId")Integer userId);
}