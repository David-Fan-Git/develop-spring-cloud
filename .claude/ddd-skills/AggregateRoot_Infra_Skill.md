# DDD Skill: AggregateRoot_Infra_Skill

## 1. 技能名称

`AggregateRoot_Infra_Skill` — 基础设施(Infra)模块的 DDD 领域建模与重构技能

## 2. 适用场景

Infra 基础设施管理模块的完整生命周期，覆盖以下 7 个聚合根的 CRUD + 领域逻辑：

| 聚合根 | 领域包 | 模块 | 核心职责 |
|--------|--------|------|----------|
| Config | `domain/config/` | 系统参数配置 | 参数配置的键值存储、类型管理、可见性控制 |
| DataSourceConfig | `domain/db/` | 数据源配置 | 动态数据源的连接信息管理、连通性校验 |
| FileConfig | `domain/file/` | 文件存储配置 | 文件存储客户端（本地/OSS/S3等）配置管理、Master 管理 |
| File | `domain/file/` | 文件记录 | 文件上传/下载/删除记录、路径生成、预签名 URL |
| CodegenTable (+Column) | `domain/codegen/` | 代码生成表定义 | 代码生成器的表/列定义、主子表管理、同步数据库结构 |
| ApiAccessLog | `domain/logger/` | API 访问日志 | 访问日志记录、超期清理 |
| ApiErrorLog | `domain/logger/` | API 错误日志 | 错误日志记录、处理状态流转（INIT->DONE/IGNORE）、超期清理 |

## 3. DDD 构造块

### 3.1 聚合根清单（Aggregate Roots）

#### Config — 系统参数配置

```
com.develop.mvp.pk.module.infra.domain.config.Config
```

**聚合边界**：
- Config（根实体）
- 不包含：无子实体，纯键值对聚合

**领域包**: `domain/config/`

#### DataSourceConfig — 数据源配置

```
com.develop.mvp.pk.module.infra.domain.db.DataSourceConfig
```

**聚合边界**：
- DataSourceConfig（根实体）
- 不包含：动态数据源框架配置（在 infrastructure 层管理）

**领域包**: `domain/db/`

#### FileConfig — 文件存储配置

```
com.develop.mvp.pk.module.infra.domain.file.FileConfig
```

**聚合边界**：
- FileConfig（根实体）
- 不包含：FileClient 实现（在 framework 层管理）

**领域包**: `domain/file/`

#### File — 文件记录

```
com.develop.mvp.pk.module.infra.domain.file.File
```

**聚合边界**：
- File（根实体）
- 不包含：FileConfig（通过 configId 引用）

**领域包**: `domain/file/`

#### CodegenTable + CodegenColumn — 代码生成

```
com.develop.mvp.pk.module.infra.domain.codegen.CodegenTable
com.develop.mvp.pk.module.infra.domain.codegen.CodegenColumn
```

**聚合边界**：
- CodegenTable（根实体）
- CodegenColumn（聚合内部实体，生命周期随 CodegenTable）
- 不包含：DataSourceConfig（通过 dataSourceConfigId 引用）

**领域包**: `domain/codegen/`

#### ApiAccessLog — API 访问日志

```
com.develop.mvp.pk.module.infra.domain.logger.ApiAccessLog
```

**聚合边界**：
- ApiAccessLog（根实体，只追加，不修改）

**领域包**: `domain/logger/`

#### ApiErrorLog — API 错误日志

```
com.develop.mvp.pk.module.infra.domain.logger.ApiErrorLog
```

**聚合边界**：
- ApiErrorLog（根实体，创建后仅 processStatus 可流转）

**领域包**: `domain/logger/`

### 3.2 值对象（Value Objects）

| 聚合 | 值对象 | 类名 | 封装字段 | 不可变 | 自校验 |
|------|--------|------|---------|--------|--------|
| Config | 配置ID | `ConfigId` | `Long value` | ✅ | 非空 |
| Config | 配置键 | `ConfigKey` | `String value` | ✅ | 非空、非空白 |
| Config | 配置类型 | `ConfigType` | `Integer code` | ✅ | SYSTEM/CUSTOM |
| Config | 可见性 | `ConfigVisible` | `Boolean visible` | ✅ | VISIBLE/INVISIBLE |
| DataSourceConfig | 数据源ID | `DataSourceConfigId` | `Long value` | ✅ | 非空 |
| DataSourceConfig | 数据源名称 | `DataSourceConfigName` | `String value` | ✅ | 非空 |
| DataSourceConfig | 数据源URL | `DataSourceConfigUrl` | `String value` | ✅ | JDBC URL 格式 |
| FileConfig | 文件配置ID | `FileConfigId` | `Long value` | ✅ | 非空 |
| FileConfig | 文件配置名称 | `FileConfigName` | `String value` | ✅ | 非空 |
| File | 文件ID | `FileId` | `Long value` | ✅ | 非空 |
| File | 文件配置引用 | `FileConfigRef` | `Long configId` | ✅ | 非空 |
| Codegen | 表ID | `CodegenTableId` | `Long value` | ✅ | 非空 |

### 3.3 仓储接口（Repository Interfaces）

所有仓储接口定义在领域层，不依赖任何基础设施（MyBatis/Spring）：

```java
// domain/config/repository/ConfigRepository.java
public interface ConfigRepository {
    Config save(Config config);
    void delete(ConfigId id);
    void deleteByIds(Collection<ConfigId> ids);
    Config findById(ConfigId id);
    Optional<Config> findByKey(ConfigKey key);
    boolean existsByKey(ConfigKey key);
    PageResult<Config> findPage(ConfigPageQuery query);
    List<Config> findByIds(Collection<ConfigId> ids);
    List<Config> findAll();
}

// domain/db/repository/DataSourceConfigRepository.java
public interface DataSourceConfigRepository {
    DataSourceConfig save(DataSourceConfig config);
    void delete(DataSourceConfigId id);
    DataSourceConfig findById(DataSourceConfigId id);
    List<DataSourceConfig> findAll();
}

// domain/file/repository/FileConfigRepository.java
public interface FileConfigRepository {
    FileConfig save(FileConfig config);
    void delete(FileConfigId id);
    FileConfig findById(FileConfigId id);
    FileConfig findMaster();
    List<FileConfig> findAll();
    PageResult<FileConfig> findPage(FileConfigPageQuery query);
    long count();
}

// domain/file/repository/FileRepository.java
public interface FileRepository {
    File save(File file);
    void delete(FileId id);
    void deleteByIds(Collection<FileId> ids);
    File findById(FileId id);
    List<File> findByIds(Collection<FileId> ids);
    PageResult<File> findPage(FilePageQuery query);
}

// domain/codegen/repository/CodegenRepository.java
public interface CodegenRepository {
    CodegenTable save(CodegenTable table);
    void delete(CodegenTableId id);
    CodegenTable findById(CodegenTableId id);
    List<CodegenTable> findByDataSourceConfigId(Long dataSourceConfigId);
    List<CodegenTable> findAll();
    PageResult<CodegenTable> findPage(CodegenTablePageQuery query);
    boolean existsByTableNameAndDataSource(String tableName, Long dataSourceConfigId);
    // Column 子实体通过 CodegenTable 聚合根管理
    List<CodegenColumn> findColumnsByTableId(CodegenTableId tableId);
    void saveColumns(List<CodegenColumn> columns);
    void deleteColumnsByTableId(CodegenTableId tableId);
    void deleteColumnIds(Set<Long> columnIds);
}

// domain/logger/repository/ApiAccessLogRepository.java
public interface ApiAccessLogRepository {
    void save(ApiAccessLog log);
    ApiAccessLog findById(Long id);
    PageResult<ApiAccessLog> findPage(ApiAccessLogPageQuery query);
    int deleteByCreateTimeLt(LocalDateTime expireDate, Integer limit);
}

// domain/logger/repository/ApiErrorLogRepository.java
public interface ApiErrorLogRepository {
    void save(ApiErrorLog log);
    ApiErrorLog findById(Long id);
    PageResult<ApiErrorLog> findPage(ApiErrorLogPageQuery query);
    void updateProcessStatus(Long id, Integer processStatus, Long processUserId, LocalDateTime processTime);
    int deleteByCreateTimeLt(LocalDateTime expireDate, Integer limit);
}
```

### 3.4 领域事件（Domain Events）

| 聚合 | 事件 | 触发时机 | 携带数据 | 消费者 |
|------|------|---------|---------|--------|
| Config | `ConfigCreatedEvent` | 创建配置后 | configId, configKey | 操作日志 |
| Config | `ConfigUpdatedEvent` | 更新配置后 | configId, configKey | 操作日志、缓存刷新 |
| Config | `ConfigDeletedEvent` | 删除配置后 | configId, configKey | 操作日志、缓存刷新 |
| DataSourceConfig | `DataSourceConfigCreatedEvent` | 创建数据源后 | dataSourceConfigId | 操作日志、数据源注册 |
| DataSourceConfig | `DataSourceConfigDeletedEvent` | 删除数据源后 | dataSourceConfigId | 操作日志、数据源注销 |
| FileConfig | `FileConfigCreatedEvent` | 创建文件配置后 | fileConfigId | 操作日志 |
| FileConfig | `FileConfigDeletedEvent` | 删除文件配置后 | fileConfigId | 操作日志、客户端清理 |
| FileConfig | `FileConfigMasterChangedEvent` | Master 配置变更后 | fileConfigId | 操作日志、master 缓存刷新 |
| File | `FileUploadedEvent` | 文件上传成功后 | fileId, fileName, url | 操作日志 |
| File | `FileDeletedEvent` | 文件删除后 | fileId, path, configId | 操作日志 |
| Codegen | `CodegenCreatedEvent` | 代码生成表创建后 | tableId, tableName | 操作日志 |
| Codegen | `CodegenDeletedEvent` | 代码生成表删除后 | tableId, tableName | 操作日志 |

### 3.5 工厂（Factories）

每个聚合根对应一个工厂类，职责：
- `create()`: 创建新聚合（不含 ID 的场景）
- `reconstitute()`: 从持久化数据重建聚合

```
infrastructure/{aggregate}/
  {Aggregate}Factory.java    # 工厂，create() + reconstitute()
```

## 4. 职责边界

### 4.1 各聚合根必须负责的规则

#### Config 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-C01 | 系统内置配置（type=SYSTEM）不可删除 | `ConfigServiceImpl.java:61-63` — 校验 `config.getType()` 是否为 `SYSTEM`，是则抛 `CONFIG_CAN_NOT_DELETE_SYSTEM_TYPE` |
| R-C02 | 参数配置键（key）在全局不可重复 | `ConfigServiceImpl.java:110-121` — `validateConfigKeyUnique()` 通过 `selectByKey()` 校验唯一性 |
| R-C03 | 不可见配置（visible=false）不允许返回给前端 | `ConfigServiceImpl` 调用层可见，通过 `ConfigVisible` 值对象控制 |
| R-C04 | 创建时默认 type=CUSTOM | `ConfigServiceImpl.java:39` — `config.setType(ConfigTypeEnum.CUSTOM.getType())` |

#### DataSourceConfig 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-D01 | Master（ID=0）数据源为系统内置，从动态数据源配置构建 | `DataSourceConfigServiceImpl.java:80-82` — `Objects.equals(id, DataSourceConfigDO.ID_MASTER)` 时调用 `buildMasterDataSourceConfig()` |
| R-D02 | 创建/更新时必须校验连接是否可达 | `DataSourceConfigServiceImpl.java:95-99` — `validateConnectionOK()` 使用 `JdbcUtils.isConnectionOK()` |
| R-D03 | 列表查询时，Master 数据源始终排第一 | `DataSourceConfigServiceImpl.java:91` — `result.add(0, buildMasterDataSourceConfig())` |

#### FileConfig 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-FC01 | 全局只能有一个 Master 文件配置 | `FileConfigServiceImpl.java:103-108` — `updateFileConfigMaster()` 先将所有配置设为 `master=false`，再设指定配置为 `master=true` |
| R-FC02 | Master 文件配置不可删除 | `FileConfigServiceImpl.java:129-131` — `if (Boolean.TRUE.equals(config.getMaster()))` 抛 `FILE_CONFIG_DELETE_FAIL_MASTER` |
| R-FC03 | 创建时默认非 Master | `FileConfigServiceImpl.java:81-82` — `.setMaster(false)` |
| R-FC04 | 更新文件配置后需清空 `FileClient` 缓存 | `FileConfigServiceImpl.java:97` — `clearCache()` 使 `clientCache` 失效 |
| R-FC05 | 文件存储配置变更时需校验配置参数有效性（JSON 反序列化 + Validation） | `FileConfigServiceImpl.java:114-123` — `parseClientConfig()` 完成 `JsonUtils.parseObject2()` + `ValidationUtils.validate()` |

#### File 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-F01 | 文件上传始终使用 Master 文件客户端 | `FileServiceImpl.java:92-93` — `fileConfigService.getMasterFileClient()` |
| R-F02 | 文件路径生成需保证唯一性（日期前缀 + 可选时间戳后缀） | `FileServiceImpl.java:104-138` — `generateUploadPath()` 包含日期前缀和时间戳后缀 |
| R-F03 | type 为空时需从内容推导 MIME 类型 | `FileServiceImpl.java:74-75` — `FileTypeUtils.getMineType(content, name)` |
| R-F04 | name 为空时使用 SHA256 作为文件名 | `FileServiceImpl.java:78-79` — `DigestUtil.sha256Hex(content)` |
| R-F05 | name 无后缀时补充 MIME 类型对应后缀 | `FileServiceImpl.java:81-87` — `FileTypeUtils.getExtension(type)` |
| R-F06 | 删除文件时需同时从对象存储删除底层文件 | `FileServiceImpl.java:179-181` — `client.delete(file.getPath())` |

#### CodegenTable 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-G01 | 同一数据源下不允许重复导入表 | `CodegenServiceImpl.java:88-91` — `selectByTableNameAndDataSourceConfigId()` 校验已存在则 `throw CODEGEN_TABLE_EXISTS` |
| R-G02 | 导入时必须校验表注释、字段、字段注释非空 | `CodegenServiceImpl.java:112-127` — `validateTableInfo()` |
| R-G03 | 更新主子表时，主表/子表/关联字段必须存在 | `CodegenServiceImpl.java:137-145` — 模板类型为 SUB 时校验 `masterTableId` 和 `subJoinColumnId` |
| R-G04 | 主子表代码生成时，子表必须存在且关联字段有效 | `CodegenServiceImpl.java:275-291` — 主表模板类型校验 `subTables` 非空及关联字段 |
| R-G05 | 代码生成时，除子表外必须有字段 | `CodegenServiceImpl.java:268-269` — `CollUtil.isEmpty(columns)` 抛 `CODEGEN_COLUMN_NOT_EXISTS` |
| R-G06 | 从数据库同步时，无变化则跳过 | `CodegenServiceImpl.java:204-206` — 无新增/删除字段时抛 `CODEGEN_SYNC_NONE_CHANGE` |
| R-G07 | 同步时对比 JDBC 类型、可空性、主键、注释、排序号确定变更字段 | `CodegenServiceImpl.java:180-196` — `primaryKeyPredicate` 逐字段比较 |
| R-G08 | 删除表定义时，同步删除其所有列定义 | `CodegenServiceImpl.java:228` — `codegenColumnMapper.deleteListByTableId(tableId)` |
| R-G09 | 创建时默认 scene=ADMIN、frontType 使用全局配置 | `CodegenServiceImpl.java:96-97` — |
| R-G10 | 无主键表使用第一个字段作为主键 | `CodegenServiceImpl.java:104-105` — `columns.get(0).setPrimaryKey(true)` |

#### ApiErrorLog 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-E01 | 创建时默认 processStatus=INIT | `ApiErrorLogServiceImpl.java:40-41` — `.setProcessStatus(ApiErrorLogProcessStatusEnum.INIT.getStatus())` |
| R-E02 | 处理时只能从 INIT 状态流转到 DONE/IGNORE | `ApiErrorLogServiceImpl.java:72-73` — 非 INIT 状态抛 `API_ERROR_LOG_PROCESSED` |
| R-E03 | 兜底处理：日志记录异常时仅打印日志，不抛异常 | `ApiErrorLogServiceImpl.java:50-53` — catch Exception，`log.error()` |
| R-E04 | 周期性清理超过指定天数的日志 | `ApiErrorLogServiceImpl.java:82-95` — `cleanErrorLog()` 循环删除 |
| R-E05 | 请求参数字段超过最大长度时截断 | `ApiErrorLogServiceImpl.java:42` — `StrUtils.maxLength()` |

#### ApiAccessLog 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-A01 | 请求参数和结果消息超过最大长度时截断 | `ApiAccessLogServiceImpl.java:38-39` — `StrUtils.maxLength()` |
| R-A02 | 周期性清理超过指定天数的日志 | `ApiAccessLogServiceImpl.java:60-73` — `cleanAccessLog()` 循环删除 |
| R-A03 | 无租户上下文时忽略租户插入 | `ApiAccessLogServiceImpl.java:43-45` — `TenantUtils.executeIgnore()` |

### 4.2 严禁外泄的职责

| 禁止行为 | 原因 | 应由谁处理 |
|---------|------|----------|
| 直接调用 Mapper/操作 DO | 破坏持久化无关性 | RepositoryImpl |
| 直接操作 FileClient（文件存储客户端） | 基础设施框架关注点 | ApplicationService 委托 FileClientFactory |
| 处理 JSON 序列化/反序列化 | 基础设施关注点 | ApplicationService/Convert |
| 管理 DataSource 注册/注销 | 动态数据源框架关注点 | Infrastructure 层 |
| 处理代码生成模板引擎 | 基础设施关注点 | CodegenEngine（infrastructure） |
| 处理缓存失效逻辑 | 基础设施关注点 | ApplicationService |
| 租户上下文处理 | 横切关注点 | 拦截器/ApplicationService |
| Excel 导入导出 | 表示层关注点 | Controller/Convert |

## 5. 依赖与协作

### 5.1 领域层依赖（向内）

每个聚合根仅依赖：
- 自身值对象
- 仓储接口（Repository）
- 领域事件接口（DomainEventPublisher）

### 5.2 跨聚合协作（仅通过 ID 引用）

| 源聚合 | 目标聚合 | 引用方式 | 协作场景 |
|-------|---------|---------|---------|
| File | FileConfig | `configId: Long` | 上传时通过 configId 获取 FileClient |
| CodegenTable | DataSourceConfig | `dataSourceConfigId: Long` | 生成代码时获取数据库方言 |

### 5.3 基础设施依赖（向外，通过接口倒置）

```
领域层定义接口                         基础设施层实现
─────────────                         ──────────────
ConfigRepository          ←──         ConfigRepositoryImpl (委托 ConfigMapper)
DataSourceConfigRepository ←──        DataSourceConfigRepositoryImpl (委托 DataSourceConfigMapper)
FileConfigRepository      ←──         FileConfigRepositoryImpl (委托 FileConfigMapper)
FileRepository            ←──         FileRepositoryImpl (委托 FileMapper)
CodegenRepository         ←──         CodegenRepositoryImpl (委托 CodegenTableMapper + CodegenColumnMapper)
ApiAccessLogRepository    ←──         ApiAccessLogRepositoryImpl (委托 ApiAccessLogMapper)
ApiErrorLogRepository     ←──         ApiErrorLogRepositoryImpl (委托 ApiErrorLogMapper)
DomainEventPublisher      ←──         SpringDomainEventPublisher (委托 Spring ApplicationEventPublisher)
```

## 6. 不变式与约束（Invariants）

| 编号 | 不变式 | 聚合 | 类型 | 验证点 |
|------|--------|------|------|--------|
| I01 | Config 的 key 在全局不可重复 | Config | 跨聚合唯一性 | 创建/修改时 |
| I02 | SYSTEM 类型的 Config 永久存在，不可删除 | Config | 聚合内部 | 删除时 |
| I03 | Config 的 type 只能是 SYSTEM(1) 或 CUSTOM(2) | Config | 值对象 | 创建/修改时 |
| I04 | 全局有且仅有一个 Master FileConfig | FileConfig | 跨聚合唯一性 | 设置 Master 时 |
| I05 | Master FileConfig 不可删除 | FileConfig | 聚合内部 | 删除时 |
| I06 | File 删除时，底层存储文件必须同时删除 | File | 聚合外部 | 删除时 |
| I07 | 同一数据源下 CodegenTable 的表名不可重复 | Codegen | 跨聚合唯一性 | 导入时 |
| I08 | 主子表模板中，子表必须通过 subJoinColumnId 关联主表 | Codegen | 聚合间约束 | 更新/生成时 |
| I09 | 代码生成时，CodegenTable 必须有至少一个 CodegenColumn | Codegen | 聚合内部 | 生成时 |
| I10 | ApiErrorLog 的 processStatus 只能从 INIT 流转到 DONE/IGNORE，不可逆行 | ApiErrorLog | 聚合内部 | 处理时 |
| I11 | ApiAccessLog/ApiErrorLog 的记录操作异常不可影响主业务流程 | 日志 | 应用层约束 | 创建时 |
| I12 | DataSourceConfig 创建/更新时必须可连接 | DataSourceConfig | 聚合内部 | 创建/修改时 |
| I13 | Master 数据源（ID=0）由基础设施管理，不可通过 DB 操作 | DataSourceConfig | 基础设施约束 | 查询/列表时 |
| I14 | 从 DB 同步 Codegen 字段时，已有字段的 ID 不可变更 | Codegen | 聚合内部 | 同步时 |
| I15 | 文件路径生成必须保证唯一性，避免覆盖 | File | 聚合内部 | 上传时 |

## 7. 验收标准

| 编号 | 验收标准 | 验证方法 |
|------|---------|---------|
| AC01 | Config、DataSourceConfig、FileConfig、File、CodegenTable、CodegenColumn、ApiAccessLog、ApiErrorLog 均无 MyBatis 注解（`@TableName`、`@TableId`等） | 代码审查 |
| AC02 | 所有聚合根无 Spring 注解（`@Component`、`@Service` 等） | 代码审查 |
| AC03 | 所有值对象是 final class，字段是 final，无 setter | 代码审查 |
| AC04 | 所有仓储接口在 `domain/{aggregate}/repository/` 包中，不 import MyBatis 类 | 代码审查 |
| AC05 | 所有 RepositoryImpl 在 `infrastructure/{aggregate}/` 包中，import MyBatis 类并负责 DO 与领域模型映射 | 代码审查 |
| AC06 | ApplicationService 在 `application/{aggregate}/` 包中，使用 `@Transactional` 管理事务 | 代码审查 |
| AC07 | 领域事件由 ApplicationService 的 `save()` 后统一发布 | 代码审查 |
| AC08 | ConfigServiceImpl/FileConfigServiceImpl 等旧 Service 仅保留编排逻辑，所有业务规则迁移到对应聚合根或值对象 | 代码审查 |
| AC09 | FileClient 的获取委托给 FileConfigRepository，不直接在 File 聚合中操作 | 代码审查 |
| AC10 | CodegenServiceImpl 的 `validateTableInfo()` 等校验逻辑迁移到 CodegenTable 聚合根 | 代码审查 |
| AC11 | ApiErrorLog 的 `processStatus` 状态流转封装为 `markProcessed()/markIgnored()` 业务方法 | 代码审查 |
| AC12 | 日志记录的兜底异常处理（catch Exception 仅打印日志）在 ApplicationService 层完成 | 代码审查 |
| AC13 | Config 的可见性控制由 `ConfigVisible` 值对象的 `isVisible()` 方法负责 | 代码审查 |
| AC14 | ConfigKey 值对象内封装 key 的格式校验（非空、非空白） | 代码审查 |
| AC15 | 编译通过，原有 Controller 行为无回归 | 运行测试 |
| AC16 | ConfigController、FileController 等控制器的接口返回与重构前一致 | 集成测试 |

## 8. 目录结构规划（重构后）

```
develop-module-infra/develop-module-infra-server/src/main/java/com/develop/mvp/pk/module/infra/
├── domain/                                              # 领域层
│   ├── config/
│   │   ├── Config.java                                  # 聚合根
│   │   ├── valueobject/
│   │   │   ├── ConfigId.java
│   │   │   ├── ConfigKey.java
│   │   │   ├── ConfigType.java
│   │   │   └── ConfigVisible.java
│   │   ├── event/
│   │   │   ├── ConfigCreatedEvent.java
│   │   │   ├── ConfigUpdatedEvent.java
│   │   │   └── ConfigDeletedEvent.java
│   │   └── repository/
│   │       ├── ConfigRepository.java
│   │       └── ConfigPageQuery.java
│   ├── db/
│   │   ├── DataSourceConfig.java                        # 聚合根
│   │   ├── valueobject/
│   │   │   ├── DataSourceConfigId.java
│   │   │   ├── DataSourceConfigName.java
│   │   │   └── DataSourceConfigUrl.java
│   │   ├── event/
│   │   │   ├── DataSourceConfigCreatedEvent.java
│   │   │   └── DataSourceConfigDeletedEvent.java
│   │   └── repository/
│   │       └── DataSourceConfigRepository.java
│   ├── file/
│   │   ├── FileConfig.java                              # 聚合根
│   │   ├── File.java                                    # 聚合根
│   │   ├── valueobject/
│   │   │   ├── FileConfigId.java
│   │   │   ├── FileConfigName.java
│   │   │   └── FileId.java
│   │   ├── event/
│   │   │   ├── FileConfigCreatedEvent.java
│   │   │   ├── FileConfigDeletedEvent.java
│   │   │   ├── FileConfigMasterChangedEvent.java
│   │   │   ├── FileUploadedEvent.java
│   │   │   └── FileDeletedEvent.java
│   │   └── repository/
│   │       ├── FileConfigRepository.java
│   │       ├── FileConfigPageQuery.java
│   │       ├── FileRepository.java
│   │       └── FilePageQuery.java
│   ├── codegen/
│   │   ├── CodegenTable.java                            # 聚合根
│   │   ├── CodegenColumn.java                           # 聚合内部实体
│   │   ├── event/
│   │   │   ├── CodegenCreatedEvent.java
│   │   │   └── CodegenDeletedEvent.java
│   │   └── repository/
│   │       ├── CodegenRepository.java
│   │       └── CodegenTablePageQuery.java
│   └── logger/
│       ├── ApiAccessLog.java                            # 聚合根
│       ├── ApiErrorLog.java                             # 聚合根
│       └── repository/
│           ├── ApiAccessLogRepository.java
│           ├── ApiAccessLogPageQuery.java
│           ├── ApiErrorLogRepository.java
│           └── ApiErrorLogPageQuery.java
├── application/                                         # 应用层
│   ├── config/
│   │   └── ConfigApplicationService.java
│   ├── db/
│   │   └── DataSourceConfigApplicationService.java
│   ├── file/
│   │   ├── FileConfigApplicationService.java
│   │   └── FileApplicationService.java
│   ├── codegen/
│   │   └── CodegenApplicationService.java
│   └── logger/
│       ├── ApiAccessLogApplicationService.java
│       └── ApiErrorLogApplicationService.java
├── infrastructure/                                      # 基础设施层
│   ├── config/
│   │   └── ConfigRepositoryImpl.java
│   ├── db/
│   │   └── DataSourceConfigRepositoryImpl.java
│   ├── file/
│   │   ├── FileConfigRepositoryImpl.java
│   │   └── FileRepositoryImpl.java
│   ├── codegen/
│   │   └── CodegenRepositoryImpl.java
│   └── logger/
│       ├── ApiAccessLogRepositoryImpl.java
│       └── ApiErrorLogRepositoryImpl.java
├── controller/                                          # 接口层（保留）
│   ├── admin/config/
│   ├── admin/db/
│   ├── admin/file/
│   ├── admin/codegen/
│   └── admin/logger/
├── dal/                                                 # 数据访问层（保留，重构为 RepositoryImpl 的底层委托）
│   ├── dataobject/
│   └── mysql/
└── convert/                                             # 转换层（保留）
```

## 9. 回滚条件

如果以下任一情况发生，应回滚当前修改并重新分析：

1. 编译失败（修改后的 `ConfigApplicationService` 等无法被 Controller 注入）
2. 聚合根内部注入了 Mapper/DO 等基础设施依赖
3. 值对象存在 setter 或可变字段
4. 业务规则从聚合根泄漏回旧的 Service 层
5. 跨聚合操作（如 File 删除时也需操作 FileConfig）未通过领域事件或仓储协作解耦
6. ConfigController.getConfigByKey() 等查询接口返回格式改变（影响前端）
7. 文件上传/下载行为出现回归（文件内容、路径、预签名 URL 等）
8. 代码生成功能无法正常生成代码
9. ApiErrorLog/ApiAccessLog 的记录丢失或兜底处理失效
10. 旧模块单元测试回归失败

## 10. 分步执行计划

按聚合根复杂度排序，从独立简单聚合开始，降低风险：

### 阶段 1：ApiAccessLog + ApiErrorLog（最简单，纯记录型）
- 值对象创建（ApiAccessLogId、ApiErrorLogId 等）
- 聚合根创建（封装 processStatus 流转、createTime 等）
- 仓储接口 + RepositoryImpl
- 保留现有清理逻辑（cleanAccessLog/cleanErrorLog）在 ApplicationService 中

### 阶段 2：Config（独立型）
- ConfigId、ConfigKey、ConfigType、ConfigVisible 值对象
- Config 聚合根（updateProfile、isSystemType、markDeleted、pullEvents）
- ConfigRepository 接口 + ConfigRepositoryImpl
- ConfigApplicationService 编排

### 阶段 3：DataSourceConfig（独立型）
- DataSourceConfigId、DataSourceConfigName、DataSourceConfigUrl 值对象
- DataSourceConfig 聚合根
- DataSourceConfigRepository
- 注意：Master 数据源（ID=0）不存储到 DB，由 ApplicationService 处理

### 阶段 4：FileConfig + File（相依型）
- FileConfigId、FileConfigName、FileId 值对象
- FileConfig 聚合根（master 管理）
- File 聚合根（path 生成封装）
- FileConfigRepository、FileRepository
- FileApplicationService 编排上传/删除（涉及跨 File-FileConfig 协作）

### 阶段 5：CodegenTable + CodegenColumn（复杂主子表型）
- CodegenTableId 值对象
- CodegenTable 聚合根 + CodegenColumn 子实体
- CodegenRepository（管理 table + column 生命周期）
- CodegenApplicationService（编排同步、生成等复杂逻辑）

### 阶段 6：适配 Controller，精简旧 Service
- Controller 注入 ApplicationService 替代旧 Service
- 移除从旧 Service 迁移到聚合根的重复逻辑
- 更新 Convert 层（DO 与领域模型互转）

### 阶段 7：编译验证 + 集成测试
- 全量编译
- 运行原有单元测试
- 手动测试关键业务路径（上传、代码生成、日志处理）
