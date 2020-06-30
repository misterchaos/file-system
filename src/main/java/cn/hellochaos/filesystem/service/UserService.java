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
     * 登录
     */
    User login(User user);

}
