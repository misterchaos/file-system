package cn.hellochaos.filesystem.controller.handler;

import cn.hellochaos.filesystem.entity.dto.ResultBean;
import cn.hellochaos.filesystem.exception.bizException.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * @author <a href="mailto:kobe524348@gmail.com">黄钰朝</a>
 * @description 全局异常处理器
 * @date 2019-08-12 19:19
 */
@Slf4j
@RestControllerAdvice
@CrossOrigin
public class GlobalExceptionHandler implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse,
                                         Object o, Exception e) {
        log.info("请求异常" + e.getMessage());
        e.printStackTrace();
        return null;
    }

    @ExceptionHandler(cn.hellochaos.filesystem.exception.bizException.BizException.class)
    public ResultBean<?> bizException(BizException e) {
        return new ResultBean<>(e);
    }


    @ExceptionHandler(SQLException.class)
    public ResultBean<?> SQLException(SQLException e) {
        if(e.getMessage().contains("Incorrect string value")){
            return new ResultBean<>("数据库不支持保存您输入的数据类型（特殊符号或表情）");
        }
        return new ResultBean<>(e.getMessage());
    }

}
