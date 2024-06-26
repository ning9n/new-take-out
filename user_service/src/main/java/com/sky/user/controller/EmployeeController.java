package com.sky.user.controller;


import com.sky.user.domain.dto.EmployeeUpdateDTO;
import com.sky.user.domain.vo.EmployeeLoginVO;
import com.sky.user.domain.vo.EmployeeVO;
import com.sky.user.domain.dto.EditPasswordDTO;
import com.sky.user.domain.dto.EmployeeInsertDTO;
import com.sky.user.domain.dto.EmployeeLoginDTO;
import com.sky.user.domain.po.Employee;
import com.sky.user.service.EmployeeService;
import common.result.PageResult;
import common.result.SkyResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商家端
 */
@Api( "员工接口")
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    /**
     * 员工登录
     * @param loginDTO 必须：账号、密码
     * @return 非必须：id、姓名、用户名、token
     */
    @ApiOperation( "员工登录")
    @PostMapping("login")
    public SkyResult<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO loginDTO){
        return SkyResult.success(employeeService.login(loginDTO));
    }

    /**
     * 退出登录
     * @return 是否成功退出登录
     */
    @ApiOperation("员工退出登录")
    @PostMapping("logout")
    public SkyResult<String> logout(){
        employeeService.logout();
        return SkyResult.success();
    }

    /**
     * 修改密码
     * @param dto id、旧密码、新密码
     */
    @ApiOperation("员工修改密码")
    @PutMapping("editPassword")
    public SkyResult<String> editPassword(@RequestBody EditPasswordDTO dto){
        employeeService.editPassword(dto);
        return SkyResult.success();
    }

    /**
     * 启用、禁用员工账号
     * @param status 启用、禁用
     */
    @ApiOperation("启用、禁用员工账号")
    @PostMapping("status/{status}")
    public SkyResult<String> status(@PathVariable Integer status,Long id){
        employeeService.status(status,id);
        return SkyResult.success();
    }

    /**
     * 分页查询
     * @param name 名字
     * @param page 页码
     * @param pageSize 每页记录数
     */
    @ApiOperation( "分页查询")
    @GetMapping("page")
    public SkyResult<PageResult<Employee>> page(String name, Integer page, Integer pageSize){
        return SkyResult.success(employeeService.getPage(name,page,pageSize));
    }

    /**
     * 新增员工
     * @param dto 员工数据：身份证号、姓名、电话号码、性别、用户名、员工权限、密码
     * @return 成功/失败
     */
    @ApiOperation("新增员工")
    @PostMapping
    public SkyResult<String> insert(@RequestBody EmployeeInsertDTO dto){
        employeeService.insertEmployee(dto);
        return SkyResult.success();
    }

    /**
     * 根据id查询
     * @param id id
     * @return 查询结果
     */
    @ApiOperation( "根据id查询")
    @GetMapping("{id}")
    public SkyResult<EmployeeVO> getById(@PathVariable Long id){
        return SkyResult.success(employeeService.getEmployeeById(id));
    }
    /**
     * 更新员工
     * @param dto 员工数据：身份证号、姓名、电话号码、性别、用户名、员工权限、密码
     * @return 成功/失败
     */
    @ApiOperation("更新员工数据")
    @PutMapping
    public SkyResult<String> update(@RequestBody EmployeeUpdateDTO dto){
        employeeService.updateEmployee(dto);
        return SkyResult.success();
    }
}
