package cn.healthcaredaas.datasphere.svc.master.mapper;

import cn.healthcaredaas.datasphere.svc.master.entity.Dictionary;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标准字典 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface DictionaryMapper extends BaseMapper<Dictionary> {
}
