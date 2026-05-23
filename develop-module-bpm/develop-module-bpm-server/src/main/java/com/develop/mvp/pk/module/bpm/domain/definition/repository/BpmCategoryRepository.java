package com.develop.mvp.pk.module.bpm.domain.definition.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.bpm.domain.definition.BpmCategory;
import java.util.*;

public interface BpmCategoryRepository {
    BpmCategory save(BpmCategory c); void delete(Long id);
    BpmCategory findById(Long id); Optional<BpmCategory> findByCode(String code);
    List<BpmCategory> findAll(); List<BpmCategory> findByStatus(Integer status);
    PageResult<BpmCategory> findPage(String name, Integer status, Integer pageNo, Integer pageSize);
}
