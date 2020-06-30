package cn.hellochaos.filesystem.controller;


import cn.hellochaos.filesystem.entity.Volume;
import cn.hellochaos.filesystem.entity.dto.ResultBean;
import cn.hellochaos.filesystem.service.VolumeService;
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
@RequestMapping("/filesystem/api/v1/volume")
public class VolumeController {

    @Autowired
    private VolumeService volumeService;

    /**
    * 查询分页数据
    */
    @RequestMapping(method = RequestMethod.GET)
    public ResultBean<?> listByPage(@RequestParam(name = "page", defaultValue = "1") int page,
                                    @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
                                    @RequestParam(name = "factor", defaultValue = "") String factor) {
        return new ResultBean<>(volumeService.listVolumesByPage(page, pageSize,factor));
    }


    /**
    * 根据id查询
    */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResultBean<?> getById(@PathVariable("id") Integer id) {
        return new ResultBean<>(volumeService.getVolumeById(id));
    }

    /**
    * 新增
    */
    @RequestMapping(method = RequestMethod.POST)
    public ResultBean<?> insert(@RequestBody Volume volume) {
        return new ResultBean<>(volumeService.insertVolume(volume));
    }

    /**
    * 删除
    */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResultBean<?> deleteById(@PathVariable("id") Integer id) {
        return new ResultBean<>(volumeService.deleteVolumeById(id));
    }

    /**
    * 修改
    */
    @RequestMapping(method = RequestMethod.PUT)
    public ResultBean<?> updateById(@RequestBody Volume volume) {
        return new ResultBean<>(volumeService.updateVolume(volume));
    }
}
