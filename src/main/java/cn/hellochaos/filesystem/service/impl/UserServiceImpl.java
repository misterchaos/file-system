package cn.hellochaos.filesystem.service.impl;

import cn.hellochaos.filesystem.entity.Dirent;
import cn.hellochaos.filesystem.entity.User;
import cn.hellochaos.filesystem.exception.bizException.BizException;
import cn.hellochaos.filesystem.mapper.DirentMapper;
import cn.hellochaos.filesystem.mapper.UserMapper;
import cn.hellochaos.filesystem.service.FileService;
import cn.hellochaos.filesystem.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现类
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  @Autowired private FileService fileService;
  @Autowired private DirentMapper direntMapper;

  @Override
  public User login(User user) {
    log.info("正在登录");
    User userPo = super.getOne(new QueryWrapper<User>().eq("username", user.getUsername()));
    if (userPo == null) {
      user.insert();
      // 建立用户目录
      fileService.createUserDir(user);
    } else {
      user = userPo;
      if (direntMapper.selectOne(new QueryWrapper<Dirent>().eq("dirent_id", user.getUserDir()))
          == null) {
        fileService.createUserDir(user);
      }
    }
    return user;
  }
}
