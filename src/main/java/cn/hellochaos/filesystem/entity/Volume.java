package cn.hellochaos.filesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <p>
 * 
 * </p>
 *
 * @author Yuchao-Huang
 * @since 2020-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ToString
public class Volume extends Model<Volume> {

    private static final long serialVersionUID = 1L;

    /**
     * 磁盘id
     */
    @TableId(value = "volume_id", type = IdType.AUTO)
    private Integer volumeId;

    /**
     * 盘符
     */
    private String drive;

    /**
     * 卷标
     */
    private String volumeLabel;

    /**
     * 容量（单位：KB）
     */
    private Long capacity;

    /**
     * FAT表
     */
    @TableField(exist = false,select = false)
    private ArrayList<Fat> fat;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 块大小（单位：KB）
     */
    private Integer blockSize;

    /**
     * 根目录（外键：dirent_id）
     */
    private Integer rootDir;

    /**
     * 已用空间
     */
    @TableField(exist = false,select = false)
    private Long usedCapacity;

    /**
     * 空闲空间
     */
    @TableField(exist = false,select = false)
    private Long freeCapacity;




    @Override
    protected Serializable pkVal() {
        return this.volumeId;
    }

}
