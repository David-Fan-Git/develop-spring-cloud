package com.develop.mvp.pk.module.infra.controller.admin.codegen;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.infra.application.codegen.CodegenApplicationService;
import com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.CodegenCreateListReqVO;
import com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.CodegenDetailRespVO;
import com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.CodegenPreviewRespVO;
import com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.CodegenUpdateReqVO;
import com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.table.CodegenTablePageReqVO;
import com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.table.CodegenTableRespVO;
import com.develop.mvp.pk.module.infra.controller.admin.codegen.vo.table.DatabaseTableRespVO;
import com.develop.mvp.pk.module.infra.convert.codegen.CodegenConvert;
import com.develop.mvp.pk.module.infra.dal.dataobject.codegen.CodegenColumnDO;
import com.develop.mvp.pk.module.infra.dal.dataobject.codegen.CodegenTableDO;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenColumn;
import com.develop.mvp.pk.module.infra.domain.codegen.CodegenTable;
import com.develop.mvp.pk.module.infra.service.codegen.CodegenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.common.pojo.CommonResult.success;
import static com.develop.mvp.pk.framework.security.core.util.SecurityFrameworkUtils.getLoginUserNickname;
import static com.develop.mvp.pk.module.infra.framework.file.core.utils.FileTypeUtils.writeAttachment;

@Tag(name = "管理后台 - 代码生成器")
@RestController
@RequestMapping("/infra/codegen")
@Validated
public class CodegenController {

    @Resource
    private CodegenApplicationService codegenApplicationService;
    @Resource
    private CodegenService codegenService; // 保留，用于复杂生成逻辑

    @GetMapping("/db/table/list")
    @Operation(summary = "获得数据库自带的表定义列表")
    @Parameters({
            @Parameter(name = "dataSourceConfigId", description = "数据源配置的编号", required = true, example = "1"),
            @Parameter(name = "name", description = "表名，模糊匹配", example = "develop"),
            @Parameter(name = "comment", description = "描述，模糊匹配", example = "David")
    })
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<List<DatabaseTableRespVO>> getDatabaseTableList(
            @RequestParam(value = "dataSourceConfigId") Long dataSourceConfigId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "comment", required = false) String comment) {
        return success(codegenService.getDatabaseTableList(dataSourceConfigId, name, comment));
    }

    @GetMapping("/table/list")
    @Operation(summary = "获得表定义列表")
    @Parameter(name = "dataSourceConfigId", description = "数据源配置的编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<List<CodegenTableRespVO>> getCodegenTableList(@RequestParam(value = "dataSourceConfigId") Long dataSourceConfigId) {
        List<CodegenTable> list = codegenApplicationService.getCodegenTableList(dataSourceConfigId);
        return success(list.stream().map(this::toCodegenTableRespVO).collect(Collectors.toList()));
    }

    @GetMapping("/table/page")
    @Operation(summary = "获得表定义分页")
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<PageResult<CodegenTableRespVO>> getCodegenTablePage(@Valid CodegenTablePageReqVO pageReqVO) {
        PageResult<CodegenTable> pageResult = codegenApplicationService.getCodegenTablePage(
                null, pageReqVO.getTableName(), pageReqVO.getTableComment(),
                null, pageReqVO.getCreateTime(),
                pageReqVO.getPageNo(), pageReqVO.getPageSize());
        PageResult<CodegenTableRespVO> voPage = new PageResult<>(
                pageResult.getList().stream().map(this::toCodegenTableRespVO).collect(Collectors.toList()),
                pageResult.getTotal());
        return success(voPage);
    }

    @GetMapping("/detail")
    @Operation(summary = "获得表和字段的明细")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:query')")
    public CommonResult<CodegenDetailRespVO> getCodegenDetail(@RequestParam("tableId") Long tableId) {
        CodegenTable table = codegenApplicationService.getCodegenTable(tableId);
        List<CodegenColumn> columns = codegenApplicationService.getCodegenColumnListByTableId(tableId);
        return success(CodegenConvert.INSTANCE.convert(
                toCodegenTableDO(table), toCodegenColumnDOList(columns)));
    }

    @Operation(summary = "基于数据库的表结构，创建代码生成器的表和字段定义")
    @PostMapping("/create-list")
    @PreAuthorize("@ss.hasPermission('infra:codegen:create')")
    public CommonResult<List<Long>> createCodegenList(@Valid @RequestBody CodegenCreateListReqVO reqVO) {
        return success(codegenService.createCodegenList(getLoginUserNickname(), reqVO));
    }

    @Operation(summary = "更新数据库的表和字段定义")
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermission('infra:codegen:update')")
    public CommonResult<Boolean> updateCodegen(@Valid @RequestBody CodegenUpdateReqVO updateReqVO) {
        codegenService.updateCodegen(updateReqVO);
        return success(true);
    }

    @Operation(summary = "基于数据库的表结构，同步数据库的表和字段定义")
    @PutMapping("/sync-from-db")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:update')")
    public CommonResult<Boolean> syncCodegenFromDB(@RequestParam("tableId") Long tableId) {
        codegenService.syncCodegenFromDB(tableId);
        return success(true);
    }

    @Operation(summary = "删除数据库的表和字段定义")
    @DeleteMapping("/delete")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:delete')")
    public CommonResult<Boolean> deleteCodegen(@RequestParam("tableId") Long tableId) {
        codegenApplicationService.deleteCodegen(tableId);
        return success(true);
    }

    @Operation(summary = "批量删除数据库的表和字段定义")
    @DeleteMapping("/delete-list")
    @Parameter(name = "tableIds", description = "表编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('infra:codegen:delete')")
    public CommonResult<Boolean> deleteCodegenList(@RequestParam("tableIds") List<Long> tableIds) {
        codegenApplicationService.deleteCodegenList(tableIds);
        return success(true);
    }

    @Operation(summary = "预览生成代码")
    @GetMapping("/preview")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:preview')")
    public CommonResult<List<CodegenPreviewRespVO>> previewCodegen(@RequestParam("tableId") Long tableId) {
        Map<String, String> codes = codegenService.generationCodes(tableId);
        return success(CodegenConvert.INSTANCE.convert(codes));
    }

    @Operation(summary = "下载生成代码")
    @GetMapping("/download")
    @Parameter(name = "tableId", description = "表编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('infra:codegen:download')")
    public void downloadCodegen(@RequestParam("tableId") Long tableId,
                                HttpServletResponse response) throws IOException {
        Map<String, String> codes = codegenService.generationCodes(tableId);
        String[] paths = codes.keySet().toArray(new String[0]);
        ByteArrayInputStream[] ins = codes.values().stream().map(IoUtil::toUtf8Stream).toArray(ByteArrayInputStream[]::new);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipUtil.zip(outputStream, paths, ins);
        writeAttachment(response, "codegen.zip", outputStream.toByteArray());
    }

    // ── 转换方法 ──

    private CodegenTableRespVO toCodegenTableRespVO(CodegenTable table) {
        if (table == null) return null;
        CodegenTableRespVO vo = new CodegenTableRespVO();
        vo.setId(table.id().value());
        vo.setDataSourceConfigId(table.dataSourceConfigId() != null ? table.dataSourceConfigId().intValue() : null);
        vo.setTableName(table.tableName());
        vo.setTableComment(table.tableComment());
        vo.setClassName(table.className());
        vo.setClassComment(table.classComment());
        vo.setAuthor(table.author());
        vo.setTemplateType(table.templateType());
        vo.setScene(table.scene());
        vo.setFrontType(table.frontType());
        vo.setMasterTableId(table.masterTableId());
        vo.setSubJoinColumnId(table.subJoinColumnId());
        vo.setSubJoinMany(Integer.valueOf(1).equals(table.subJoinMany()));
        vo.setModuleName(table.moduleName());
        vo.setBusinessName(table.businessName());
        vo.setRemark(table.remark());
        return vo;
    }

    private CodegenTableDO toCodegenTableDO(CodegenTable table) {
        if (table == null) return null;
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

    private List<CodegenColumnDO> toCodegenColumnDOList(List<CodegenColumn> columns) {
        if (columns == null) return null;
        return columns.stream().map(this::toCodegenColumnDO).collect(Collectors.toList());
    }

    private CodegenColumnDO toCodegenColumnDO(CodegenColumn column) {
        if (column == null) return null;
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
}
