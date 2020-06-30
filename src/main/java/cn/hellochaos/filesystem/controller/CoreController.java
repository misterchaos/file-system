package cn.hellochaos.filesystem.controller;


import cn.hellochaos.filesystem.entity.User;
import cn.hellochaos.filesystem.entity.Volume;
import cn.hellochaos.filesystem.entity.dto.ResultBean;
import cn.hellochaos.filesystem.entity.vo.File;
import cn.hellochaos.filesystem.exception.bizException.BizException;
import cn.hellochaos.filesystem.exception.bizException.BizExceptionCodeEnum;
import cn.hellochaos.filesystem.service.DiskService;
import cn.hellochaos.filesystem.service.FileService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 * @version v1.0
 */
@RestController
@RequestMapping("/filesystem/api/v1/core")
public class CoreController {

    @Autowired
    private FileService fileService;


    /**
    * 根据id查询
    */
    @RequestMapping(method = RequestMethod.GET)
    public ResultBean<?> open(@RequestParam(required = false) Integer id) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        if (null == user) {
            throw new BizException(BizExceptionCodeEnum.NO_LOGIN);
        }
        return new ResultBean<>(fileService.open(id,user));
    }

    /**
    * 新增
    */
    @RequestMapping(method = RequestMethod.POST)
    public ResultBean<?> create(@RequestBody File file) {
        return new ResultBean<>(fileService.create(file.getFilename(),file.getParent(),
        file.getType(),file.getOwnId()));
    }

    /**
    * 删除
    */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResultBean<?> delete(@PathVariable("id") Integer id) {
        fileService.delete(id);
        return new ResultBean<>();
    }


    /**
     * 修改
     */
    @RequestMapping(method = RequestMethod.PUT,value = "/{id}")
    public ResultBean<?> close(@PathVariable("id") Integer id) {
        fileService.close(id);
        return new ResultBean<>();
    }

    /**
    * 修改
    */
    @RequestMapping(method = RequestMethod.PUT)
    public ResultBean<?> update(@RequestBody File file) {
        return new ResultBean<>(fileService.update(file));
    }

}
