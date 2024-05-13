package com.sky.user.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
public class Employee implements Serializable {
    //id
    private Long id;
    //姓名
    private String name;
    //用户名
    private String username;
    //密码
    private String password;

    private String phone;

    private String sex;

    private String idNumber;

    private Integer status;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;

    //权限 商家、管理员、员工
    private String permission;
    //所属商家
    private Long merchantId;
}
