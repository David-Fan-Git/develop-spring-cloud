package com.develop.mvp.pk.module.infra.application.db;

// DDD 角色：应用编排服务

import com.develop.mvp.pk.module.infra.domain.db.DataSourceConfig;
import com.develop.mvp.pk.module.infra.domain.db.repository.DataSourceConfigRepository;
import com.develop.mvp.pk.module.infra.domain.db.valueobject.DataSourceConfigId;
import com.develop.mvp.pk.module.infra.domain.event.DomainEventPublisher;
import com.develop.mvp.pk.module.infra.framework.mybatis.core.util.JdbcUtils;
import com.develop.mvp.pk.module.infra.infrastructure.db.DataSourceConfigFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.infra.enums.ErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_EXISTS;
import static com.develop.mvp.pk.module.infra.enums.ErrorCodeConstants.DATA_SOURCE_CONFIG_NOT_OK;

@Service
public class DataSourceConfigApplicationService {

    private final DataSourceConfigRepository dataSourceConfigRepository;
    private final DomainEventPublisher eventPublisher;

    public DataSourceConfigApplicationService(DataSourceConfigRepository dataSourceConfigRepository,
                                               DomainEventPublisher eventPublisher) {
        this.dataSourceConfigRepository = dataSourceConfigRepository;
        this.eventPublisher = eventPublisher;
    }

    // ── 命令 ──

    @Transactional
    public Long createDataSourceConfig(String name, String url, String username, String password) {
        validateConnectionOK(url, username, password);
        DataSourceConfig config = DataSourceConfigFactory.create(null, name, url, username, password);
        config = dataSourceConfigRepository.save(config);
        config.markCreated();
        publishEvents(config);
        return config.id().value();
    }

    @Transactional
    public void updateDataSourceConfig(Long id, String name, String url, String username, String password) {
        DataSourceConfig config = findExistingConfig(DataSourceConfigId.of(id));
        validateConnectionOK(url, username, password);
        // 持久化时通过基础设施层处理
        configRepositorySaveWithNewIdentity(config, name, url, username, password);
        publishEvents(config);
    }

    @Transactional
    public void deleteDataSourceConfig(Long id) {
        DataSourceConfig config = findExistingConfig(DataSourceConfigId.of(id));
        config.markDeleted();
        dataSourceConfigRepository.delete(config.id());
        publishEvents(config);
    }

    @Transactional
    public void deleteDataSourceConfigList(List<Long> ids) {
        for (Long id : ids) {
            deleteDataSourceConfig(id);
        }
    }

    // ── 查询 ──

    public DataSourceConfig getDataSourceConfig(Long id) {
        return dataSourceConfigRepository.findById(DataSourceConfigId.of(id));
    }

    public List<DataSourceConfig> getDataSourceConfigList() {
        return dataSourceConfigRepository.findAll();
    }

    // ── 私有方法 ──

    private void configRepositorySaveWithNewIdentity(DataSourceConfig config, String name,
                                                      String url, String username, String password) {
        DataSourceConfig newConfig = DataSourceConfigFactory.create(
                config.id().value(), name, url, username, password);
        dataSourceConfigRepository.save(newConfig);
    }

    private DataSourceConfig findExistingConfig(DataSourceConfigId id) {
        DataSourceConfig config = dataSourceConfigRepository.findById(id);
        if (config == null) throw exception(DATA_SOURCE_CONFIG_NOT_EXISTS);
        return config;
    }

    private void validateConnectionOK(String url, String username, String password) {
        boolean success = JdbcUtils.isConnectionOK(url, username, password);
        if (!success) {
            throw exception(DATA_SOURCE_CONFIG_NOT_OK);
        }
    }

    private void publishEvents(DataSourceConfig config) {
        for (var event : config.pullEvents()) {
            eventPublisher.publish(event);
        }
    }
}
