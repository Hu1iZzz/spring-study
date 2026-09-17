package com.hh.example.study.service;

import com.hh.example.study.entity.DictType;
import com.hh.example.study.entity.DictData;

import java.util.List;

public interface DictService {
    List<DictType> findAllTypes();
    DictType findTypeById(Long id);
    DictType createType(DictType type);
    DictType updateType(Long id, DictType type);
    void deleteType(Long id);

    List<DictData> findDataByTypeId(Long typeId);
    DictData createData(DictData data);
    DictData updateData(Long id, DictData data);
    void deleteData(Long id);
}