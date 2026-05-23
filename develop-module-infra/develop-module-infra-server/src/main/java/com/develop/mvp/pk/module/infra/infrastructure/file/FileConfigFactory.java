package com.develop.mvp.pk.module.infra.infrastructure.file;

// DDD 角色：工厂，负责创建和重建 FileConfig 聚合

import com.develop.mvp.pk.module.infra.domain.file.FileConfig;
import com.develop.mvp.pk.module.infra.domain.file.valueobject.FileConfigId;
import com.develop.mvp.pk.module.infra.domain.file.valueobject.FileConfigName;

public final class FileConfigFactory {

    private FileConfigFactory() {}

    /** 创建新文件配置 */
    public static FileConfig create(Long id, String name, Integer storage,
                                    Boolean master, String remark) {
        return new FileConfig(
                FileConfigId.of(id),
                FileConfigName.of(name),
                storage, master, remark
        );
    }

    /** 从持久化数据重建 */
    public static FileConfig reconstitute(Long id, String name, Integer storage,
                                          Boolean master, String remark) {
        return new FileConfig(
                FileConfigId.of(id),
                FileConfigName.of(name),
                storage, master, remark
        );
    }
}
