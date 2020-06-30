package cn.hellochaos.filesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString
public class Fat extends Model<Fat> {
  private static final long serialVersionUID = 1L;
  /**
   * 主键
   */
  @TableId(value = "fat_id", type = IdType.INPUT)
  int fatId;
  int nextId = EOF;
  int status = FREE;
  public static final int EOF = -1;
  public static final int FREE = 0;
  public static final int FULL = 1;
  @Override
  protected Serializable pkVal() {
    return this.fatId;
  }
}
