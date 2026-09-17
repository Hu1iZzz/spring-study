package com.hh.example.study.service.impl;

import com.hh.example.study.entity.DictData;
import com.hh.example.study.entity.DictType;
import com.hh.example.study.repository.DictDataRepository;
import com.hh.example.study.repository.DictTypeRepository;
import com.hh.example.study.service.DictService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DictServiceImpl implements DictService {

    private final DictTypeRepository dictTypeRepository;
    private final DictDataRepository dictDataRepository;

    @Override
    public List<DictType> findAllTypes() {
        return dictTypeRepository.findAll();
    }

    @Override
    public DictType findTypeById(Long id) {
        return dictTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("字典类型不存在"));
    }

    @Override
    @Transactional
    public DictType createType(DictType type) {
        if (dictTypeRepository.existsByCode(type.getCode())) {
            throw new RuntimeException("字典编码已存在");
        }
        if (type.getStatus() == null) type.setStatus(true);
        return dictTypeRepository.save(type);
    }

    @Override
    @Transactional
    public DictType updateType(Long id, DictType type) {
        DictType existing = findTypeById(id);
        existing.setName(type.getName());
        existing.setStatus(type.getStatus());
        return dictTypeRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteType(Long id) {
        dictDataRepository.findByTypeId(id).forEach(dictDataRepository::delete);
        dictTypeRepository.deleteById(id);
    }

    @Override
    public List<DictData> findDataByTypeId(Long typeId) {
        return dictDataRepository.findByTypeId(typeId);
    }

    @Override
    @Transactional
    public DictData createData(DictData data) {
        if (data.getSortOrder() == null) data.setSortOrder(0);
        if (data.getStatus() == null) data.setStatus(true);
        return dictDataRepository.save(data);
    }

    @Override
    @Transactional
    public DictData updateData(Long id, DictData data) {
        DictData existing = dictDataRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("字典数据不存在"));
        existing.setLabel(data.getLabel());
        existing.setValue(data.getValue());
        existing.setSortOrder(data.getSortOrder());
        existing.setStatus(data.getStatus());
        return dictDataRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteData(Long id) {
        dictDataRepository.deleteById(id);
    }
}