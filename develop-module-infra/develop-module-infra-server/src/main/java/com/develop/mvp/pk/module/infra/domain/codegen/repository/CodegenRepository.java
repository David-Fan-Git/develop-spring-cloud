package com.develop.mvp.pk.module.infra.domain.codegen.repository;

// DDD 角色：仓储接口，定义在领域层，不依赖任何基础设施

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenColumn;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenTable;
import com.develop.mvp.pk.module.infra.domain.codegen.valueobject.CodegenTableId;

import java.util.Collection;
import java.util.List;

public interface CodegenRepository {
    CodegenTable save(CodegenTable table);
    void delete(CodegenTableId tableId);
    void deleteByIds(Collection<CodegenTableId> tableIds);
    CodegenTable findById(CodegenTableId tableId);
    PageResult<CodegenTable> findPage(CodegenTablePageQuery query);
    List<CodegenTable> findByDataSourceConfigId(Long dataSourceConfigId);
    List<CodegenTable> findByTemplateTypeAndMasterTableId(Integer templateType, Long masterTableId);
    CodegenTable findByTableNameAndDataSourceConfigId(String tableName, Long dataSourceConfigId);

    // 列操作
    List<CodegenColumn> findColumnsByTableId(Long tableId);
    List<CodegenColumn> saveColumns(Long tableId, List<CodegenColumn> columns);
    void deleteColumnsByTableId(Long tableId);
    void deleteColumnsByIds(Collection<Long> columnIds);
}
