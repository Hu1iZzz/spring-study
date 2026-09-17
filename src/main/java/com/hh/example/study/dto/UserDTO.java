package com.hh.example.study.dto;

import com.hh.example.study.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DTO for {@link User}
 */
@Data
@NoArgsConstructor
public class UserDTO implements Serializable {

    private  Long id;

    private  String username;

    private  String email;

    private  String phone;

    private  String nickname;

    private  String avatar;

    private  Boolean gender;

    private  Integer age;

    private  Boolean status;

    private  Long departmentId;

    private  Instant lastLoginTime;

    private  String lastLoginIp;

    private  Instant createTime;

    private  Instant updateTime;

    private List<String> roles;

    private List<String> permissions;

    private List<Map<String, Object>> menus;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.avatar = user.getAvatar();
        this.gender = user.getGender();
        this.age = user.getAge();
        this.status = user.getStatus();
        this.departmentId = user.getDepartmentId();
        this.lastLoginTime = user.getLastLoginTime();
        this.lastLoginIp = user.getLastLoginIp();
        this.createTime = user.getCreateTime();
        this.updateTime = user.getUpdateTime();
    }
}