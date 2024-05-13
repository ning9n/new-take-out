package com.sky.user.service.Impl;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.user.domain.dto.EmployeeInsertDTO;
import com.sky.user.domain.dto.EmployeeUpdateDTO;
import common.stp.util.StpEmployeeUtil;
import com.sky.user.domain.dto.EditPasswordDTO;
import com.sky.user.domain.dto.EmployeeLoginDTO;
import com.sky.user.domain.po.Employee;
import com.sky.user.domain.vo.EmployeeLoginVO;
import com.sky.user.domain.vo.EmployeeVO;
import com.sky.user.mapper.EmployeeMapper;
import com.sky.user.service.EmployeeService;
import common.constant.Permission;
import common.exception.LllegalDataException;
import common.exception.NoAuthorityException;
import common.exception.UserNotFoundException;
import common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static common.stp.util.StpEmployeeUtil.getLoginId;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    private final EmployeeMapper employeeMapper;
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
        //如果已经登录，原设备强制注销
        //登录

        StpEmployeeUtil.login(employee.getId());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        String token=tokenInfo.tokenValue;
        return EmployeeLoginVO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .userName(employee.getUsername())
                .token(token)
                .build();
    }

    /**
     * 退出登录
     * 需要判断员工登录类型
     */
    @Override
    public void logout() {
        StpEmployeeUtil.checkLogin();
        StpEmployeeUtil.logout();
    }

    @Override
    public void editPassword(EditPasswordDTO dto) {
        //1.数据合法性检验
        //密码要求不小于8位
        if(dto.getOldPassword()==null||dto.getNewPassword()==null||dto.getNewPassword().length()<8) {
            throw new LllegalDataException();
        }
        //如果是商家账号，员工是否属于该商家
        if(StpEmployeeUtil.hasPermission(Permission.MERCHANT)) {
            checkSameMerchantForUsers(dto.getEmpId());
        }
        //如果不是，是否为本人账号
        else {
            Long id = Long.parseLong((String)getLoginId());
            if (!Objects.equals(id, dto.getEmpId())) {
                throw new LllegalDataException();
            }
        }

        //2.查询用户是否存在，旧密码是否正确
        Employee employee=lambdaQuery()
                .eq(Employee::getId,dto.getEmpId())
                .eq(Employee::getPassword,dto.getOldPassword())
                .one();
        if(employee==null||!employee.getPassword().equals(dto.getOldPassword())) {
            throw new UserNotFoundException();
        }

        //3.修改
        lambdaUpdate()
                .eq(Employee::getId,dto.getEmpId())
                .set(Employee::getPassword,dto.getNewPassword())
                .set(Employee::getUpdateUser,Long.parseLong((String)getLoginId()))
                .set(Employee::getUpdateTime,LocalDateTime.now())
                .update();
    }

    @SaCheckPermission(Permission.ADMIN)
    @Override
    public void status(Integer status, Long id) {
        //员工具有相应权限
        //员工存在且属于同一商家
        checkSameMerchantForUsers(id);
        lambdaUpdate()
                .eq(Employee::getId,id)
                .set(Employee::getStatus,status)
                .set(Employee::getUpdateUser,Long.parseLong((String)getLoginId()))
                .set(Employee::getUpdateTime,LocalDateTime.now())
                .update();
    }
    @Override
    public PageResult<Employee> getPage(String name, Integer page, Integer pageSize) {
        //查询当前商家的员工
        Long id=Long.parseLong((String) StpEmployeeUtil.getLoginId());
        Employee employee=lambdaQuery().eq(Employee::getId,id).one();
        LambdaQueryWrapper<Employee> wrapper=new LambdaQueryWrapper<Employee>()
                .eq(Employee::getMerchantId,employee.getMerchantId())
                .like(name!=null&&!name.isEmpty(),Employee::getName,name);
        Page<Employee> p=page(new Page<>(page,pageSize),wrapper);
        return new PageResult<>(p.getTotal(), p.getRecords());
    }
    @SaCheckPermission(Permission.MERCHANT)
    @Override
    public void insertEmployee(EmployeeInsertDTO dto) {
        Employee employee=BeanUtil.copyProperties(dto,Employee.class);
        System.out.println(employee);
        System.out.println(dto);
        Long id=Long.parseLong((String) StpEmployeeUtil.getLoginId());
        Employee admin=lambdaQuery().eq(Employee::getId,id).one();
        employee.setMerchantId(admin.getMerchantId());
        employee.setCreateUser(id);
        employee.setUpdateUser(id);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        save(employee);
    }



    @Override
    public EmployeeVO getEmployeeById(Long id) {

        Employee employee=checkSameMerchantForUsers(id);
        return BeanUtil.copyProperties(employee,EmployeeVO.class);
    }

    @SaCheckPermission(Permission.ADMIN)
    @Override
    public void updateEmployee(EmployeeUpdateDTO dto) {
        if(!StpEmployeeUtil.hasPermission(Permission.ADMIN)){
            Long id=Long.parseLong((String) StpEmployeeUtil.getLoginId());
            if(!Objects.equals(dto.getId(), id)) {
                throw new NoAuthorityException();
            }
        }
        lambdaUpdate()
                .eq(Employee::getId,dto.getId())
                .set(Employee::getIdNumber,dto.getIdNumber())
                .set(Employee::getName,dto.getName())
                .set(Employee::getPhone,dto.getPhone())
                .set(Employee::getSex,dto.getSex())
                .set(Employee::getUsername,dto.getUsername())
                .set(Employee::getUpdateUser,Long.parseLong((String)getLoginId()))
                .set(Employee::getUpdateTime,LocalDateTime.now())
                .update();
    }



    /**
     * 检测当前用户和要操作的用户是否属于同一商家
     * 不是抛出异常
     * @param id 用户id
     * @return 当前用户
     */
    @SaCheckPermission(Permission.EMPLOYEE)
    private Employee checkSameMerchantForUsers(Long id) {
        Long adminId=Long.parseLong((String) StpEmployeeUtil.getLoginId());
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
