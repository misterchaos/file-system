package cn.hellochaos.filesystem.service.impl;

import cn.hellochaos.filesystem.entity.Dirent;
import cn.hellochaos.filesystem.entity.Inode;
import cn.hellochaos.filesystem.entity.User;
import cn.hellochaos.filesystem.entity.vo.File;
import cn.hellochaos.filesystem.exception.bizException.BizException;
import cn.hellochaos.filesystem.mapper.DirentMapper;
import cn.hellochaos.filesystem.mapper.InodeMapper;
import cn.hellochaos.filesystem.mapper.UserMapper;
import cn.hellochaos.filesystem.service.FileService;
import cn.hellochaos.filesystem.service.DiskService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class FileServiceImpl implements FileService {

  @Autowired private DirentMapper direntMapper;
  @Autowired private InodeMapper inodeMapper;
  @Autowired private DiskService diskService;
  @Autowired private UserMapper userMapper;

  private final Set<Integer> activeList = new ConcurrentHashSet<>();

  @Override
  public File create(String fileName, Integer parent, String type, Integer ownerId) {
    log.info("正在创建文件/文件夹：{}", fileName);

    // 检查文件名是否合法
    checkFileName(fileName);

    // 检查是否重名
    if (parent != null && contain(parent, fileName)) {
      throw new BizException("创建失败：该目录下已经有文件名为" + fileName + "的文件！");
    }

    // 创建索引节点
    Inode inode = new Inode();
    inode.setType(type);
    inode.setOwnId(ownerId);
    inode.insert();

    // 创建目录项
    Dirent dirent = new Dirent();
    dirent.setFilename(fileName);
    dirent.setParent(parent);
    dirent.setInodeId(inode.getInodeId());
    dirent.insert();

    // 如果是创建文件夹则需要保存目录id
    if (File.DIR.equalsIgnoreCase(type)) {
      diskService.update(inode.getInodeId(), String.valueOf(dirent.getDirentId()));
    }
    inode.updateById();

    // 创建..文件夹
    if (parent != null && !fileName.equals(File.SUPER_DIR) && File.DIR.equalsIgnoreCase(type)) {
      createSuperDir(dirent.getDirentId(), parent);
    }

    log.info("创建完成！");

    // 返回文件
    return getFile(dirent.getDirentId());
  }

  @Override
  public File createUserDir(User user) {
    File file =
        create(user.getUsername(), diskService.getRootDirentId(), File.DIR, user.getUserId());
    user.setUserDir(file.getDirentId());
    user.updateById();
    return file;
  }

  /** 创建..文件夹 */
  public File createSuperDir(Integer direntId, Integer parent) {
    log.info("正在创建共享文件/文件夹链接：{}", File.SUPER_DIR);

    Dirent superDirent = direntMapper.selectById(parent);

    // 链接到父目录的索引节点
    Inode inode = inodeMapper.selectById(superDirent.getInodeId());

    // 创建目录项
    Dirent dirent = new Dirent();
    dirent.setFilename(File.SUPER_DIR);
    dirent.setParent(direntId);
    dirent.setInodeId(inode.getInodeId());
    dirent.insert();

    log.info("创建完成！");

    // 返回文件
    return getFile(dirent.getDirentId());
  }

  @Override
  public File open(Integer direntId, User user) {
    log.info("正在打开文件");

    File file;
    // 读取文件
    if (direntId == null) {
      file = getUserDir(user);
    } else {
      // 返回指定的文件
      file = getFile(direntId);
    }

    // 加入到已打开的文件列表中
    activeList.add(file.getInodeId());

    log.info("打开完成");
    return read(file);
  }

  @Override
  public File update(File file) {
    log.info("正在更新文件");

    // 检查文件名是否合法
    checkFileName(file.getFilename());
    file.updateById();

    // 检查文件内容是否有改动
    File old = read(file.getDirentId());
    if (file.getData() != null && !old.getData().equals(file.getData())) {
      // 更新数据
      diskService.update(file.getInodeId(), file.getData());
    }

    log.info("更新完成");
    return file;
  }

  @Override
  public void delete(Integer fileId) {
    log.info("正在删除文件,direntId = {}", fileId);

    // 读取文件信息
    File file = read(fileId);

    if (file.getFilename().equals(File.SUPER_DIR)) {
      file.deleteById();
      return;
    }

    // 检查是否已经打开
    if (activeList.contains(file.getInodeId())) {
      throw new BizException("文件已打开，请关闭后删除");
    }

    // 检查共享计数器
    if (file.getShareCount() > 0) {
      // 获取索引节点
      Inode inode = new Inode();
      BeanUtil.copyProperties(file, inode);
      // 共享计数器减1
      inode.setShareCount(inode.getShareCount() - 1);
      inode.updateById();
    } else {
      // 检查是目录还是文件
      if (file.getType().equals(File.DIR)) {
        List<File> files = file.getFiles();
        for (File subFile : files) {
          this.delete(subFile.getDirentId());
        }
      }
    }
    // 删除文件的块，释放资源
    diskService.delete(file.getInodeId());
    // 删除文件目录项
    file.deleteById();
  }

  @Override
  public void close(Integer fileId) {
    File file = read(fileId);
    if (activeList.contains(file.getInodeId())) {
      activeList.remove(file.getInodeId());
      log.info("关闭成功：inodeId = {}", file.getInodeId());
    }
  }

  /** 检查一个目录下是否存在该文件名的文件或者目录 */
  private boolean contain(Integer parentDir, String filename) {
    return getFileByName(parentDir, filename) != null;
  }

  /** 在一个目录下查找指定名称的文件 */
  private File getFileByName(Integer parentDir, String filename) {
    List<File> fileList = read(parentDir).getFiles();
    for (int i = 0; fileList != null && i < fileList.size(); i++) {
      if (fileList.get(i).getFilename().equalsIgnoreCase(filename)) {
        return fileList.get(i);
      }
    }
    return null;
  }

  private File getUserDir(User user) {
    if (User.ROOT_USER.equals(user.getUsername())) {
      // 如果是root用户则打开根目录
      return read(diskService.getRootDirentId());
    } else {
      // 其他用户则返回用户目录
      return read(user.getUserDir());
    }
  }

  /** 通过目录id获取File信息 */
  private File getFile(Integer direntId) {
    Dirent dirent = direntMapper.selectById(direntId);
    if (dirent == null) {
      throw new BizException("id为" + direntId + "的文件/目录不存在");
    }
    Inode inode = inodeMapper.selectById(dirent.getInodeId());
    if (inode == null) {
      throw new BizException("id为" + dirent.getInodeId() + "的索引节点不存在");
    }
    File file = new File();
    BeanUtil.copyProperties(dirent, file);
    BeanUtil.copyProperties(inode, file);
    // 获取所有者信息
    User owner = userMapper.selectById(file.getOwnId());
    if (owner == null) {
      file.setOwner("root");
    } else {
      file.setOwner(owner.getUsername());
    }
    return file;
  }

  /** 检查文件名是否合法 */
  private void checkFileName(String fileName) {
    String regex = "[^*|\\:\"<>?/]+";
    if (!fileName.matches(regex)) {
      throw new BizException("文件名格式不合法！");
    }
  }

  /** 读取文件 */
  private File read(Integer direntId) {
    return read(getFile(direntId));
  }

  /** 读取文件 */
  private File read(File file) {
    // 获取文件信息
    if (file.getType().equals(File.DIR)) {
      // 获取父节点
      Integer parent;
      try {
        parent = Integer.valueOf(diskService.getFileData(file.getInodeId()));
      } catch (Exception e) {
        parent = file.getDirentId();
      }
      // 打开目录
      List<Dirent> dirents =
          direntMapper.selectList(new QueryWrapper<Dirent>().eq("parent", parent));
      List<File> subFiles = new LinkedList<>();
      for (int i = 0; i < dirents.size(); i++) {
        File subFile = getFile(dirents.get(i).getDirentId());
        subFiles.add(subFile);
      }
      file.setFiles(subFiles);
    } else {
      // 读取数据
      file.setData(diskService.getFileData(file.getInodeId()));
    }
    return file;
  }
}
