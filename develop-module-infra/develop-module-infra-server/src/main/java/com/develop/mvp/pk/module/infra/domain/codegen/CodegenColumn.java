package com.develop.mvp.pk.module.infra.domain.codegen;

// DDD 角色：代码生成字段定义，作为 CodegenTable 聚合内的子实体

import java.util.Objects;

public final class CodegenColumn {

    private Long id;
    private Long tableId;
    private String columnName;
    private Integer ordinalPosition;
    private String dataType;
    private String columnType;
    private String columnComment;
    private Boolean nullable;
    private Boolean primaryKey;
    private Boolean autoIncrement;
    private String ordinalBasis;
    private String javaType;
    private String javaField;
    private String dictType;
    private String example;
    private Boolean createOperation;
    private Boolean updateOperation;
    private Boolean listOperation;
    private Boolean listOperationCondition;
    private Boolean listOperationResult;
    private String htmlType;

    public CodegenColumn(Long id, Long tableId, String columnName, Integer ordinalPosition,
                         String dataType, String columnType, String columnComment,
                         Boolean nullable, Boolean primaryKey, Boolean autoIncrement,
                         String ordinalBasis, String javaType, String javaField,
                         String dictType, String example, Boolean createOperation,
                         Boolean updateOperation, Boolean listOperation,
                         Boolean listOperationCondition, Boolean listOperationResult,
                         String htmlType) {
        this.id = id;
        this.tableId = Objects.requireNonNull(tableId, "tableId 不能为空");
        this.columnName = Objects.requireNonNull(columnName, "columnName 不能为空");
        this.ordinalPosition = ordinalPosition;
        this.dataType = dataType;
        this.columnType = columnType;
        this.columnComment = columnComment;
        this.nullable = nullable;
        this.primaryKey = primaryKey;
        this.autoIncrement = autoIncrement;
        this.ordinalBasis = ordinalBasis;
        this.javaType = javaType;
        this.javaField = javaField;
        this.dictType = dictType;
        this.example = example;
        this.createOperation = createOperation;
        this.updateOperation = updateOperation;
        this.listOperation = listOperation;
        this.listOperationCondition = listOperationCondition;
        this.listOperationResult = listOperationResult;
        this.htmlType = htmlType;
    }

    // ── 查询方法 ──

    public Long id() { return id; }
    public Long tableId() { return tableId; }
    public String columnName() { return columnName; }
    public Integer ordinalPosition() { return ordinalPosition; }
    public String dataType() { return dataType; }
    public String columnType() { return columnType; }
    public String columnComment() { return columnComment; }
    public Boolean nullable() { return nullable; }
    public Boolean primaryKey() { return primaryKey; }
    public Boolean autoIncrement() { return autoIncrement; }
    public String ordinalBasis() { return ordinalBasis; }
    public String javaType() { return javaType; }
    public String javaField() { return javaField; }
    public String dictType() { return dictType; }
    public String example() { return example; }
    public Boolean createOperation() { return createOperation; }
    public Boolean updateOperation() { return updateOperation; }
    public Boolean listOperation() { return listOperation; }
    public Boolean listOperationCondition() { return listOperationCondition; }
    public Boolean listOperationResult() { return listOperationResult; }
    public String htmlType() { return htmlType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodegenColumn that)) return false;
        return Objects.equals(id, that.id) && columnName.equals(that.columnName);
    }

    @Override
    public int hashCode() { return Objects.hash(id, columnName); }

    @Override
    public String toString() {
        return "CodegenColumn{id=" + id + ", columnName=" + columnName + '}';
    }
}
