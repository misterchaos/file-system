package cn.hellochaos.filesystem.service.impl;

import cn.hellochaos.filesystem.entity.Dirent;
import cn.hellochaos.filesystem.entity.Inode;
import cn.hellochaos.filesystem.entity.User;
import cn.hellochaos.filesystem.entity.vo.File;
import cn.hellochaos.filesystem.exception.bizException.BizException;
import cn.hellochaos.filesystem.mapper.DirentMapper;
import cn.hellochaos.filesystem.mapper.InodeMapper;
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

  private final Set<Integer> activeList = new ConcurrentHashSet<>();

  @Override
  public File create(String fileName, Integer parent, String type, Integer ownerId) {
    log.info("正在创建文件/文件夹：{}",fileName);

    // 检查文件名是否合法
    checkFileName(fileName);

    // 检查是否重名
    if (parent!=null&&contain(parent, fileName)) {
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

    //创建..文件夹
    if(parent!=null&&!fileName.equals(File.SUPER_DIR)){
      createSuperDir(dirent.getDirentId());
    }

    log.info("创建完成！");

    // 返回文件
    return getFile(dirent.getDirentId());
  }

  @Override
  public File createUserDir(String username, Integer userId) {
    return create(username,diskService.getRootDirentId(),File.DIR,userId);
  }

  /**
   * 创建..文件夹
   */
  public File createSuperDir(Integer parent){
    log.info("正在创建共享文件/文件夹链接：{}",File.SUPER_DIR);

    Dirent superDirent = direntMapper.selectById(parent);

    // 链接到父目录的索引节点
    Inode inode = inodeMapper.selectById(superDirent.getInodeId());

    // 创建目录项
    Dirent dirent = new Dirent();
    dirent.setFilename(File.SUPER_DIR);
    dirent.setParent(parent);
    dirent.setInodeId(inode.getInodeId());
    dirent.insert();

    log.info("创建完成！");

    // 返回文件
    return getFile(dirent.getDirentId());  }

  @Override
  public File open(Integer direntId, User user) {
    log.info("正在打开文件");

    File root = read(diskService.getRootDirentId());

    File file;
    //读取文件
    if(direntId==null){
       if(User.ROOT_USER.equals(user.getUsername())){
         //如果是root用户则打开根目录
         file = root;
       }else {
         //其他用户则返回用户目录
         file = getFileByName(root.getDirentId(),user.getUsername());
       }
    }else {
      // 返回指定的文件
       file = read(direntId);
    }

    if(file==null){
      throw new BizException("文件/文件夹不存在，无法打开");
    }
    // 加入到已打开的文件列表中
    activeList.add(file.getInodeId());

    return file;
  }


  @Override
  public File update(File file) {
    // 检查文件名是否合法
    checkFileName(file.getFilename());
    file.updateById();

    // 检查文件内容是否有改动
    File old = read(file.getDirentId());
    if(file.getData()!=null&&!old.getData().equals(file.getData())) {
      // 更新数据
      diskService.update(file.getInodeId(), file.getData());
    }
    return file;
  }

  @Override
  public void delete(Integer fileId) {
    log.info("正在删除文件,direntId = {}",fileId);

    // 读取文件信息
    File file = read(fileId);

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
      } else {
        // 删除文件的块，释放资源
        diskService.delete(file.getInodeId());
      }
    }

    // 删除文件目录项
    file.deleteById();
  }

  @Override
  public void close(Integer fileId) {
    File file = read(fileId);
    if (activeList.contains(file.getInodeId())) {
      activeList.remove(file.getInodeId());
      log.info("关闭成功：inodeId = {}",file.getInodeId());
    }
  }

  /** 检查一个目录下是否存在该文件名的文件或者目录 */
  private boolean contain(Integer parentDir, String filename) {
    return getFileByName(parentDir, filename)!=null;
  }

  /**
   * 在一个目录下查找指定名称的文件
   */
  private File getFileByName(Integer parentDir, String filename) {
    List<File> fileList = read(parentDir).getFiles();
    for (int i = 0; fileList!=null&&i < fileList.size(); i++) {
      if (fileList.get(i).getFilename().equalsIgnoreCase(filename)) {
        return fileList.get(i);
      }
    }
    return null;
  }

  /** 通过目录id获取File */
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
    return file;
  }

  /** 检查文件名是否合法 */
  private void checkFileName(String fileName) {
    String regex = "[^*|\\:\"<>?/]+";
    if (!fileName.matches(regex)) {
      throw new BizException("文件名格式不合法！");
    }
  }


  /**
   * 读取文件
   */
  private File read(Integer direntId) {
    // 获取文件信息
    File file = getFile(direntId);
    if (file.getType().equals(File.DIR)) {
      // 打开目录
      List<Dirent> dirents =
              direntMapper.selectList(new QueryWrapper<Dirent>().eq("parent", file.getDirentId()));
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
