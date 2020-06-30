package cn.hellochaos.filesystem.security.shiro.token;

import cn.hellochaos.filesystem.entity.User;
import lombok.Data;
import org.apache.shiro.authc.UsernamePasswordToken;

import java.io.Serializable;

@Data
public class WechatToken extends UsernamePasswordToken implements Serializable {

    private User user;

    private String code;

    private static final long serialVersionUID = 4812793519945855483L;

    @Override
    public Object getPrincipal() {
        return getUser();
    }


    @Override
    public Object getCredentials() {
        return "ok";
    }

    public WechatToken(String code) {
        this.code = code;
    }

}