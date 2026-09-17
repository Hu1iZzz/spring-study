package com.hh.example.study.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link com.hh.example.study.entity.User}
 */
@Data
@NoArgsConstructor
public class UserCreateDTO implements Serializable {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")

    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 255, message = "密码长度至少6位")
    private String password;

    private String email;

    private String phone;

    private String nickname;

    private String avatar;

    private Boolean gender;

    private Integer age;

    private Boolean status;

    private Instant lastLoginTime;

    private String lastLoginIp;

    private Instant createTime;

    private Instant updateTime;

}