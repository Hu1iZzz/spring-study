package com.hh.example.study.controller;

import com.hh.example.study.entity.Role;
import com.hh.example.study.entity.SysLog;
import com.hh.example.study.entity.User;
import com.hh.example.study.entity.UserRole;
import com.hh.example.study.repository.RoleRepository;
import com.hh.example.study.repository.SysLogRepository;
import com.hh.example.study.repository.UserRepository;
import com.hh.example.study.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final SysLogRepository sysLogRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/active-users")
    public Map<String, Object> activeUsers() {
        Instant end = Instant.now();
        Instant start = end.minus(30, ChronoUnit.DAYS);

        List<SysLog> logs = sysLogRepository.findByOperationAndCreateTimeBetween("LOGIN", start, end);

        List<String> dates = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            dates.add(LocalDate.now().minusDays(i).toString());
        }

        Map<String, Map<String, Set<String>>> dateRoleUsers = new HashMap<>();
        Set<String> roleNames = new LinkedHashSet<>();

        Set<String> seen = new HashSet<>();
        for (SysLog log : logs) {
            String username = log.getUsername();
            if (username == null) continue;
            String date = log.getCreateTime().atZone(ZoneId.systemDefault()).toLocalDate().toString();
            String key = username + "|" + date;
            if (seen.contains(key)) continue;
            seen.add(key);

            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) continue;
            Long userId = userOpt.get().getId();

            List<Long> roleIds = userRoleRepository.findByUserId(userId).stream()
                    .map(UserRole::getRoleId).collect(Collectors.toList());
            for (Long roleId : roleIds) {
                Optional<Role> roleOpt = roleRepository.findById(roleId);
                if (roleOpt.isEmpty()) continue;
                String roleName = roleOpt.get().getName();
                roleNames.add(roleName);
                dateRoleUsers.computeIfAbsent(date, k -> new HashMap<>())
                        .computeIfAbsent(roleName, k -> new HashSet<>())
                        .add(username);
            }
        }

        List<String> sortedRoles = new ArrayList<>(roleNames);
        List<Map<String, Object>> series = new ArrayList<>();
        for (String roleName : sortedRoles) {
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("name", roleName);
            List<Integer> data = new ArrayList<>();
            for (String date : dates) {
                Set<String> users = dateRoleUsers.getOrDefault(date, Collections.emptyMap()).get(roleName);
                data.add(users == null ? 0 : users.size());
            }
            s.put("data", data);
            series.add(s);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dates", dates);
        result.put("series", series);
        return result;
    }
}