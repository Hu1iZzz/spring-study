package com.hh.example.study.controller;

import com.hh.example.study.entity.Asset;
import com.hh.example.study.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    @PreAuthorize("hasAuthority('asset:view')")
    public Page<Asset> list(@PageableDefault(size = 10) Pageable pageable) {
        return assetService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:view')")
    public Asset get(@PathVariable Long id) {
        return assetService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('asset:add')")
    public Asset create(@RequestBody Asset asset) {
        return assetService.create(asset);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:edit')")
    public Asset update(@PathVariable Long id, @RequestBody Asset asset) {
        return assetService.update(id, asset);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('asset:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
