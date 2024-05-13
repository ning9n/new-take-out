package com.sky.user.service.Impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import common.enumeration.LoginType;
import common.enumeration.Role;
import common.exception.LllegalDataException;
import common.exception.NoAuthorityException;
import common.exception.UserNotFoundException;
import common.result.PageResult;
import com.sky.user.domain.VO.EmployeeLoginVO;
import com.sky.user.domain.VO.EmployeeVO;
import com.sky.user.domain.dto.EditPasswordDTO;
import com.sky.user.domain.dto.EmployeeDTO;
import com.sky.user.domain.dto.EmployeeLoginDTO;
import com.sky.user.domain.po.Employee;
import com.sky.user.mapper.EmployeeMapper;
import com.sky.user.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    EmployeeMapper employeeMapper;
    @Override
    public EmployeeLoginVO login(EmployeeLoginDTO loginDTO) {
        //数据合法性检验
        if(loginDTO.getUsername()==null||loginDTO.getUsername().isEmpty()
                ||loginDTO.getPassword()==null||loginDTO.getPassword().isEmpty()){
            throw new LllegalDataException("传输数据不合法");
        }
        //查找
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<Employee>().eq(Employee::getUsername,loginDTO.getUsername())
                .eq(Employee::getPassword,loginDTO.getPassword());
        Employee employee=getOne(wrapper);
        //用户不存在抛出异常
        if(employee==null) {
            throw new UserNotFoundException("用户不存在");
        }
        //TODO 权限认证  登录状态检测 登录优化
        //登录
        StpUtil.login(employee.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        String token=tokenInfo.tokenValue;
        return EmployeeLoginVO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .userName(employee.getUsername())
                .token(token)
                .build();
    }

    @Override
    public void logout() {
        StpUtil.checkLogin();
        StpUtil.logout();
    }

    @Override
    public void editPassword(EditPasswordDTO dto) {
        //1.数据合法性检验
        //密码要求不小于8位
        if(dto.getOldPassword()==null||dto.getNewPassword()==null||dto.getNewPassword().length()<8) {
            throw new LllegalDataException();
        }
        //是否为当前用户
        Long id=(Long)StpUtil.getLoginId();
        if(!Objects.equals(id, dto.getEmpId())){
            throw new LllegalDataException();
        }
        //2.查询用户是否存在，旧密码是否正确
        Employee employee=lambdaQuery()
                .eq(Employee::getId,dto.getEmpId())
                .eq(Employee::getPassword,dto.getOldPassword())
                .one();
        if(employee==null) {
            throw new UserNotFoundException();
        }
        //3.修改
        lambdaUpdate()
                .eq(Employee::getId,dto.getEmpId())
                .set(Employee::getPassword,dto.getNewPassword())
                .update();
    }

    @Override
    public void status(Integer status, Long id) {
        //员工存在且属于同一商家
        checkSameMerchantForUsers(id);
        //员工具有相应权限
        StpUtil.checkRoleOr(Role.EMPLOYEE,Role.MERCHANT);
        lambdaUpdate()
                .eq(Employee::getId,id)
                .set(Employee::getStatus,status)
                .update();
    }

    @Override
    public PageResult<Employee> getPage(String name, Integer page, Integer pageSize) {
        //查询当前商家的员工
        Long id=(Long)StpUtil.getLoginId();
        Employee employee=lambdaQuery().eq(Employee::getId,id).one();
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<Employee>()
                .eq(Employee::getMerchantId,employee.getMerchantId())
                .like(name!=null&&!name.isEmpty(),Employee::getName,name);
        Page<Employee> p=page(new Page<>(page,pageSize),wrapper);
        return new PageResult<>(p.getTotal(), p.getRecords());
    }

    @Override
    public void insertEmployee(EmployeeDTO dto) {
        Employee employee=BeanUtil.copyProperties(dto,Employee.class);
        Long id= (Long) StpUtil.getLoginId();
        Employee admin=lambdaQuery().eq(Employee::getId,id).one();
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        employee.setCreateUser(id);
        employee.setUpdateUser(id);
        employee.setMerchantId(admin.getMerchantId());
        //TODO type,密码作为参数传递
        employee.setType(LoginType.EMPLOYEE);
        employee.setPassword("123456");
        save(employee);
    }

    @Override
    public EmployeeVO getEmployeeById(Long id) {

        Employee employee=checkSameMerchantForUsers(id);
        return BeanUtil.copyProperties(employee,EmployeeVO.class);
    }

    @Override
    public void updateEmployee(EmployeeDTO dto) {
        if(!StpUtil.hasRole(LoginType.MERCHANT)){
            Long id=(Long) StpUtil.getLoginId();
            if(!Objects.equals(dto.getId(), id)) {
                throw new NoAuthorityException();
            }
        }
        lambdaUpdate().eq(Employee::getId,dto.getId())
                .set(Employee::getIdNumber,dto.getIdNumber())
                .set(Employee::getName,dto.getName())
                .set(Employee::getPhone,dto.getPhone())
                .set(Employee::getSex,dto.getSex())
                .set(Employee::getUsername,dto.getUserName())
                .set(Employee::getUpdateTime,LocalDateTime.now())
                .set(Employee::getUpdateUser,StpUtil.getLoginId())
                .update();
    }

    /**
     * 检测当前用户和要操作的用户是否属于同一商家
     * 不是抛出异常
     * @param id 用户id
     * @return 当前用户
     */
    private Employee checkSameMerchantForUsers(Long id) {
        Long adminId=(Long) StpUtil.getLoginId();
        Employee admin=lambdaQuery().eq(Employee::getId,adminId).one();
        Employee employee=lambdaQuery().eq(Employee::getId,id).one();
        if(admin==null||employee==null){
            throw new LllegalDataException();
        }
        if(!Objects.equals(admin.getMerchantId(), employee.getMerchantId())){
            throw new NoAuthorityException();
        }
        return employee;
    }

}
