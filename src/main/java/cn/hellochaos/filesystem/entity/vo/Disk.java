package cn.hellochaos.filesystem.entity.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Disk {

  /** 要输出的磁盘信息 */
  private String info;

  /** 已使用百分比 */
  @TableField(exist = false, select = false)
  private String percent;
}
