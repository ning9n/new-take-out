package com.sky.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.result.PageResult;
import com.sky.user.domain.VO.EmployeeLoginVO;
import com.sky.user.domain.VO.EmployeeVO;
import com.sky.user.domain.dto.EditPasswordDTO;
import com.sky.user.domain.dto.EmployeeDTO;
import com.sky.user.domain.dto.EmployeeLoginDTO;
import com.sky.user.domain.po.Employee;

public interface EmployeeService extends IService<Employee> {
    /**
     * 员工登录
     * @param loginDTO
     * @return
     */
    EmployeeLoginVO login(EmployeeLoginDTO loginDTO);
    /**
     * 退出登录
     * @return 是否成功退出登录
     */
    void logout();
    /**
     * 修改密码
     * @param dto id、旧密码、新密码
     */
    void editPassword(EditPasswordDTO dto);
    /**
     * 启用、禁用员工账号
     *
     * @param status 启用、禁用
     * @param id
     */
    void status(Integer status, Long id);

    PageResult<Employee> getPage(String name, Integer page, Integer pageSize);

    void insertEmployee(EmployeeDTO dto);

    EmployeeVO getEmployeeById(Long id);

    void updateEmployee(EmployeeDTO dto);
}
