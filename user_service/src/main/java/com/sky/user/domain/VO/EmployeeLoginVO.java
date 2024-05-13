package com.sky.user.domain.VO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeLoginVO {
    Long id;
    String name;
    String token;
    String userName;
}
