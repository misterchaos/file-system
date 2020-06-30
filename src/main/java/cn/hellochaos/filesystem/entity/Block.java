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
public class Block extends Model<Block> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "block_id", type = IdType.INPUT)
    private Integer blockId;

    /**
     * 物理地址
     */
    private Integer address;

    /**
     * 数据
     */
    private String data;


    @Override
    protected Serializable pkVal() {
        return this.blockId;
    }

}
