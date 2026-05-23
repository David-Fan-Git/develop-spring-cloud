package com.develop.mvp.pk.module.bpm.application.definition;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.bpm.domain.definition.BpmCategory;
import com.develop.mvp.pk.module.bpm.domain.definition.repository.BpmCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BpmCategoryApplicationService {
    private final BpmCategoryRepository repo;

    @Transactional public Long create(String name, String code, Integer status, Integer sort) {
        var c = BpmCategory.of(null, name).code(code).status(status).sort(sort); repo.save(c); return c.id(); }
    @Transactional public void update(Long id, String name, String code, Integer status, Integer sort) {
        repo.save(BpmCategory.of(id, name).code(code).status(status).sort(sort)); }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public BpmCategory get(Long id) { return repo.findById(id); }
    public List<BpmCategory> getList() { return repo.findAll(); }
    public PageResult<BpmCategory> getPage(String name, Integer status, Integer pageNo, Integer pageSize) {
        return repo.findPage(name, status, pageNo, pageSize); }
}
