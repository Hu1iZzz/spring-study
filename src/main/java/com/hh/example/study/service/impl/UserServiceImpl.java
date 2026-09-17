package com.hh.example.study.service.impl;


import com.hh.example.study.dto.UserCreateDTO;
import com.hh.example.study.dto.UserDTO;
import com.hh.example.study.entity.User;
import com.hh.example.study.repository.UserRepository;
import com.hh.example.study.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // 注入

    @Override
    @Transactional
    public User createUser(UserCreateDTO createDTO) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(createDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(createDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setEmail(createDTO.getEmail());
        user.setPhone(createDTO.getPhone());
        user.setNickname(createDTO.getNickname());
        user.setGender(createDTO.getGender());
        user.setAge(createDTO.getAge());
        user.setStatus(true); // 默认启用

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDTO updateDTO) {
        User user = findById(id).orElseThrow(() -> new EntityNotFoundException("用户不存在"));

        if (updateDTO.getUsername() != null) {
            // 如果修改了用户名，检查新用户名是否已被其他用户使用
            if (!user.getUsername().equals(updateDTO.getUsername()) &&
                    userRepository.existsByUsername(updateDTO.getUsername())) {
                throw new RuntimeException("用户名已被占用");
            }
            user.setUsername(updateDTO.getUsername());
        }

        if (updateDTO.getEmail() != null) {
            if (!user.getEmail().equals(updateDTO.getEmail()) &&
                    userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new RuntimeException("邮箱已被占用");
            }
            user.setEmail(updateDTO.getEmail());
        }

        if (updateDTO.getPhone() != null) {
            if (user.getPhone() != null && !user.getPhone().equals(updateDTO.getPhone()) &&
                    userRepository.existsByPhone(updateDTO.getPhone())) {
                throw new RuntimeException("手机号已被占用");
            }
            user.setPhone(updateDTO.getPhone());
        }

        user.setNickname(updateDTO.getNickname());
        user.setAvatar(updateDTO.getAvatar());
        user.setGender(updateDTO.getGender());
        user.setAge(updateDTO.getAge());
        user.setStatus(updateDTO.getStatus());

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        User user = findById(id).orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        user.setStatus(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        User user = findById(id).orElseThrow(() -> new EntityNotFoundException("用户不存在"));
        user.setStatus(true);
        userRepository.save(user);
    }
}