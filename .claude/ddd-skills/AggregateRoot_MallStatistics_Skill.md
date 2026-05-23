# DDD Skill: AggregateRoot_MallStatistics_Skill

## 1. 技能名称
`AggregateRoot_MallStatistics_Skill` — 商城统计域(Statistics)聚合根的领域建模与重构技能

## 2. 适用场景
商城统计子域：商品统计(ProductStatistics)、交易统计(TradeStatistics)、交易订单统计(TradeOrderStatistics)、售后统计(AfterSaleStatistics)、分销统计(BrokerageStatistics)、钱包统计(PayWalletStatistics)。

## 3. DDD 构造块

### 3.1 聚合根
- **ProductStatistics** — 商品统计聚合根，按天+SPU粒度记录浏览量/访客数/收藏量/加购量/下单数/支付数/支付金额/访客支付转化率
- **TradeStatistics** — 交易统计聚合根，按天粒度记录订单/退款/佣金/充值的数据汇总

### 3.2 值对象
- `ProductStatisticsId` — 商品统计标识值对象
- `TradeStatisticsId` — 交易统计标识值对象
- `StatisticsDate` — 统计日期值对象（LocalDate封装，含beginOfDay/endOfDay计算）
- `BrowseConvertPercent` — 访客转化率值对象（百分比整数，0-100范围校验）
- `StatisticsSummary` — 统计汇总值对象(含订单数/支付数/支付金额/退款金额等)
- 不可变类，构造方法自校验

### 3.3 仓储接口
- `ProductStatisticsRepository` — save/findBySpuIdAndTime/findPageGroupBySpuId/findByTimeBetween/saveBatch(批量写入)/deleteByTime
- `TradeStatisticsRepository` — save/findByTimeBetween/deleteByTime/findOrderCreateCountSumAndOrderPayPriceSumByTimeBetween
- 所有Repository定义在domain层，无infrastructure imports

### 3.4 领域事件
- `ProductStatisticsUpdatedEvent` — 商品统计数据刷新事件
- `TradeStatisticsUpdatedEvent` — 交易统计数据刷新事件
- `StatisticsTaskCompletedEvent` — 统计任务完成事件

### 3.5 工厂
- `ProductStatisticsFactory` — 创建商品统计聚合根，含转化率计算、默认值初始化
- `TradeStatisticsFactory` — 创建交易统计聚合根，含多维度数据组装

### 3.6 领域服务
- `ProductStatisticsDomainService` — 商品统计计算服务（浏览转化率计算、排名计算）
- `TradeStatisticsDomainService` — 交易统计计算服务（多维度汇总：订单+售后+佣金+充值）
- `StatisticsComparisonDomainService` — 数据对比分析服务（当前周期 vs 上一周期）

## 4. 职责边界

### R01 — 商品统计数据按天+SPU唯一
每天每个SPU只有一条统计记录。二次运行统计任务前需检查是否已存在(`selectCountByTimeBetween`)，已存在则跳过。
- Source: `ProductStatisticsServiceImpl.statisticsProduct()` lines 83-91

### R02 — 商品统计的访客转化率计算
转化率 = 下单支付人数(orderPayCount) / 浏览用户数(browseUserCount) * 100。除数为0时不做计算。
- Source: `ProductStatisticsServiceImpl.statisticsProduct()` lines 105-109

### R03 — 商品统计分页批量处理
为避免商品表数据量大导致超时，每次最多处理100条记录(pageSize=100)，分批查询和插入。
- Source: `ProductStatisticsServiceImpl.statisticsProduct()` lines 97-113

### R04 — 商品统计排名默认按浏览量倒序
分页查询商品统计排名时，默认排序字段为`browseCount`降序。
- Source: `ProductStatisticsServiceImpl.getProductStatisticsRankPage()` line 45

### R05 — 商品统计数据分析支持同比对比
`getProductStatisticsAnalyse`返回当前时段数据和上一对照时段数据的`DataComparisonRespVO`，对照时段长度与当前时段相同。
- Source: `ProductStatisticsServiceImpl.getProductStatisticsAnalyse()` lines 49-61

### R06 — 交易统计按天唯一
每天一条交易统计记录。统计前检查`selectByTimeBetween`，已存在则跳过。
- Source: `TradeStatisticsServiceImpl.statisticsTrade()` lines 98-107

### R07 — 交易统计多维度汇总[重要]
每日交易统计从四个维度汇总：
1. 订单维度(`TradeOrderStatisticsService.getOrderSummary`)：下单数、支付订单数、支付金额
2. 售后维度(`AfterSaleStatisticsService.getAfterSaleSummary`)：退款单数、退款金额
3. 佣金维度(`BrokerageStatisticsService.getBrokerageSettlementPriceSummary`)：已结算佣金
4. 充值维度(`PayWalletStatisticsService.getWalletSummary`)：充值金额、充值人数
- Source: `TradeStatisticsServiceImpl.statisticsTrade()` lines 109-131

### R08 — 交易趋势支持按年月日分组
订单趋势查询：按年统计时以月份分组(`groupByMonth`)；其他(天/周/月)以天分组(`groupByDay`)。
- Source: `TradeOrderStatisticsServiceImpl.getOrderCountTrend()` lines 99-106

### R09 — 交易数据对比分析
`getTradeStatisticsAnalyse`返回当前时段与上一对照时段的`DataComparisonRespVO`。另`getOrderComparison`返回当日与昨日的对比。
- Source: `TradeStatisticsServiceImpl.getTradeStatisticsAnalyse()` lines 62-71, `TradeOrderStatisticsServiceImpl.getOrderComparison()` lines 70-74

### R10 — 售后统计按退款时间维度
售后统计的`getAfterSaleSummary`以`refundTime`为时间维度汇总退款单数和退款金额。
- Source: `AfterSaleStatisticsServiceImpl.getAfterSaleSummary()` lines 25-27

### R11 — 分销佣金统计按解冻时间维度
佣金统计的`getBrokerageSettlementPriceSummary`以`unfreezeTime`为时间维度，筛选已结算的订单佣金。
- Source: `BrokerageStatisticsServiceImpl.getBrokerageSettlementPriceSummary()` lines 26-29

### R12 — 交易统计按状态+配送类型查询
`getCountByStatusAndDeliveryType`按订单状态和配送类型(快递/自提)聚合订单数量，用于看板展示。
- Source: `TradeOrderStatisticsServiceImpl.getCountByStatusAndDeliveryType()` lines 65-68

## 5. 依赖与协作

### 5.1 模块内依赖
- `TradeStatistics` → `TradeOrderStatisticsService`: 获取订单统计汇总
- `TradeStatistics` → `AfterSaleStatisticsService`: 获取售后统计汇总
- `TradeStatistics` → `BrokerageStatisticsService`: 获取佣金统计汇总
- `TradeStatistics` → `PayWalletStatisticsService`: 获取钱包充值统计汇总

### 5.2 模块间依赖
- `ProductStatistics` → `product`: 从product模块的浏览/收藏/加购/下单/支付行为表统计数据
- `TradeStatistics` → `trade`: 从trade模块的订单/售后/佣金表统计数据
- `TradeStatistics` → `pay`: 从pay模块的钱包充值表统计数据
- `TradeOrderStatistics` → `pay`: 从pay模块的订单支付状态判断

## 6. 不变式与约束

### I01 — 数据幂等性
每日统计任务可重复执行，同一日期+SPU(或日期)的数据已存在则跳过不重复写入。
- ProductStatistics: `selectCountByTimeBetween > 0`时跳过
- TradeStatistics: `selectByTimeBetween`非null时跳过

### I02 — 转化率范围
`browseConvertPercent = orderPayCount / browseUserCount * 100`，结果在[0, 100]区间。
- 当`browseUserCount = 0`时，转化率保持null或不处理

### I03 — 对照分析时段一致性
对比分析时，对照时段长度与当前时段长度严格相等。例如7天数据对照前7天。

### I04 — 商品统计排名边界
- 默认按浏览量排序，支持其他维度排序
- 分页查询不限制数据量

## 7. 验收标准

### AC01 — 聚合根纯净性
聚合根类无MyBatis/Spring注解，不注入Mapper。
- 验证方法: grep聚合根文件确认

### AC02 — 值对象不可变性
值对象为final class或record，字段final，无setter。
- 验证方法: 检查值对象文件

### AC03 — 仓储接口在领域层
Repository定义在`domain/{aggregate}/repository/`。
- 验证方法: 检查import语句

### AC04 — 仓储实现在基础设施层
Repository实现在`infrastructure/{aggregate}/`。
- 验证方法: 确认文件位置

### AC05 — 统计任务幂等性
同一日期的统计任务重复执行不会产生重复数据。
- 验证方法: 统计任务重复执行后验证数据唯一性

### AC06 — 对比分析时段一致
对照时段长度等于当前时段长度。
- 验证方法: 检查时段计算逻辑

### AC07 — 编译通过
- 验证方法: `mvn compile -pl develop-module-mall/develop-module-statistics-server`

## 8. 目录结构规划

```
develop-module-statistics-server/src/main/java/com/develop/mvp/pk/module/statistics/
├── domain/
│   ├── productstatistics/
│   │   ├── ProductStatistics.java             (聚合根)
│   │   ├── ProductStatisticsFactory.java
│   │   ├── valueobject/
│   │   │   └── ProductStatisticsId.java
│   │   ├── repository/
│   │   │   └── ProductStatisticsRepository.java
│   │   └── event/
│   │       └── ProductStatisticsUpdatedEvent.java
│   ├── tradestatistics/
│   │   ├── TradeStatistics.java               (聚合根)
│   │   ├── TradeStatisticsFactory.java
│   │   ├── valueobject/
│   │   │   ├── TradeStatisticsId.java
│   │   │   ├── StatisticsDate.java            (value object)
│   │   │   └── StatisticsSummary.java         (value object)
│   │   ├── repository/
│   │   │   └── TradeStatisticsRepository.java
│   │   └── event/
│   │       └── TradeStatisticsUpdatedEvent.java
│   ├── service/
│   │   ├── ProductStatisticsDomainService.java
│   │   ├── TradeStatisticsDomainService.java
│   │   └── StatisticsComparisonDomainService.java
│   └── event/
│       ├── DomainEvent.java
│       └── DomainEventPublisher.java
├── application/
│   ├── ProductStatisticsApplicationService.java
│   └── TradeStatisticsApplicationService.java
├── infrastructure/productstatistics/
│   ├── ProductStatisticsRepositoryImpl.java
│   └── TradeStatisticsRepositoryImpl.java
└── convert/
```

## 9. 回滚条件

以下任一情况应回滚当前步骤：
1. 编译失败
2. 统计任务重复执行产生重复数据（幂等性失效）
3. 统计汇总数据与直接查询源表数据不一致
4. 原有管理端统计分析接口行为变化
5. 转化率计算错误（除零/精度问题）

## 10. 分步执行计划

### Step 1: 完善已有聚合根
- 1.1 审查`ProductStatistics`：补充增量更新方法(incrementBrowseCount/incrementFavoriteCount等)、转化率计算方法
- 1.2 审查`TradeStatistics`：补充多维度数据初始化方法、汇总更新方法

### Step 2: 创建领域服务
- 2.1 `ProductStatisticsDomainService` — 商品日统计计算
- 2.2 `TradeStatisticsDomainService` — 交易日统计计算（编排订单/售后/佣金/充值四个维度）
- 2.3 `StatisticsComparisonDomainService` — 对照分析计算

### Step 3: 完善仓储接口与实现
- 3.1 补齐ProductStatisticsRepository的批量查询/写入方法
- 3.2 补齐TradeStatisticsRepository的时间范围查询方法

### Step 4: 创建应用服务
- 4.1 商品统计应用服务：排名/分析/定时统计
- 4.2 交易统计应用服务：汇总/趋势/分析/定时统计

### Step 5: 验证
- 5.1 运行全部单元测试
- 5.2 编译通过
- 5.3 幂等性验证通过
