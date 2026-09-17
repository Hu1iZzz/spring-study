package com.hh.example.study.controller;

import com.hh.example.study.entity.DictData;
import com.hh.example.study.entity.DictType;
import com.hh.example.study.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping("/types")
    @PreAuthorize("hasAuthority('dict:view')")
    public List<DictType> listTypes() {
        return dictService.findAllTypes();
    }

    @GetMapping("/types/{id}")
    @PreAuthorize("hasAuthority('dict:view')")
    public DictType getType(@PathVariable Long id) {
        return dictService.findTypeById(id);
    }

    @PostMapping("/types")
    @PreAuthorize("hasAuthority('dict:add')")
    public DictType createType(@RequestBody DictType type) {
        return dictService.createType(type);
    }

    @PutMapping("/types/{id}")
    @PreAuthorize("hasAuthority('dict:edit')")
    public DictType updateType(@PathVariable Long id, @RequestBody DictType type) {
        return dictService.updateType(id, type);
    }

    @DeleteMapping("/types/{id}")
    @PreAuthorize("hasAuthority('dict:delete')")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        dictService.deleteType(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/data/{typeId}")
    @PreAuthorize("hasAuthority('dict:view')")
    public List<DictData> listData(@PathVariable Long typeId) {
        return dictService.findDataByTypeId(typeId);
    }

    @PostMapping("/data")
    @PreAuthorize("hasAuthority('dict:add')")
    public DictData createData(@RequestBody DictData data) {
        return dictService.createData(data);
    }

    @PutMapping("/data/{id}")
    @PreAuthorize("hasAuthority('dict:edit')")
    public DictData updateData(@PathVariable Long id, @RequestBody DictData data) {
        return dictService.updateData(id, data);
    }

    @DeleteMapping("/data/{id}")
    @PreAuthorize("hasAuthority('dict:delete')")
    public ResponseEntity<Void> deleteData(@PathVariable Long id) {
        dictService.deleteData(id);
        return ResponseEntity.noContent().build();
    }
}
