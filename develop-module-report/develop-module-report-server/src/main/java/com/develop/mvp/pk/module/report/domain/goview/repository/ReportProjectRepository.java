package com.develop.mvp.pk.module.report.domain.goview.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.report.domain.goview.ReportProject;
import java.util.*;
public interface ReportProjectRepository {
    ReportProject save(ReportProject p); void delete(Long id);
    ReportProject findById(Long id); List<ReportProject> findAll();
    PageResult<ReportProject> findPage(String name, Integer status, Integer pageNo, Integer pageSize);
}
