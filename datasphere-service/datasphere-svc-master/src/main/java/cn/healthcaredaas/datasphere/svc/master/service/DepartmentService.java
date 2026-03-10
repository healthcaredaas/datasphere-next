package cn.healthcaredaas.datasphere.svc.master.service;

import cn.healthcaredaas.datasphere.svc.master.entity.Department;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 科室服务接口
 *
 * @author chenpan
 */
public interface DepartmentService extends IService<Department> {

    IPage<Department> pageQuery(IPage<Department> page, Department params);
}
