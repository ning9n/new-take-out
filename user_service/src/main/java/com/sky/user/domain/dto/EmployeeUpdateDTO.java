package com.sky.user.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeUpdateDTO {
    Long id;
    String idNumber;
    String name;
    String phone;
    String sex;
    String username;
    String permission;
}
