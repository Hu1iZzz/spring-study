package com.hh.example.study.controller;

import com.hh.example.study.entity.SysLog;
import com.hh.example.study.service.SysLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class SysLogController {

    private final SysLogService sysLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('log:view')")
    public Page<SysLog> list(@PageableDefault(size = 20) Pageable pageable) {
        return sysLogService.findAll(pageable);
    }
}
