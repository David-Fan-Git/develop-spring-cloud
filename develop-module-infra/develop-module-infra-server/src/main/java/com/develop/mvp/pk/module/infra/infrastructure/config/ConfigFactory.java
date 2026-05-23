package com.develop.mvp.pk.module.infra.infrastructure.config;

// DDD 角色：工厂，负责创建和重建 Config 聚合
// 位于基础设施层，供 RepositoryImpl 调用

import com.develop.mvp.pk.module.infra.domain.config.Config;
import com.develop.mvp.pk.module.infra.domain.config.valueobject.ConfigId;
import com.develop.mvp.pk.module.infra.domain.config.valueobject.ConfigKey;
import com.develop.mvp.pk.module.infra.domain.config.valueobject.ConfigType;
import com.develop.mvp.pk.module.infra.domain.config.valueobject.ConfigVisible;

public final class ConfigFactory {

    private ConfigFactory() {}

    /** 创建新配置 */
    public static Config create(Long id, String key, String value, String name,
                                String category, Integer type, Boolean visible, String remark) {
        return new Config(
                ConfigId.of(id),
                ConfigKey.of(key),
                value, name, category,
                ConfigType.of(type),
                ConfigVisible.of(visible),
                remark
        );
    }

    /** 从持久化数据重建 Config 聚合 */
    public static Config reconstitute(Long id, String key, String value, String name,
                                      String category, Integer type, Boolean visible, String remark) {
        return new Config(
                ConfigId.of(id),
                ConfigKey.of(key),
                value, name, category,
                ConfigType.of(type),
                ConfigVisible.of(visible),
                remark
        );
    }
}
