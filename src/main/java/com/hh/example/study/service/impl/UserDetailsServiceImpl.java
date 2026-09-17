package com.hh.example.study.service.impl;

import com.hh.example.study.entity.User;
import com.hh.example.study.repository.UserRepository;
import com.hh.example.study.service.PermissionService;
import com.hh.example.study.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        if (Boolean.FALSE.equals(user.getStatus())) {
            throw new UsernameNotFoundException("账户已被禁用");
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        List<Long> roleIds = roleService.findRoleIdsByUserId(user.getId());
        for (Long roleId : roleIds) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleId));
        }

        Set<String> permCodes = permissionService.findCodesByUserId(user.getId());
        for (String code : permCodes) {
            authorities.add(new SimpleGrantedAuthority(code));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
