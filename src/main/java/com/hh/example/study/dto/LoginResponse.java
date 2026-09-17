package com.hh.example.study.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Integer code;
    private String message;
    private UserDTO data;

    public LoginResponse() {}

    public LoginResponse(Integer code, String message, UserDTO data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static LoginResponse success(UserDTO userDTO) {
        return new LoginResponse(200, "登录成功", userDTO);
    }

    public static LoginResponse fail(String message) {
        return new LoginResponse(401, message, null);
    }
}