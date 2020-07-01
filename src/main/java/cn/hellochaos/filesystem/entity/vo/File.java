package cn.hellochaos.filesystem.entity.vo;

import cn.hellochaos.filesystem.entity.Dirent;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Yuchao-Huang
 * @since 2020-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class File extends Dirent {

  /** 文件类型 */
  private String type;

  /** 物理地址（第一个FAT表项的序号） */
  private Integer address;

  /** 文件长度（占用文件块的数量） */
  private Integer length;

  /** 所有者id */
  private Integer ownId;

  /** 创建时间 */
  private LocalDateTime createTime;

  /** 上次修改时间 */
  private LocalDateTime updateTime;

  /** 共享计数器 */
  private int shareCount;

  /** 文件数据 */
  private String data;

  /** 目录的子文件或者子目录 */
  private List<File> files;

  /**
   * 所有者
   */
  @TableField(exist = false,select = false)
  private String owner;


  /**
   * 目录类型文件
   */
  public static final String DIR = "dir";
  /**
   * 根目录
   */
  public static final String ROOT = "\\";

  /**
   * txt格式文件
   */
  public static final String TXT = "txt";


  public static final String SUPER_DIR = "..";

  @Override
  public String toString() {
    return "File{" +
            "filename="+super.getFilename()+
            "type='" + type + '\'' +
            ", address=" + address +
            ", length=" + length +
            ", ownId=" + ownId +
            ", createTime=" + createTime +
            ", updateTime=" + updateTime +
            ", shareCount=" + shareCount +
            ", data='" + data + '\'' +
            ", files=" + files +
            '}';
  }
}
