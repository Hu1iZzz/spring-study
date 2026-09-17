package com.hh.example.study.service;


import com.hh.example.study.dto.UserCreateDTO;
import com.hh.example.study.dto.UserDTO;
import com.hh.example.study.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    /**
     * 创建用户
     */
    User createUser(UserCreateDTO createDTO);

    /**
     * 更新用户
     */
    User updateUser(Long id, UserDTO updateDTO);

    /**
     * 根据ID查询用户
     */
    Optional<User> findById(Long id);

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 分页查询所有用户
     */
    Page<User> findAll(Pageable pageable);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 禁用用户
     */
    void disableUser(Long id);

    /**
     * 启用用户
     */
    void enableUser(Long id);
}