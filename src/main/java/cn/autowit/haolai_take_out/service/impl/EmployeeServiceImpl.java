package cn.autowit.haolai_take_out.service.impl;

import cn.autowit.haolai_take_out.entity.Employee;
import cn.autowit.haolai_take_out.mapper.EmployeeMapper;
import cn.autowit.haolai_take_out.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
