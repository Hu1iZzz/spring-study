package com.hh.example.study.repository;

import com.hh.example.study.entity.DictData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictDataRepository extends JpaRepository<DictData, Long> {
    List<DictData> findByTypeId(Long typeId);
    List<DictData> findByTypeIdAndStatusTrueOrderBySortOrderAsc(Long typeId);
}