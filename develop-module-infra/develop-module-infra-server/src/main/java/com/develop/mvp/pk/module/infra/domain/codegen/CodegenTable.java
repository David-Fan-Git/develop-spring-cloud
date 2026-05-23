package com.develop.mvp.pk.module.infra.domain.codegen;

// DDD 角色：代码生成表定义聚合根
// 聚合包含 CodegenColumn 子实体

import com.develop.mvp.pk.module.infra.domain.codegen.event.CodegenCreatedEvent;
import com.develop.mvp.pk.module.infra.domain.codegen.event.CodegenDeletedEvent;
import com.develop.mvp.pk.module.infra.domain.codegen.valueobject.CodegenTableId;
import com.develop.mvp.pk.module.infra.domain.event.DomainEvent;

import java.util.*;
import java.util.stream.Collectors;

public final class CodegenTable {

    private final CodegenTableId id;
    private Long dataSourceConfigId;
    private String tableName;
    private String tableComment;
    private String className;
    private String classComment;
    private String author;
    private Integer templateType;
    private Integer scene;
    private Integer frontType;
    private Long masterTableId;
    private Long subJoinColumnId;
    private Integer subJoinMany;
    private String remark;
    private String moduleName;
    private String businessName;
    private String packageName;

    private List<CodegenColumn> columns = new ArrayList<>();

    private final List<DomainEvent> events = new ArrayList<>();

    CodegenTable(CodegenTableId id, String tableName, String tableComment, String className,
                 String classComment, String author, Integer templateType, Integer scene,
                 Integer frontType, Long masterTableId, Long subJoinColumnId,
                 Integer subJoinMany, String moduleName, String businessName,
                 String packageName, Long dataSourceConfigId, String remark) {
        this.id = Objects.requireNonNull(id, "codegenTableId 不能为空");
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.className = className;
        this.classComment = classComment;
        this.author = author;
        this.templateType = templateType;
        this.scene = scene;
        this.frontType = frontType;
        this.masterTableId = masterTableId;
        this.subJoinColumnId = subJoinColumnId;
        this.subJoinMany = subJoinMany;
        this.moduleName = moduleName;
        this.businessName = businessName;
        this.packageName = packageName;
        this.dataSourceConfigId = dataSourceConfigId;
        this.remark = remark;
    }

    // ── 业务方法 ──

    /** 更新表定义 */
    public void updateDefinition(String tableName, String tableComment, String className,
                                  String classComment, Integer templateType, Integer scene,
                                  Integer frontType, Long masterTableId, Long subJoinColumnId,
                                  Integer subJoinMany, String moduleName, String businessName,
                                  String packageName, String remark) {
        this.tableName = tableName;
        this.tableComment = tableComment;
        this.className = className;
        this.classComment = classComment;
        this.templateType = templateType;
        this.scene = scene;
        this.frontType = frontType;
        this.masterTableId = masterTableId;
        this.subJoinColumnId = subJoinColumnId;
        this.subJoinMany = subJoinMany;
        this.moduleName = moduleName;
        this.businessName = businessName;
        this.packageName = packageName;
        this.remark = remark;
    }

    /** 更新列定义 */
    public void updateColumns(List<CodegenColumn> newColumns) {
        this.columns = new ArrayList<>(newColumns);
    }

    /** 是否为子表 */
    public boolean isSubTable() {
        return com.develop.mvp.pk.module.infra.enums.codegen.CodegenTemplateTypeEnum.SUB.getType()
                .equals(templateType);
    }

    /** 是否为主表 */
    public boolean isMasterTable() {
        return com.develop.mvp.pk.module.infra.enums.codegen.CodegenTemplateTypeEnum.isMaster(templateType);
    }

    public void markCreated() {
        events.add(new CodegenCreatedEvent(this.id.value(), this.tableName));
    }

    public void markDeleted() {
        events.add(new CodegenDeletedEvent(this.id.value(), this.tableName));
    }

    // ── 查询方法 ──

    public CodegenTableId id() { return id; }
    public Long dataSourceConfigId() { return dataSourceConfigId; }
    public String tableName() { return tableName; }
    public String tableComment() { return tableComment; }
    public String className() { return className; }
    public String classComment() { return classComment; }
    public String author() { return author; }
    public Integer templateType() { return templateType; }
    public Integer scene() { return scene; }
    public Integer frontType() { return frontType; }
    public Long masterTableId() { return masterTableId; }
    public Long subJoinColumnId() { return subJoinColumnId; }
    public Integer subJoinMany() { return subJoinMany; }
    public String moduleName() { return moduleName; }
    public String businessName() { return businessName; }
    public String packageName() { return packageName; }
    public String remark() { return remark; }
    public List<CodegenColumn> columns() { return Collections.unmodifiableList(columns); }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> result = new ArrayList<>(events);
        events.clear();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodegenTable that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "CodegenTable{id=" + id + ", tableName=" + tableName + '}';
    }
}
