package com.sky.user.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 员工修改密码
 */
@Data
@Builder
public class EditPasswordDTO {
    //员工id
    Long empId;
    //原密码
    String newPassword;
    //新密码
    String oldPassword;
}
