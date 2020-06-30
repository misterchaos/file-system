package cn.hellochaos.filesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Inode extends Model<Inode> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "inode_id", type = IdType.AUTO)
    private Integer inodeId;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 物理地址（第一个FAT表项的序号）
     */
    private Integer address;

    /**
     * 文件长度（占用文件块的数量）
     */
    private Integer length;

    /**
     * 所有者id
     */
    private Integer ownId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 上次修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 共享计数器
     */
    private Integer shareCount;


    @Override
    protected Serializable pkVal() {
        return this.inodeId;
    }

}
