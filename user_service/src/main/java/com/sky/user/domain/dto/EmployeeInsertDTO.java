package com.sky.user.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeInsertDTO {
    private String idNumber;
    private String name;
    private String phone;
    private String sex;
    private String username;
    private String permission;
    private String password;
}
