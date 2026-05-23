package com.develop.mvp.pk.module.infra.infrastructure.codegen;

// DDD 角色：CodegenRepository 的 MyBatis 实现

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.infra.dal.dataobject.codegen.CodegenColumnDO;
import com.develop.mvp.pk.module.infra.dal.dataobject.codegen.CodegenTableDO;
import com.develop.mvp.pk.module.infra.dal.mysql.codegen.CodegenColumnMapper;
import com.develop.mvp.pk.module.infra.dal.mysql.codegen.CodegenTableMapper;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenColumn;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenTable;
import com.develop.mvp.pk.module.infra.domain.codegen.repository.CodegenRepository;
import com.develop.mvp.pk.module.infra.domain.codegen.repository.CodegenTablePageQuery;
import com.develop.mvp.pk.module.infra.domain.codegen.valueobject.CodegenTableId;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CodegenRepositoryImpl implements CodegenRepository {

    private final CodegenTableMapper codegenTableMapper;
    private final CodegenColumnMapper codegenColumnMapper;

    public CodegenRepositoryImpl(CodegenTableMapper codegenTableMapper,
                                  CodegenColumnMapper codegenColumnMapper) {
        this.codegenTableMapper = codegenTableMapper;
        this.codegenColumnMapper = codegenColumnMapper;
    }

    @Override
    public CodegenTable save(CodegenTable table) {
        CodegenTableDO tableDO = toTableDataObject(table);
        if (codegenTableMapper.selectById(table.id().value()) == null) {
            codegenTableMapper.insert(tableDO);
        } else {
            codegenTableMapper.updateById(tableDO);
        }
        return table;
    }

    @Override
    public void delete(CodegenTableId tableId) {
        codegenTableMapper.deleteById(tableId.value());
        codegenColumnMapper.deleteListByTableId(tableId.value());
    }

    @Override
    public void deleteByIds(Collection<CodegenTableId> tableIds) {
        List<Long> rawIds = tableIds.stream().map(CodegenTableId::value).collect(Collectors.toList());
        codegenTableMapper.deleteByIds(rawIds);
        rawIds.forEach(codegenColumnMapper::deleteListByTableId);
    }

    @Override
    public CodegenTable findById(CodegenTableId tableId) {
        CodegenTableDO tableDO = codegenTableMapper.selectById(tableId.value());
        if (tableDO == null) return null;
        CodegenTable table = toTableDomain(tableDO);
        List<CodegenColumn> columns = findColumnsByTableId(tableId.value());
        table.updateColumns(columns);
        return table;
    }

    @Override
    public PageResult<CodegenTable> findPage(CodegenTablePageQuery query) {
        var reqVO = new com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.table.CodegenTablePageReqVO();
        reqVO.setTableName(query.tableName());
        reqVO.setTableComment(query.tableComment());
        reqVO.setCreateTime(query.createTime());
        reqVO.setPageNo(query.pageNo());
        reqVO.setPageSize(query.pageSize());

        PageResult<CodegenTableDO> doPage = codegenTableMapper.selectPage(reqVO);
        List<CodegenTable> tables = doPage.getList().stream()
                .map(this::toTableDomain).collect(Collectors.toList());
        return new PageResult<>(tables, doPage.getTotal());
    }

    @Override
    public List<CodegenTable> findByDataSourceConfigId(Long dataSourceConfigId) {
        return codegenTableMapper.selectListByDataSourceConfigId(dataSourceConfigId).stream()
                .map(this::toTableDomain).collect(Collectors.toList());
    }

    @Override
    public List<CodegenTable> findByTemplateTypeAndMasterTableId(Integer templateType, Long masterTableId) {
        return codegenTableMapper.selectListByTemplateTypeAndMasterTableId(templateType, masterTableId).stream()
                .map(this::toTableDomain).collect(Collectors.toList());
    }

    @Override
    public CodegenTable findByTableNameAndDataSourceConfigId(String tableName, Long dataSourceConfigId) {
        CodegenTableDO tableDO = codegenTableMapper.selectByTableNameAndDataSourceConfigId(tableName, dataSourceConfigId);
        return tableDO != null ? toTableDomain(tableDO) : null;
    }

    @Override
    public List<CodegenColumn> findColumnsByTableId(Long tableId) {
        return codegenColumnMapper.selectListByTableId(tableId).stream()
                .map(this::toColumnDomain).collect(Collectors.toList());
    }

    @Override
    public List<CodegenColumn> saveColumns(Long tableId, List<CodegenColumn> columns) {
        List<CodegenColumnDO> columnDOs = columns.stream()
                .map(this::toColumnDataObject).collect(Collectors.toList());
        for (CodegenColumnDO columnDO : columnDOs) {
            if (columnDO.getId() != null && codegenColumnMapper.selectById(columnDO.getId()) != null) {
                codegenColumnMapper.updateById(columnDO);
            } else {
                codegenColumnMapper.insert(columnDO);
            }
        }
        return columns;
    }

    @Override
    public void deleteColumnsByTableId(Long tableId) {
        codegenColumnMapper.deleteListByTableId(tableId);
    }

    @Override
    public void deleteColumnsByIds(Collection<Long> columnIds) {
        codegenColumnMapper.deleteByIds(columnIds);
    }

    private CodegenTableDO toTableDataObject(CodegenTable table) {
        CodegenTableDO tableDO = new CodegenTableDO();
        tableDO.setId(table.id().value());
        tableDO.setDataSourceConfigId(table.dataSourceConfigId());
        tableDO.setTableName(table.tableName());
        tableDO.setTableComment(table.tableComment());
        tableDO.setClassName(table.className());
        tableDO.setClassComment(table.classComment());
        tableDO.setAuthor(table.author());
        tableDO.setTemplateType(table.templateType());
        tableDO.setScene(table.scene());
        tableDO.setFrontType(table.frontType());
        tableDO.setMasterTableId(table.masterTableId());
        tableDO.setSubJoinColumnId(table.subJoinColumnId());
        tableDO.setSubJoinMany(Integer.valueOf(1).equals(table.subJoinMany()));
        tableDO.setModuleName(table.moduleName());
        tableDO.setBusinessName(table.businessName());
        tableDO.setRemark(table.remark());
        return tableDO;
    }

    private CodegenTable toTableDomain(CodegenTableDO tableDO) {
        return CodegenTableFactory.reconstitute(
                tableDO.getId(),
                tableDO.getTableName(),
                tableDO.getTableComment(),
                tableDO.getClassName(),
                tableDO.getClassComment(),
                tableDO.getAuthor(),
                tableDO.getTemplateType(),
                tableDO.getScene(),
                tableDO.getFrontType(),
                tableDO.getMasterTableId(),
                tableDO.getSubJoinColumnId(),
                Boolean.TRUE.equals(tableDO.getSubJoinMany()) ? 1 : 0,
                tableDO.getModuleName(),
                tableDO.getBusinessName(),
                null,
                tableDO.getDataSourceConfigId(),
                tableDO.getRemark()
        );
    }

    private CodegenColumnDO toColumnDataObject(CodegenColumn column) {
        CodegenColumnDO columnDO = new CodegenColumnDO();
        columnDO.setId(column.id());
        columnDO.setTableId(column.tableId());
        columnDO.setColumnName(column.columnName());
        columnDO.setOrdinalPosition(column.ordinalPosition());
        columnDO.setDataType(column.dataType());
        columnDO.setColumnComment(column.columnComment());
        columnDO.setNullable(column.nullable());
        columnDO.setPrimaryKey(column.primaryKey());
        columnDO.setJavaType(column.javaType());
        columnDO.setJavaField(column.javaField());
        columnDO.setDictType(column.dictType());
        columnDO.setExample(column.example());
        columnDO.setCreateOperation(column.createOperation());
        columnDO.setUpdateOperation(column.updateOperation());
        columnDO.setListOperation(column.listOperation());
        columnDO.setListOperationCondition(
                Boolean.TRUE.equals(column.listOperationCondition()) ? "true" : "false");
        columnDO.setListOperationResult(column.listOperationResult());
        columnDO.setHtmlType(column.htmlType());
        return columnDO;
    }

    private CodegenColumn toColumnDomain(CodegenColumnDO columnDO) {
        return CodegenTableFactory.reconstituteColumn(
                columnDO.getId(),
                columnDO.getTableId(),
                columnDO.getColumnName(),
                columnDO.getOrdinalPosition(),
                columnDO.getDataType(),
                null,
                columnDO.getColumnComment(),
                columnDO.getNullable(),
                columnDO.getPrimaryKey(),
                null,
                null,
                columnDO.getJavaType(),
                columnDO.getJavaField(),
                columnDO.getDictType(),
                columnDO.getExample(),
                columnDO.getCreateOperation(),
                columnDO.getUpdateOperation(),
                columnDO.getListOperation(),
                "true".equalsIgnoreCase(columnDO.getListOperationCondition()),
                columnDO.getListOperationResult(),
                columnDO.getHtmlType()
        );
    }
}
