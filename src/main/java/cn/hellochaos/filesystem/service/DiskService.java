package cn.hellochaos.filesystem.service;

import cn.hellochaos.filesystem.entity.Block;
import cn.hellochaos.filesystem.entity.Inode;
import cn.hellochaos.filesystem.entity.Volume;
import cn.hellochaos.filesystem.entity.vo.Disk;
import cn.hellochaos.filesystem.entity.vo.File;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 服务类
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 */
public interface DiskService {

  /**
   * 分页查询Volume
   *
   * @param page 当前页数
   * @param pageSize 页的大小
   * @param factor 搜索关键词
   * @return 返回mybatis-plus的Page对象,其中records字段为符合条件的查询结果
   * @author Yuchao-Huang
   * @since 2020-06-28
   */
  Page<Volume> listVolumesByPage(int page, int pageSize, String factor);

  /**
   * 根据id查询Volume
   *
   * @param id 需要查询的Volume的id
   * @return 返回对应id的Volume对象
   * @author Yuchao-Huang
   * @since 2020-06-28
   */
  Volume getVolumeById(int id);

  /**
   * 插入Volume
   *
   * @param volume 需要插入的Volume对象
   * @return 返回插入成功之后Volume对象的id
   * @author Yuchao-Huang
   * @since 2020-06-28
   */
  int insertVolume(Volume volume);

  /**
   * 根据id删除Volume
   *
   * @param id 需要删除的Volume对象的id
   * @return 返回被删除的Volume对象的id
   * @author Yuchao-Huang
   * @since 2020-06-28
   */
  int deleteVolumeById(int id);

  /**
   * 根据id更新Volume
   *
   * @param volume 需要更新的Volume对象
   * @return 返回被更新的Volume对象的id
   * @author Yuchao-Huang
   * @since 2020-06-28
   */
  int updateVolume(Volume volume);

  /** 创建一个卷 */
  Volume createVolume(String volumeLabel);

  /** 格式化卷 必需信息：磁盘id,容量,块大小 */
  Volume format(Volume volume);

  /** 获取文件数据 */
  String getFileData(Integer inodeId);

  /** 保存文件数据 */
  void update(Integer inodeId, String data);

  /** 系统初始化 */
  void init();

  /** 删除文件 */
  void deleteInode(Integer inodeId);

  /** 获取根目录id */
  Integer getRootDirentId();

  /** 获取磁盘信息 */
  Disk getDiskInfo();
}
