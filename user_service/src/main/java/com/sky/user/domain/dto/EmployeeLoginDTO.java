package com.sky.user.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 员工登录
 */
@Data
@Builder

public class EmployeeLoginDTO {
    String username;
    String password;
}
