package cn.hellochaos.filesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
public class Dirent extends Model<Dirent> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "dirent_id", type = IdType.AUTO)
    private Integer direntId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 父目录id
     */
    private Integer parent;

    /**
     * 索引节点id
     */
    private Integer inodeId;


    @Override
    protected Serializable pkVal() {
        return this.direntId;
    }

}
