package com.develop.mvp.pk.module.infra.application.file;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.infra.domain.event.DomainEventPublisher;
import com.develop.mvp.pk.module.infra.domain.file.FileConfig;
import com.develop.mvp.pk.module.infra.domain.file.repository.FileConfigPageQuery;
import com.develop.mvp.pk.module.infra.domain.file.repository.FileConfigRepository;
import com.develop.mvp.pk.module.infra.domain.file.valueobject.FileConfigId;
import com.develop.mvp.pk.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import com.develop.mvp.pk.module.infra.framework.file.core.enums.FileStorageEnum;
import com.develop.mvp.pk.module.infra.infrastructure.file.FileConfigFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FileConfigApplicationServiceTest {

    private InMemoryFileConfigRepository repository;
    private FileConfigApplicationService applicationService;

    @BeforeEach
    void setUp() {
        repository = new InMemoryFileConfigRepository();
        DomainEventPublisher eventPublisher = event -> {};
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        applicationService = new FileConfigApplicationService(repository, eventPublisher, validator);
    }

    @Test
    void createFileConfig_preservesClientConfig() {
        Long id = applicationService.createFileConfig("本地存储", FileStorageEnum.LOCAL.getStorage(), false, localConfigMap(), "remark");

        LocalFileClientConfig config = assertInstanceOf(LocalFileClientConfig.class,
                repository.findById(FileConfigId.of(id)).config());
        assertEquals("/tmp/uploads", config.getBasePath());
        assertEquals("https://static.example.com", config.getDomain());
    }

    @Test
    void updateFileConfig_preservesClientConfig() {
        Long id = applicationService.createFileConfig("本地存储", FileStorageEnum.LOCAL.getStorage(), false, localConfigMap(), "remark");
        Map<String, Object> updatedConfig = localConfigMap();
        updatedConfig.put("basePath", "/data/uploads");

        applicationService.updateFileConfig(id, "本地存储", FileStorageEnum.LOCAL.getStorage(), updatedConfig, "updated");

        LocalFileClientConfig config = assertInstanceOf(LocalFileClientConfig.class,
                repository.findById(FileConfigId.of(id)).config());
        assertEquals("/data/uploads", config.getBasePath());
        assertEquals("https://static.example.com", config.getDomain());
    }

    private static Map<String, Object> localConfigMap() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("basePath", "/tmp/uploads");
        config.put("domain", "https://static.example.com");
        return config;
    }

    private static final class InMemoryFileConfigRepository implements FileConfigRepository {
        private final Map<Long, FileConfig> configs = new LinkedHashMap<>();
        private long nextId = 1L;

        @Override
        public FileConfig save(FileConfig config) {
            Long id = config.id() != null ? config.id().value() : nextId++;
            FileConfig saved = FileConfigFactory.reconstitute(id, config.name().value(), config.storage(),
                    config.master(), config.config(), config.remark());
            configs.put(id, saved);
            return saved;
        }

        @Override
        public void delete(FileConfigId id) {
            configs.remove(id.value());
        }

        @Override
        public void deleteByIds(Collection<FileConfigId> ids) {
            ids.forEach(this::delete);
        }

        @Override
        public FileConfig findById(FileConfigId id) {
            return configs.get(id.value());
        }

        @Override
        public FileConfig findByMaster() {
            return configs.values().stream().filter(FileConfig::isMaster).findFirst().orElse(null);
        }

        @Override
        public PageResult<FileConfig> findPage(FileConfigPageQuery query) {
            return new PageResult<>(findAll(), (long) configs.size());
        }

        @Override
        public List<FileConfig> findAll() {
            return new ArrayList<>(configs.values());
        }

        @Override
        public List<FileConfig> findByIds(Collection<FileConfigId> ids) {
            return ids.stream().map(this::findById).toList();
        }
    }
}
