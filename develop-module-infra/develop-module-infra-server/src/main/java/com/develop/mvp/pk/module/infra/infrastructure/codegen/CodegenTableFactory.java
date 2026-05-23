package com.develop.mvp.pk.module.infra.infrastructure.codegen;

// DDD 角色：工厂，负责创建和重建 CodegenTable 和 CodegenColumn

import com.develop.mvp.pk.module.infra.domain.codegen.CodegenColumn;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenTable;
import com.develop.mvp.pk.module.infra.domain.codegen.valueobject.CodegenTableId;

public final class CodegenTableFactory {

    private CodegenTableFactory() {}

    /** 创建 CodegenTable */
    public static CodegenTable create(Long id, String tableName, String tableComment,
                                       String className, String classComment, String author,
                                       Integer templateType, Integer scene, Integer frontType,
                                       Long masterTableId, Long subJoinColumnId,
                                       Integer subJoinMany, String moduleName,
                                       String businessName, String packageName,
                                       Long dataSourceConfigId, String remark) {
        return new CodegenTable(
                CodegenTableId.of(id), tableName, tableComment, className, classComment,
                author, templateType, scene, frontType, masterTableId, subJoinColumnId,
                subJoinMany, moduleName, businessName, packageName, dataSourceConfigId, remark
        );
    }

    /** 从持久化数据重建 CodegenTable */
    public static CodegenTable reconstitute(Long id, String tableName, String tableComment,
                                             String className, String classComment, String author,
                                             Integer templateType, Integer scene, Integer frontType,
                                             Long masterTableId, Long subJoinColumnId,
                                             Integer subJoinMany, String moduleName,
                                             String businessName, String packageName,
                                             Long dataSourceConfigId, String remark) {
        return new CodegenTable(
                CodegenTableId.of(id), tableName, tableComment, className, classComment,
                author, templateType, scene, frontType, masterTableId, subJoinColumnId,
                subJoinMany, moduleName, businessName, packageName, dataSourceConfigId, remark
        );
    }

    /** 创建 CodegenColumn */
    public static CodegenColumn createColumn(Long id, Long tableId, String columnName,
                                              Integer ordinalPosition, String dataType,
                                              String columnType, String columnComment,
                                              Boolean nullable, Boolean primaryKey,
                                              Boolean autoIncrement, String ordinalBasis,
                                              String javaType, String javaField,
                                              String dictType, String example,
                                              Boolean createOperation, Boolean updateOperation,
                                              Boolean listOperation, Boolean listOperationCondition,
                                              Boolean listOperationResult, String htmlType) {
        return new CodegenColumn(id, tableId, columnName, ordinalPosition, dataType, columnType,
                columnComment, nullable, primaryKey, autoIncrement, ordinalBasis, javaType,
                javaField, dictType, example, createOperation, updateOperation, listOperation,
                listOperationCondition, listOperationResult, htmlType);
    }

    /** 从持久化数据重建 CodegenColumn */
    public static CodegenColumn reconstituteColumn(Long id, Long tableId, String columnName,
                                                    Integer ordinalPosition, String dataType,
                                                    String columnType, String columnComment,
                                                    Boolean nullable, Boolean primaryKey,
                                                    Boolean autoIncrement, String ordinalBasis,
                                                    String javaType, String javaField,
                                                    String dictType, String example,
                                                    Boolean createOperation, Boolean updateOperation,
                                                    Boolean listOperation, Boolean listOperationCondition,
                                                    Boolean listOperationResult, String htmlType) {
        return new CodegenColumn(id, tableId, columnName, ordinalPosition, dataType, columnType,
                columnComment, nullable, primaryKey, autoIncrement, ordinalBasis, javaType,
                javaField, dictType, example, createOperation, updateOperation, listOperation,
                listOperationCondition, listOperationResult, htmlType);
    }
}
