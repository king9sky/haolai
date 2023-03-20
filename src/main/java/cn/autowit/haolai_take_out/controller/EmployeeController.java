package cn.autowit.haolai_take_out.controller;

import cn.autowit.haolai_take_out.common.R;
import cn.autowit.haolai_take_out.entity.Employee;
import cn.autowit.haolai_take_out.service.EmployeeService;
import cn.autowit.haolai_take_out.service.impl.EmployeeServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1.密码加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.数据库查询用户名
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp=employeeService.getOne(queryWrapper);

        //3.如果没有查询到
        if(emp == null){
            return R.error("登录失败");
        }

        //4.密码比对
        if(!emp.getPassword().equals(password)){
            return R.error("用户名或密码错误");
        }

        //5.员工状态
        if(emp.getStatus() == 0){
            return R.error("当前账号已被锁定");
        }

        //6.登录成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     *
     * @param request
     * @return
     */
    @PostMapping("logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> add(HttpServletRequest request,@RequestBody Employee employee){
        log.info("增加新员工：{}",employee.toString());
        log.info("referer:{}",request.getHeader("Referer"));
        if(request.getHeader("Referer")==null || !request.getHeader("Referer").equals("http://localhost:8080/backend/page/member/add.html")){
            return R.error("无法新增员工");
        }

        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long user_id = (Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(user_id);
        employee.setUpdateUser(user_id);
        employeeService.save(employee);
        log.info("新增员工保存成功");
        return R.success("新增员工保存成功");

    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page-{},pageSize-{},name-{}",page,pageSize,name);
        //1、分页构造器
        Page pageinfo = new Page(page,pageSize);
        //2、查询
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //过滤条件
        queryWrapper.like(StringUtils.hasLength(name),Employee::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageinfo,queryWrapper);

        return R.success(pageinfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        Long emp =(Long) request.getSession().getAttribute("employee");
        log.info("用户ID:{}",emp);
        if(emp == 1){
            employee.setUpdateTime(LocalDateTime.now());
            employee.setUpdateUser(emp);
            employeeService.updateById(employee);

            return R.success("用户状态修改成功");
        }
        return R.error("用户状态修改失败");
    }
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee=employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("查询用户失败");
    }
}
