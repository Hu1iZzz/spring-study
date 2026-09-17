package com.hh.example.study.controller;

import com.hh.example.study.entity.SysConfig;
import com.hh.example.study.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configs")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @GetMapping
    @PreAuthorize("hasAuthority('config:view')")
    public List<SysConfig> list() {
        return sysConfigService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('config:view')")
    public SysConfig get(@PathVariable Long id) {
        return sysConfigService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('config:add')")
    public SysConfig create(@RequestBody SysConfig config) {
        return sysConfigService.create(config);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('config:edit')")
    public SysConfig update(@PathVariable Long id, @RequestBody SysConfig config) {
        return sysConfigService.update(id, config);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('config:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sysConfigService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
