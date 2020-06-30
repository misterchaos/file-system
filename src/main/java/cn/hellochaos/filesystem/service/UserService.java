package cn.hellochaos.filesystem.service;

import cn.hellochaos.filesystem.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* <p>
*  服务类
* </p>
*
* @author Yuchao-Huang
* @since 2020-06-28
*/
public interface UserService {

    /**
    * 分页查询User
    *
    * @param page     当前页数
    * @param pageSize 页的大小
    * @param factor  搜索关键词
    * @return 返回mybatis-plus的Page对象,其中records字段为符合条件的查询结果
    * @author Yuchao-Huang
    * @since 2020-06-28
    */
    Page<User> listUsersByPage(int page, int pageSize, String factor);

    /**
    * 根据id查询User
    *
    * @param id 需要查询的User的id
    * @return 返回对应id的User对象
    * @author Yuchao-Huang
    * @since 2020-06-28
    */
    User getUserById(int id);

    /**
    * 插入User
    *
    * @param user 需要插入的User对象
    * @return 返回插入成功之后User对象的id
    * @author Yuchao-Huang
    * @since 2020-06-28
    */
    int insertUser(User user);

    /**
    * 根据id删除User
    *
    * @param id 需要删除的User对象的id
    * @return 返回被删除的User对象的id
    * @author Yuchao-Huang
    * @since 2020-06-28
    */
    int deleteUserById(int id);

    /**
    * 根据id更新User
    *
    * @param user 需要更新的User对象
    * @return 返回被更新的User对象的id
    * @author Yuchao-Huang
    * @since 2020-06-28
    */
    int updateUser(User user);

    /**
     * 登录
     * @param user
     */
    User login(User user);

}
