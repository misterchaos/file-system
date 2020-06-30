package cn.hellochaos.filesystem.mapper;

import cn.hellochaos.filesystem.entity.Block;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
* <p>
*  Mapper 接口
* </p>
*
* @author Yuchao-Huang
* @since 2020-06-28
*/
@Mapper
@Repository
public interface BlockMapper extends BaseMapper<Block> {

}
