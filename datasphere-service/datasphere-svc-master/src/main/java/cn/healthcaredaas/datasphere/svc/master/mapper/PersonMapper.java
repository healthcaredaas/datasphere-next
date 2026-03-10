package cn.healthcaredaas.datasphere.svc.master.mapper;

import cn.healthcaredaas.datasphere.svc.master.entity.Person;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 人员信息 Mapper
 *
 * @author chenpan
 */
@Mapper
public interface PersonMapper extends BaseMapper<Person> {
}
