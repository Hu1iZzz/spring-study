package com.hh.example.study.repository;

import com.hh.example.study.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    Optional<User> findByPhone(String phone);

    /**
     * 判断用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 判断邮箱是否存在
     */
    boolean existsByEmail(String email);

    /**
     * 判断手机号是否存在
     */
    boolean existsByPhone(String phone);
}