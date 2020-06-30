package cn.hellochaos.filesystem.controller;


import cn.hellochaos.filesystem.entity.User;
import cn.hellochaos.filesystem.entity.dto.ResultBean;
import cn.hellochaos.filesystem.security.shiro.token.UserToken;
import cn.hellochaos.filesystem.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 前端控制器
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 * @version v1.0
 */
@RestController
@RequestMapping("/filesystem/api/v1/user")
public class UserController {

  @Autowired private UserService userService;

  @PostMapping("/login")
  public ResultBean<?> login(@RequestBody User user,HttpServletResponse response){
      Subject subject = SecurityUtils.getSubject();
      subject.login(new UserToken(user.getUsername()));
      user = (User) subject.getPrincipal();
      response.setHeader("Authorization", subject.getSession().getId().toString());
      return new ResultBean<>(user);
  }
}
