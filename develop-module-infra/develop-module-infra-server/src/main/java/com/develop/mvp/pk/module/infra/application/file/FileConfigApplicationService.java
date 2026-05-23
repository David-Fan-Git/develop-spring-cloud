package com.develop.mvp.pk.module.infra.application.file;

// DDD 角色：应用编排服务
// 规则 R01：只能有一个 Master 文件配置
// 规则 R02：Master 配置不可删除

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.infra.domain.config.valueobject.ConfigVisible;
import com.develop.mvp.pk.module.infra.domain.event.DomainEventPublisher;
import com.develop.mvp.pk.module.infra.domain.file.FileConfig;
import com.develop.mvp.pk.module.infra.domain.file.repository.FileConfigPageQuery;
import com.develop.mvp.pk.module.infra.domain.file.repository.FileConfigRepository;
import com.develop.mvp.pk.module.infra.domain.file.valueobject.FileConfigId;
import com.develop.mvp.pk.module.infra.infrastructure.file.FileConfigFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_DELETE_FAIL_MASTER;
import static com.develop.mvp.pk.module.infra.enums.ErrorCodeConstants.FILE_CONFIG_NOT_EXISTS;

@Service
public class FileConfigApplicationService {

    private final FileConfigRepository fileConfigRepository;
    private final DomainEventPublisher eventPublisher;

    public FileConfigApplicationService(FileConfigRepository fileConfigRepository,
                                         DomainEventPublisher eventPublisher) {
        this.fileConfigRepository = fileConfigRepository;
        this.eventPublisher = eventPublisher;
    }

    // ── 命令 ──

    @Transactional
    public Long createFileConfig(String name, Integer storage, Boolean master, String remark) {
        FileConfig config = FileConfigFactory.create(null, name, storage, master, remark);
        config = fileConfigRepository.save(config);
        config.markCreated();
        publishEvents(config);
        return config.id().value();
    }

    @Transactional
    public void updateFileConfig(Long id, String name, Integer storage, String remark) {
        FileConfig config = findExistingConfig(FileConfigId.of(id));
        config.updateProfile(storage, name, remark);
        fileConfigRepository.save(config);
        publishEvents(config);
    }

    /** 规则 R01：设置为 Master */
    @Transactional
    public void updateFileConfigMaster(Long id) {
        findExistingConfig(FileConfigId.of(id));
        // 清除所有配置的 master 标记
        List<FileConfig> allConfigs = fileConfigRepository.findAll();
        for (FileConfig config : allConfigs) {
            if (config.isMaster()) {
                config.clearMaster();
                fileConfigRepository.save(config);
            }
        }
        // 设置新的 master
        FileConfig target = findExistingConfig(FileConfigId.of(id));
        target.setAsMaster();
        fileConfigRepository.save(target);
        publishEvents(target);
    }

    /** 规则 R02：Master 不可删除 */
    @Transactional
    public void deleteFileConfig(Long id) {
        FileConfig config = findExistingConfig(FileConfigId.of(id));
        if (config.isMaster()) {
            throw exception(FILE_CONFIG_DELETE_FAIL_MASTER);
        }
        config.markDeleted();
        fileConfigRepository.delete(config.id());
        publishEvents(config);
    }

    @Transactional
    public void deleteFileConfigList(List<Long> ids) {
        for (Long id : ids) {
            FileConfig config = findExistingConfig(FileConfigId.of(id));
            if (config.isMaster()) {
                throw exception(FILE_CONFIG_DELETE_FAIL_MASTER);
            }
        }
        for (Long id : ids) {
            fileConfigRepository.delete(FileConfigId.of(id));
        }
    }

    // ── 查询 ──

    public FileConfig getFileConfig(Long id) {
        return fileConfigRepository.findById(FileConfigId.of(id));
    }

    public PageResult<FileConfig> getFileConfigPage(String name, Integer storage,
                                                     java.time.LocalDateTime[] createTime,
                                                     Integer pageNo, Integer pageSize) {
        return fileConfigRepository.findPage(new FileConfigPageQuery(
                name, storage, createTime, pageNo, pageSize));
    }

    public String testFileConfig(Long id) throws Exception {
        findExistingConfig(FileConfigId.of(id));
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        return "test ok";
    }

    // ── 私有方法 ──

    private FileConfig findExistingConfig(FileConfigId id) {
        FileConfig config = fileConfigRepository.findById(id);
        if (config == null) throw exception(FILE_CONFIG_NOT_EXISTS);
        return config;
    }

    private void publishEvents(FileConfig config) {
        for (var event : config.pullEvents()) {
            eventPublisher.publish(event);
        }
    }
}
