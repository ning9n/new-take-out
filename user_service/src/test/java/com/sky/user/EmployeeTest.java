package com.sky.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.user.controller.EmployeeController;
import com.sky.user.domain.dto.EmployeeUpdateDTO;
import com.sky.user.domain.vo.EmployeeLoginVO;
import com.sky.user.domain.dto.EditPasswordDTO;
import com.sky.user.domain.dto.EmployeeInsertDTO;
import com.sky.user.domain.dto.EmployeeLoginDTO;
import com.sky.user.domain.po.Employee;
import com.sky.user.service.EmployeeService;
import common.result.SkyResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Objects;

@SpringBootTest
@Slf4j
public class EmployeeTest {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeController controller;

    /**
     * 对商家账号进行简单测试
     */
    @Test
    public void MerchantTest() {
        //员工登录
        Employee merchant = employeeService.getById(133523);
        EmployeeLoginDTO loginDTO=EmployeeLoginDTO.builder().
                username(merchant.getUsername())
                .password(merchant.getPassword())
                .build();
        SkyResult<EmployeeLoginVO> vo=controller.login(loginDTO);
        try {
            //新增员工
            EmployeeInsertDTO employeeInsertDTO = EmployeeInsertDTO.builder()
                    .idNumber("2131223")
                    .name("employee")
                    .phone("12232342311")
                    .sex("女")
                    .username("username")
                    .build();
            controller.insert(employeeInsertDTO);
            Long id=employeeService.lambdaQuery().eq(Employee::getUsername,"username").one().getId();
            //更新员工数据
            controller.update(EmployeeUpdateDTO.builder()
                    .id(id)
                    .idNumber("1234213")
                    .name("employ")
                    .phone("1222342311")
                    .sex("女")
                    .username("username")
                    .build());
            log.info(controller.getById(id).getData().getName());
            assert controller.getById(id).getData().getName().equals("employ");
            //修改密码
            controller.editPassword(EditPasswordDTO.builder()
                    .empId(id)
                    .oldPassword("123456")
                    .newPassword("12345892")
                    .build());
            assert Objects.equals(employeeService.getById(id).getPassword(), "12345892");
            //根据id查询
            assert controller.getById(id).getData() != null;
            //分页查询
            assert controller.page("", 1, 2).getData() != null;
            //禁用员工账号
            controller.status(0, id);
            assert employeeService.getById(id).getStatus().equals(0);
        } finally {
            //删除测试数据
            employeeService.remove(new LambdaQueryWrapper<Employee>().eq(Employee::getUsername,"username"));
            //退出登录
            controller.logout();
        }
    }


}
