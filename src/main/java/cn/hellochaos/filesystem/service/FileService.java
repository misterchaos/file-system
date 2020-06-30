package cn.hellochaos.filesystem.service;


import cn.hellochaos.filesystem.entity.User;
import cn.hellochaos.filesystem.entity.vo.File;

/**
 * 服务类
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 */
public interface FileService {

  /** 创建文件或者目录 */
  File create(String fileName,Integer parent,String type,Integer ownerId);

  /**
   * 创建用户目录
   */
  File createUserDir(String username, Integer userId);

  /**
   * 打开一个目录或文件
   *
   * @param direntId 要打开的目录或者文件id
   * @return 目录中的文件列表或者文件数据
   */
  File open(Integer direntId, User user);

  /** 更新文件 */
  File update(File file);

  /**
   * 删除文件（当是共享文件时，只把共享计数器减1，删除文件目录，计数器为0时，同时删除文件）
   *
   * @param fileId 要删除的文件id
   */
  void delete(Integer fileId);

  /**
   * 关闭文件
   *
   * @param fileId 要关闭的文件id
   */
  void close(Integer fileId);
}
