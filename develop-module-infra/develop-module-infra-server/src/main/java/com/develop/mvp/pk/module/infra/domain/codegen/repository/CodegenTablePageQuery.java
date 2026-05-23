package com.develop.mvp.pk.module.infra.domain.codegen.repository;

// DDD 角色：代码生成表分页查询对象

import java.time.LocalDateTime;

public record CodegenTablePageQuery(
        Long dataSourceConfigId,
        String tableName,
        String tableComment,
        Integer scene,
        LocalDateTime[] createTime,
        Integer pageNo,
        Integer pageSize
) {}
