package com.sky.user.domain.dto;

import lombok.Data;

/**
 * 员工修改密码
 */
@Data
public class EditPasswordDTO {
    //员工id
    Long empId;
    //原密码
    String newPassword;
    //新密码
    String oldPassword;
}
