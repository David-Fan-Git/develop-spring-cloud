package com.develop.mvp.pk.module.infra.application.codegen;

// DDD 角色：应用编排服务 - 代码生成

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenColumn;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenTable;
import com.develop.mvp.pk.module.infra.domain.codegen.repository.CodegenRepository;
import com.develop.mvp.pk.module.infra.domain.codegen.repository.CodegenTablePageQuery;
import com.develop.mvp.pk.module.infra.domain.codegen.valueobject.CodegenTableId;
import com.develop.mvp.pk.module.infra.domain.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.infra.enums.ErrorCodeConstants.*;

@Service
public class CodegenApplicationService {

    private final CodegenRepository codegenRepository;
    private final DomainEventPublisher eventPublisher;

    public CodegenApplicationService(CodegenRepository codegenRepository,
                                      DomainEventPublisher eventPublisher) {
        this.codegenRepository = codegenRepository;
        this.eventPublisher = eventPublisher;
    }

    // ── 命令 ──

    @Transactional
    public List<Long> createCodegenList(String author, Long dataSourceConfigId,
                                         List<String> tableNames,
                                         java.util.function.BiFunction<Long, String, ?> tableInfoProvider,
                                         java.util.function.BiFunction<String, Long, CodegenTable> tableBuilder) {
        List<Long> ids = new java.util.ArrayList<>();
        for (String tableName : tableNames) {
            CodegenTable table = tableBuilder.apply(author, tableName);
            table = codegenRepository.save(table);
            table.markCreated();
            publishEvents(table);
            ids.add(table.id().value());
        }
        return ids;
    }

    @Transactional
    public void updateCodegen(Long tableId, CodegenTable updatedTable,
                               List<CodegenColumn> updatedColumns) {
        CodegenTable existing = findExistingTable(CodegenTableId.of(tableId));
        existing.updateDefinition(
                updatedTable.tableName(), updatedTable.tableComment(),
                updatedTable.className(), updatedTable.classComment(),
                updatedTable.templateType(), updatedTable.scene(),
                updatedTable.frontType(), updatedTable.masterTableId(),
                updatedTable.subJoinColumnId(), updatedTable.subJoinMany(),
                updatedTable.moduleName(), updatedTable.businessName(),
                updatedTable.packageName(), updatedTable.remark()
        );
        codegenRepository.save(existing);
        // 更新列
        codegenRepository.deleteColumnsByTableId(tableId);
        if (CollUtil.isNotEmpty(updatedColumns)) {
            codegenRepository.saveColumns(tableId, updatedColumns);
        }
    }

    @Transactional
    public void syncCodegenFromDB(Long tableId,
                                   java.util.function.Function<Long, List<CodegenColumn>> syncFunction) {
        findExistingTable(CodegenTableId.of(tableId));
        List<CodegenColumn> syncedColumns = syncFunction.apply(tableId);
        codegenRepository.deleteColumnsByTableId(tableId);
        if (CollUtil.isNotEmpty(syncedColumns)) {
            codegenRepository.saveColumns(tableId, syncedColumns);
        }
    }

    @Transactional
    public void deleteCodegen(Long tableId) {
        findExistingTable(CodegenTableId.of(tableId));
        codegenRepository.delete(CodegenTableId.of(tableId));
    }

    @Transactional
    public void deleteCodegenList(List<Long> tableIds) {
        for (Long tableId : tableIds) {
            codegenRepository.delete(CodegenTableId.of(tableId));
        }
    }

    // ── 查询 ──

    public CodegenTable getCodegenTable(Long id) {
        return codegenRepository.findById(CodegenTableId.of(id));
    }

    public PageResult<CodegenTable> getCodegenTablePage(Long dataSourceConfigId, String tableName,
                                                         String tableComment, Integer scene,
                                                         java.time.LocalDateTime[] createTime,
                                                         Integer pageNo, Integer pageSize) {
        return codegenRepository.findPage(new CodegenTablePageQuery(
                dataSourceConfigId, tableName, tableComment, scene, createTime, pageNo, pageSize));
    }

    public List<CodegenTable> getCodegenTableList(Long dataSourceConfigId) {
        return codegenRepository.findByDataSourceConfigId(dataSourceConfigId);
    }

    public List<CodegenColumn> getCodegenColumnListByTableId(Long tableId) {
        return codegenRepository.findColumnsByTableId(tableId);
    }

    // ── 私有方法 ──

    private CodegenTable findExistingTable(CodegenTableId id) {
        CodegenTable table = codegenRepository.findById(id);
        if (table == null) throw exception(CODEGEN_TABLE_NOT_EXISTS);
        return table;
    }

    private void publishEvents(CodegenTable table) {
        for (var event : table.pullEvents()) {
            eventPublisher.publish(event);
        }
    }
}
