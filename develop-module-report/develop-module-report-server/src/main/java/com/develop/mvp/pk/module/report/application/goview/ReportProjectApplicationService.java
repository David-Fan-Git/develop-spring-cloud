package com.develop.mvp.pk.module.report.application.goview;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.report.domain.goview.ReportProject;
import com.develop.mvp.pk.module.report.domain.goview.repository.ReportProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class ReportProjectApplicationService {
    private final ReportProjectRepository repo;
    @Transactional public Long create(String name, Integer status, String remark) { var p = ReportProject.of(null, name).status(status).remark(remark); repo.save(p); return p.id(); }
    @Transactional public void update(Long id, String name, Integer status, String remark) { repo.save(ReportProject.of(id, name).status(status).remark(remark)); }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public ReportProject get(Long id) { return repo.findById(id); }
    public List<ReportProject> getList() { return repo.findAll(); }
    public PageResult<ReportProject> getPage(String name, Integer status, Integer pageNo, Integer pageSize) { return repo.findPage(name, status, pageNo, pageSize); }
}
