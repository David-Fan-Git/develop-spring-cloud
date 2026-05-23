# DDD Skill: AggregateRoot_Infra_Skill

## 1. 技能名称
`AggregateRoot_Infra_Skill` — 基础设施(Infra)模块的 DDD 领域建模与重构技能

## 2. 适用场景
Infra 基础设施管理：配置管理(Config)、数据源配置(DataSourceConfig)、文件存储配置(FileConfig)、文件上传(File)、代码生成(CodegenTable/CodegenColumn)、日志管理(ApiAccessLog/ApiErrorLog)。

## 3. 聚合根清单

| 聚合根 | 领域包 | 核心职责 | 业务规则 |
|--------|--------|----------|----------|
| Config | domain/config/ | 系统配置管理 | R01: SYSTEM 不可删, R02: key 唯一, R03: 不可见不返回 |
| DataSourceConfig | domain/db/ | 数据源连接配置 | R01: Master(0L) 为内置数据源 |
| FileConfig | domain/file/ | 文件存储配置 | R01: 只能一个 Master, R02: Master 不可删 |
| File | domain/file/ | 文件上传记录 | 上传/下载/删除文件记录 |
| CodegenTable(+Column) | domain/codegen/ | 代码生成表定义 | 包含 CodegenColumn 子实体，主子表模板 |
| ApiAccessLog | domain/logger/ | API 访问日志 | 周期性清理 |
| ApiErrorLog | domain/logger/ | API 错误日志 | ProcessStatus 状态流转 |

## 4. DDD 构造块

### 4.1 聚合根 (Aggregate Root)
- `Config` — 参数配置聚合根 (final class, 无 MyBatis/Spring 注解)
- `DataSourceConfig` — 数据源配置聚合根
- `FileConfig` — 文件存储配置聚合根
- `File` — 文件聚合根
- `CodegenTable` — 代码生成表定义聚合根，包含 CodegenColumn 子实体
- `ApiAccessLog` — API 访问日志聚合根
- `ApiErrorLog` — API 错误日志聚合根

### 4.2 值对象 (Value Object)
- ConfigId, ConfigKey, ConfigType, ConfigVisible
- DataSourceConfigId, DataSourceConfigName, DataSourceConfigUrl
- FileConfigId, FileConfigName
- FileId, FileConfigId
- CodegenTableId
- 所有值对象: final class, final fields, 无 setter, 自校验

### 4.3 仓储接口 (Repository Interface)
- ConfigRepository, DataSourceConfigRepository
- FileConfigRepository, FileRepository
- CodegenRepository
- ApiAccessLogRepository, ApiErrorLogRepository
- 定义在 domain 层，无基础设施依赖

### 4.4 领域事件 (Domain Event)
- ConfigCreatedEvent, ConfigUpdatedEvent, ConfigDeletedEvent
- DataSourceConfigCreatedEvent, DataSourceConfigDeletedEvent
- FileConfigCreatedEvent, FileConfigDeletedEvent, FileConfigMasterChangedEvent
- FileUploadedEvent, FileDeletedEvent
- CodegenCreatedEvent, CodegenDeletedEvent
- 事件基类: `DomainEvent` (interface, occurredAt())
- 事件发布器: `DomainEventPublisher` (interface)

## 5. 三层架构

### Domain 层 (领域层)
```
domain/{aggregate}/
  {Aggregate}.java              # 聚合根，POJO，无 MyBatis/Spring 注解
  valueobject/                   # 值对象
    {Aggregate}Id.java
    ...
  repository/                    # 仓储接口
    {Aggregate}Repository.java
    {Aggregate}PageQuery.java
  event/                         # 领域事件
    {Aggregate}CreatedEvent.java
    ...
```

### Infrastructure 层 (基础设施层)
```
infrastructure/{aggregate}/
  {Aggregate}RepositoryImpl.java  # MyBatis 实现，DO Domain 映射
  {Aggregate}Factory.java         # 工厂，create() + reconstitute()
```

### Application 层 (应用层)
```
application/{aggregate}/
  {Aggregate}ApplicationService.java  # @Service, @Transactional
```

## 6. 关键模式示例

### Config 聚合根
```java
public final class Config {
    private final ConfigId id;
    private final ConfigKey key;
    private String value;
    private final List<DomainEvent> events = new ArrayList<>();

    Config(ConfigId id, ConfigKey key, ...) { ... }  // 包级构造器

    public void updateProfile(...) { ... events.add(new ConfigUpdatedEvent(...)); }
    public boolean isSystemType() { return type.isSystem(); }
    public ConfigId id() { return id; }
    public List<DomainEvent> pullEvents() { ... }
}
```

### Config 工厂
```java
public final class ConfigFactory {
    private ConfigFactory() {}
    public static Config create(Long id, String key, ...) { ... }
    public static Config reconstitute(Long id, String key, ...) { ... }
}
```

### Config 仓储实现
```java
@Repository
public class ConfigRepositoryImpl implements ConfigRepository {
    private final ConfigMapper configMapper;

    public Config save(Config config) {
        ConfigDO configDO = toDataObject(config);
        if (configMapper.selectById(config.id().value()) == null) {
            configMapper.insert(configDO);
        } else {
            configMapper.updateById(configDO);
        }
        return config;
    }

    private ConfigDO toDataObject(Config config) { ... }
    private Config toDomain(ConfigDO configDO) {
        return ConfigFactory.reconstitute(configDO.getId(), ...);
    }
}
```

### Config 应用服务
```java
@Service
public class ConfigApplicationService {
    private final ConfigRepository configRepository;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public Long createConfig(String key, String value, ...) {
        assertKeyUnique(ConfigKey.of(key), null);
        Config config = ConfigFactory.create(null, key, value, ...);
        config = configRepository.save(config);
        config.markCreated();
        publishEvents(config);
        return config.id().value();
    }

    private void publishEvents(Config config) {
        for (var event : config.pullEvents()) {
            eventPublisher.publish(event);
        }
    }
}
```

## 7. 领域事件模式
```java
// 接口
public interface DomainEvent { LocalDateTime occurredAt(); }
public interface DomainEventPublisher { void publish(DomainEvent event); }

// 事件记录
public record ConfigCreatedEvent(Long configId, String configKey, LocalDateTime occurredAt)
        implements DomainEvent {
    public ConfigCreatedEvent(Long configId, String configKey) {
        this(configId, configKey, LocalDateTime.now());
    }
}
```

## 8. 职责边界
- **聚合负责**: 配置校验(唯一性/类型)、文件元数据、数据源连接信息、代码生成表/列定义、日志处理状态
- **应用服务负责**: 跨聚合编排、事务管理、校验编排、领域事件发布
- **基础设施负责**: MyBatis/DO 映射、FileClient 等框架集成
- **严禁外泄**: 聚合根不 import MyBatis/Spring 类; Repository 接口不依赖基础设施

## 9. 验收标准
- AC01: 聚合根无 MyBatis 注解(@TableName/@TableId)
- AC02: 聚合根无 Spring 注解(@Component/@Service)
- AC03: 值对象是 final class
- AC04: 值对象字段是 final，无 setter
- AC05: Repository 接口不 import MyBatis 类
- AC06: RepositoryImpl 在 infrastructure 包
- AC07: ApplicationService 在 application 包
- AC08: 控制器使用 ApplicationService 注入
- AC09: @Transactional 在 ApplicationService 方法上
- AC10: 领域事件由 ApplicationService 发布
