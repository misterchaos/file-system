package cn.hellochaos.filesystem.security.shiro;

import cn.hellochaos.filesystem.security.shiro.filter.CrosFilter;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

  @Bean
  public ShiroRealm shiroRealm() {
    ShiroRealm shiroRealm = new ShiroRealm();
    // 小程序使用openid登录使用的realm
    return shiroRealm;
  }

  @Bean
  public WebSecurityManager securityManager(
      @Qualifier("sessionManager") SessionManager sessionManager) {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setSessionManager(sessionManager);
    securityManager.setRealm(shiroRealm());
    return securityManager;
  }

  @Bean("sessionManager")
  public SessionManager sessionManager() {
    CustomDefaultWebSessionManager manager = new CustomDefaultWebSessionManager();
    manager.setSessionDAO(new EnterpriseCacheSessionDAO());
    // 半年过期
    manager.setGlobalSessionTimeout(
        4320 * CustomDefaultWebSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL);
    return manager;
  }

  @Bean
  public ShiroFilterFactoryBean shiroFilter(WebSecurityManager securityManager) {
    ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
    shiroFilterFactoryBean.setSecurityManager(securityManager);
    Map<String, String> map = new LinkedHashMap<>();
    // todo 这里后期得进行修改

    map.put("/", "anon");
    map.put("/api/user/login", "anon");
    map.put("/island/api/v1/user/login", "anon");
    map.put("/island/api/v1/user/", "anon");
    map.put("/wx/**", "anon");
    map.put("/css/**", "anon");
    map.put("/js/**", "anon");
    map.put("/fonts/**", "anon");
    map.put("/sockjs-node/**", "anon");
    map.put("/img/**", "anon");
    map.put("/login/**", "anon");
    map.put("/#/login/**", "anon");
    map.put("/**", "authc");

    shiroFilterFactoryBean.setLoginUrl("/#/login");
    shiroFilterFactoryBean.getFilters().put("authc", new CrosFilter());
    shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
    return shiroFilterFactoryBean;
  }
}
