package cn.hellochaos.filesystem.mapper;

import cn.hellochaos.filesystem.entity.Fat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FatMapper extends BaseMapper<Fat> {}
