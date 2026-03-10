package cn.healthcaredaas.datasphere.svc.master.service;

import cn.healthcaredaas.datasphere.svc.master.entity.Person;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 人员信息服务接口
 *
 * @author chenpan
 */
public interface PersonService extends IService<Person> {

    IPage<Person> pageQuery(IPage<Person> page, Person params);
}
