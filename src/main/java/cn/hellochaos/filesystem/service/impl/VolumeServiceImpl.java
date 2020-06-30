package cn.hellochaos.filesystem.service.impl;

import cn.hellochaos.filesystem.entity.Block;
import cn.hellochaos.filesystem.entity.Fat;
import cn.hellochaos.filesystem.entity.Inode;
import cn.hellochaos.filesystem.entity.Volume;
import cn.hellochaos.filesystem.entity.vo.File;
import cn.hellochaos.filesystem.exception.bizException.BizException;
import cn.hellochaos.filesystem.mapper.BlockMapper;
import cn.hellochaos.filesystem.mapper.FatMapper;
import cn.hellochaos.filesystem.mapper.InodeMapper;
import cn.hellochaos.filesystem.mapper.VolumeMapper;
import cn.hellochaos.filesystem.service.FileService;
import cn.hellochaos.filesystem.service.VolumeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务实现类
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 */
@Slf4j
@Service
@Transactional
public class VolumeServiceImpl extends ServiceImpl<VolumeMapper, Volume> implements VolumeService {

  /** 工作硬盘 */
  private static Volume currentVolume;

  @Autowired private FileService fileService;

  @Autowired private BlockMapper blockMapper;

  @Autowired private InodeMapper inodeMapper;

  @Autowired
  private FatMapper fatMapper;

  @Override
  public Page<Volume> listVolumesByPage(int page, int pageSize, String factor) {
    log.info("正在执行分页查询volume: page = {} pageSize = {} factor = {}", page, pageSize, factor);
    QueryWrapper<Volume> queryWrapper = new QueryWrapper<Volume>().like("", factor);
    // TODO 这里需要自定义用于匹配的字段,并把wrapper传入下面的page方法
    Page<Volume> result = super.page(new Page<>(page, pageSize));
    log.info("分页查询volume完毕: 结果数 = {} ", result.getRecords().size());
    return result;
  }

  @Override
  public Volume getVolumeById(int id) {
    log.info("正在查询volume中id为{}的数据", id);
    Volume volume = super.getById(id);
    log.info("查询id为{}的volume{}", id, (null == volume ? "无结果" : "成功"));
    return volume;
  }

  @Override
  public int insertVolume(Volume volume) {
    log.info("正在插入volume");
    if (super.save(volume)) {
      log.info("插入volume成功,id为{}", volume.getVolumeId());
      return volume.getVolumeId();
    } else {
      log.error("插入volume失败");
      throw new BizException("添加失败");
    }
  }

  @Override
  public int deleteVolumeById(int id) {
    log.info("正在删除id为{}的volume", id);
    if (super.removeById(id)) {
      log.info("删除id为{}的volume成功", id);
      return id;
    } else {
      log.error("删除id为{}的volume失败", id);
      throw new BizException("删除失败[id=" + id + "]");
    }
  }

  @Override
  public int updateVolume(Volume volume) {
    log.info("正在更新id为{}的volume", volume.getVolumeId());
    if (super.updateById(volume)) {
      log.info("更新d为{}的volume成功", volume.getVolumeId());
      return volume.getVolumeId();
    } else {
      log.error("更新id为{}的volume失败", volume.getVolumeId());
      throw new BizException("更新失败[id=" + volume.getVolumeId() + "]");
    }
  }

  @Override
  public void init() {
    log.info("正在初始化系统");
    List<Volume> volumeList = super.list();
    if (volumeList == null || volumeList.isEmpty()) {
      // 新建卷
      Volume volume = createVolume("DISK_0");
      // 格式化(容量：1MB，块大小：4KB)
      volume.setCapacity((long) (1024));
      volume.setBlockSize(4);
      format(volume);
      // 保存到数据库
      volume.updateById();
      // 设置为当前硬盘
      currentVolume = volume;
    } else {
      // 设置第一块硬盘为当前硬盘
      currentVolume = volumeList.get(0);
      //获取FAT
      List<Fat> fats = fatMapper.selectList(new QueryWrapper<Fat>().orderByAsc("fat_id"));
      currentVolume.setFat(new ArrayList<>(fats));
    }
    log.info("初始化完成！");
  }

  @Override
  public Volume createVolume(String volumeLabel) {
    log.info("正在新建卷：{}", volumeLabel);
    Volume volume = new Volume();
    volume.setVolumeLabel(volumeLabel);
    super.save(volume);
    log.info("新建卷成功：{}", volumeLabel);
    return volume;
  }

  @Override
  public Volume format(Volume volume) {
    log.info("正在格式化磁盘");

    // 计算块数量
    long blockNumber = volume.getCapacity() / volume.getBlockSize();

    // 创建FAT
    ArrayList<Fat> FATArray = new ArrayList<>((int) blockNumber);
    for (int i = 0; i < blockNumber; i++) {
      // 创建硬盘块
      Block block = new Block();
      block.setBlockId(i);
      block.setAddress(i * volume.getBlockSize() * 1024);
      block.insert();
      log.info("创建硬盘块成功：{}",i);
      // 创建FAT表项
      Fat fat = new Fat();
      fat.setFatId(i);
      fat.insert();
      FATArray.add(i, fat);
    }

    // 创建根目录
    File file = fileService.create(File.ROOT, null, File.DIR, null);
    volume.setRootDir(file.getDirentId());

    // 保存FAT
    volume.setFat(FATArray);

    log.info("格式化完成！");
    return volume;
  }

  @Override
  public String getFileData(Integer inodeId) {
    // 获取索引节点
    Inode inode = inodeMapper.selectById(inodeId);

    // 获取FAT第一项
    ArrayList<Fat> FATArray = currentVolume.getFat();
    if (inode.getAddress() == null) {
      return "";
    }
    Fat fat = FATArray.get(inode.getAddress());
    Block block = blockMapper.selectById(inode.getAddress());

    // 获取数据
    String data = block.getData();

    // 遍历FAT显式链接
    while (fat.getNextId() != Fat.EOF) {
      // 下个数据块
      block = blockMapper.selectById(fat.getNextId());
      data += block.getData();
      fat = FATArray.get(fat.getNextId());
    }
    return data;
  }

  @Override
  public void update(Integer inodeId, String data) {
    // 删除旧数据
    delete(inodeId);
    // 重新写入
    write(inodeId, data);
  }

  /** 获取磁盘剩余空间 */
  private void caculateFreeCapacity() {
    // 获取FAT第一项
    ArrayList<Fat> FATArray = currentVolume.getFat();

    long free = 0;
    long used = 0;

    for (int i = 0; i < FATArray.size(); i++) {
      Fat fat = FATArray.get(i);
      if (fat.getStatus() == Fat.FREE) {
        free += currentVolume.getBlockSize();
      } else {
        used += currentVolume.getBlockSize();
      }
    }

    currentVolume.setFreeCapacity(free);
    currentVolume.setUsedCapacity(used);
    saveVolume();
  }

  /** 删除文件块，释放资源 */
  @Override
  public void delete(Integer inodeId) {
    Inode inode = inodeMapper.selectById(inodeId);
    delete(inode);
  }

  @Override
  public Integer getRootDirentId() {
    return currentVolume.getRootDir();
  }

  /** 删除文件块，释放资源 */
  private void delete(Inode inode) {

    // 获取FAT第一项
    ArrayList<Fat> FATArray = currentVolume.getFat();
    if (inode.getAddress() == null) {
      return;
    }
    Fat fat = FATArray.get(inode.getAddress());

    // 标记为空闲
    fat.setStatus(Fat.FREE);

    // 遍历FAT显式链接
    while (fat.getNextId() != Fat.EOF) {
      // 下个数据块
      fat = FATArray.get(fat.getNextId());
      fat.setStatus(Fat.FREE);
    }
  }

  /** 写入文件数据 */
  private void write(Integer inodeId, String data) {

    // 获取索引节点
    Inode inode = inodeMapper.selectById(inodeId);

    // 计算每个块容纳的字符数
    int numberPerBlock = (currentVolume.getBlockSize() * 1024) / 2;

    // 检查剩余空间大小
    caculateFreeCapacity();
    if (((data.length() / numberPerBlock) * currentVolume.getBlockSize())
        > currentVolume.getFreeCapacity()) {
      throw new BizException("剩余空间不足，无法写入");
    }

    Fat fat = getFirstFreeBlock();
    // 写入磁盘
    while (true) {
      if (fat == null) {
        throw new BizException("存储空间不足，写入失败");
      }
      // 写入空闲块
      fat.setStatus(Fat.FULL);
      Block block = blockMapper.selectById(fat.getFatId());
      String blockData;
      if (data.length() > numberPerBlock) {
        blockData = data.substring(0, numberPerBlock);
        data = data.substring(numberPerBlock);
      } else {
        blockData = data;
        data = "";
      }
      block.setData(blockData);
      block.updateById();

      // 获取新的fat块
      if (data.length() > 0) {
        Fat tmp = getFirstFreeBlock();
        fat.setNextId(tmp.getFatId());
        fat.updateById();
        fat = tmp;
        // 先默认是最后一块
        fat.setNextId(Fat.EOF);
      } else {
        fat.updateById();
        break;
      }
    }
  }

  /** 定期保存FAT */
  @Scheduled(cron = "0/15 * * * * ? *")
  private void saveVolume() {
    log.info("保存磁盘信息");
    currentVolume.updateById();
  }

  /** 获取第一个空闲的FAT项 */
  private Fat getFirstFreeBlock() {
    // 获取FAT
    ArrayList<Fat> FATArray = currentVolume.getFat();
    for (int i = 0; i < FATArray.size(); i++) {
      Fat fat = FATArray.get(i);
      if (fat.getStatus() == Fat.FREE) {
        return fat;
      }
    }
    return null;
  }
}
