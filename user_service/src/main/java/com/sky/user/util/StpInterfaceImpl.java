package com.sky.user.util;

import cn.dev33.satoken.stp.StpInterface;
import com.sky.user.service.EmployeeService;
import common.constant.LoginType;
import common.constant.Permission;
import common.constant.Role;
import common.exception.UnknownException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {
    private final EmployeeService employeeService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if(Objects.equals(loginType, LoginType.RIDER)||Objects.equals(loginType, LoginType.USER)){
            return null;
        }
        String permission=employeeService.getById(Long.parseLong((String) loginId)).getPermission();
        List<String> list = new ArrayList<>();
        list.add(Permission.EMPLOYEE);
        if(!Objects.equals(permission, Permission.EMPLOYEE)){
            list.add(Permission.ADMIN);
        }
        if(!Objects.equals(permission, Permission.ADMIN)){
            list.add(Permission.MERCHANT);
        }
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     * 角色标志：
     * merchant：商家，具有所有权限，
     * admin:管理员，具有部分权限，
     * employee：普通员工，只有基本权限
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if(LoginType.EMPLOYEE.equals(loginType)){
            List<String> list = new ArrayList<>();
            list.add(Role.EMPLOYEE);
            return list;
        }
        else if(LoginType.USER.equals(loginType)){
            List<String> list = new ArrayList<>();
            list.add(Role.USER);
            return list;
        }
        else if(LoginType.RIDER.equals(loginType)){
            List<String> list = new ArrayList<>();
            list.add(Role.RIDER);
            return list;
        }
        else {
           throw new UnknownException();
        }
    }

}
