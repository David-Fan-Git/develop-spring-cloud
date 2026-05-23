# DDD Skill: AggregateRoot_MallPromotion_Skill

## 1. 技能名称
`AggregateRoot_MallPromotion_Skill` — 商城营销域(Promotion)聚合根的领域建模与重构技能

## 2. 适用场景
商城营销子域：Banner、秒杀活动(SeckillActivity)、优惠券模板(CouponTemplate)、优惠券(Coupon)、限时折扣(DiscountActivity)、满减送(RewardActivity)、拼团(CombinationActivity)、砍价(BargainActivity)、积分商城(PointActivity)。

## 3. DDD 构造块

### 3.1 聚合根
- **Banner** — 首页Banner聚合根，含位置(position)管理、启用/禁用、点击次数统计
- **SeckillActivity** — 秒杀活动聚合根（旗舰级），含时段配置(configIds)、活动商品管理、库存管理、单次限购
- **CouponTemplate** — 优惠券模板聚合根，含领取方式(takeType)、有效期类型、商品范围、发放/使用计数
- **Coupon** — 优惠券实例聚合根，含状态(UNUSED/USED/EXPIRE)流转、有效期、使用链路
- **DiscountActivity** — 限时折扣活动聚合根，含多商品折扣配置、时间范围、启用/禁用
- **RewardActivity** — 满减送活动聚合根，含多级规则(rules)、商品范围(ALL/SPU/CATEGORY)、时间范围
- **CombinationActivity** — 拼团活动聚合根，含商品配置、开团/参团人数限制、时间范围
- **BargainActivity** — 砍价活动聚合根，含砍价配置(最低价/帮砍人数)、库存管理
- **PointActivity** — 积分商城活动聚合根，含积分商品配置、单次限购、库存管理

### 3.2 值对象
- `BannerId`, `BannerPosition` — Banner标识与位置值对象
- `SeckillActivityId`, `SeckillProduct` — 秒杀活动标识与商品值对象（含skuId/stock/price）
- `CouponTemplateId`, `CouponId` — 优惠券模板与实例标识
- `DiscountProduct` — 折扣商品值对象（含spuId/skuId/discountType/discountPercent等）
- `RewardRule` — 满减送规则值对象（含conditionType/conditionValue/discountType/discountValue）
- `CombinationProduct` — 拼团商品值对象（含skuId/price/stock）
- `PointProduct` — 积分商品值对象（含spuId/skuId/point/stock/count限购）
- `ProductScope` — 商品范围值对象（scope类型 + scopeValues列表）
- 不可变类，构造方法自校验

### 3.3 仓储接口
- `BannerRepository` — findById/save/delete/findByPosition/updateBrowseCount
- `SeckillActivityRepository` — findById/save/delete/page/findBySpuIdAndStatus/findByConfigId/updateStockDecr(乐观锁)
- `CouponTemplateRepository` — findById/save/delete/page/findByTakeType/updateTakeCount(乐观锁)
- `CouponRepository` — findById/save/delete/findByUserIdAndTemplateId/updateByIdAndStatus/批量查询
- `DiscountActivityRepository` — findById/save/delete/page/findActiveBySkuIds
- `RewardActivityRepository` — findById/save/delete/page/findMatchBySpuIdAndTime
- `CombinationActivityRepository` — findById/save/delete/page/findBySpuIdAndStatus
- `BargainActivityRepository` — findById/save/delete/page/findBySpuIdAndStatus/updateStock(乐观锁)
- `PointActivityRepository` — findById/save/delete/page/updateStockDecr(乐观锁)
- 所有Repository定义在domain层，无infrastructure imports

### 3.4 领域事件
- `SeckillActivityStatusChangedEvent` — 秒杀活动状态变更(启用/关闭)
- `SeckillActivityStockChangedEvent` — 秒杀活动库存变更
- `CouponTemplateStatusChangedEvent` — 优惠券模板状态变更
- `CouponUsedEvent` — 优惠券使用事件
- `CouponExpiredEvent` — 优惠券过期事件
- `DiscountActivityStatusChangedEvent` — 限时折扣状态变更
- `RewardActivityStatusChangedEvent` — 满减送状态变更
- `CombinationActivityStatusChangedEvent` — 拼团活动状态变更
- `BargainActivityStatusChangedEvent` — 砍价活动状态变更
- `PointActivityStatusChangedEvent` — 积分商城活动状态变更

### 3.5 工厂
- `BannerFactory` — 创建Banner聚合根
- `SeckillActivityFactory` — 创建秒杀活动聚合根，含商品列表初始化、时间校验
- `CouponTemplateFactory` — 创建优惠券模板聚合根，含商品范围校验
- `DiscountActivityFactory` — 创建限时折扣聚合根，含商品冲突校验
- `RewardActivityFactory` — 创建满减送聚合根，含规则初始化、时间范围校验
- `CombinationActivityFactory` — 创建拼团活动聚合根
- `BargainActivityFactory` — 创建砍价活动聚合根
- `PointActivityFactory` — 创建积分商城活动聚合根

### 3.6 领域服务
- `SeckillConflictDomainService` — 秒杀活动商品冲突校验（同SPU不可同时参与多个时段冲突的秒杀活动）
- `PromotionProductScopeDomainService` — 活动商品范围校验服务（SPU/CATEGORY/ALL存在性和有效性）
- `RewardConflictDomainService` — 满减送活动时间+商品范围冲突判别（时间重叠时的商品范围逐级校验）

## 4. 职责边界

### R01 — Banner必须存在才能操作
所有Banner的更新/删除/增加点击次数操作前都需校验存在性。
- Source: `BannerServiceImpl.validateBannerExists()` line 57-61

### R02 — 创建秒杀活动必须校验时段冲突
同一SPU在同一秒杀时段(configIds)不能参与多个秒杀活动。需查询所有ENABLE状态的秒杀活动并比对configIds交集。
- Source: `SeckillActivityServiceImpl.validateProductConflict()` lines 95-109

### R03 — 秒杀活动已关闭(DISABLE)状态不能更新
更新活动时校验当前状态是否为DISABLE，如果是则抛出异常`SECKILL_ACTIVITY_UPDATE_FAIL_STATUS_CLOSED`。
- Source: `SeckillActivityServiceImpl.updateSeckillActivity()` lines 139-141

### R04 — 秒杀活动库存扣减使用乐观锁防超卖
`updateStockDecr`更新活动库存和商品库存，返回0表示库存不足抛出`SECKILL_ACTIVITY_UPDATE_STOCK_FAIL`。
- Source: `SeckillActivityServiceImpl.updateSeckillStockDecr()` lines 160-183

### R05 — 秒杀活动删除前必须已关闭
仅DISABLE状态或已过期活动可删除。
- Source: `SeckillActivityServiceImpl.deleteSeckillActivity()` lines 243-244

### R06 — 秒杀活动参与资格校验[重要]
参与秒杀需校验五要素：①活动存在 ②活动已启用 ③在活动时间范围内 ④在秒杀时段配置时间范围内 ⑤不超过单次限购数量
- Source: `SeckillActivityServiceImpl.validateJoinSeckill()` lines 295-326

### R07 — 优惠券模板商品范围校验
SPU范围：通过`ProductSpuApi.validateSpuList()`校验SPU存在性；CATEGORY范围：通过`ProductCategoryApi.validateCategoryList()`校验分类存在性
- Source: `CouponTemplateServiceImpl.validateProductScope()` lines 107-113

### R08 — 优惠券模板总发放数下限保护
更新模板时，如果领取方式为USER且总发放数非不限，则总发放数不能小于已领取数`takeCount`。
- Source: `CouponTemplateServiceImpl.updateCouponTemplate()` lines 70-74

### R09 — 优惠券使用状态严格校验
使用前校验状态必须为UNUSED且有效期包含当前时间。使用后更新为USED。
- Source: `CouponServiceImpl.useCoupon()` lines 59-77

### R10 — 优惠券退还逻辑
退还后如果已过期则置为EXPIRE状态，否则恢复UNUSED状态。使用CAS方式`updateByIdAndStatus`更新。
- Source: `CouponServiceImpl.returnUsedCoupon()` lines 80-102

### R11 — 优惠券领取防超量（每人限领）
`removeTakeLimitUser()`过滤已达领取上限的用户，通过查询用户已领取数量与模板`takeLimitCount`比较。
- Source: `CouponServiceImpl.removeTakeLimitUser()` lines 306-318

### R12 — 优惠券领取时校验模板有效性
校验：①领取方式匹配 ②总发放数未超(USER领取) ③固定有效期类型未过期
- Source: `CouponServiceImpl.validateCouponTemplateCanTake()` lines 272-298

### R13 — 限时折扣活动商品冲突校验
同一SPU不能同时参与多个ENABLE状态的限时折扣活动。通过查询全部活动的商品列表做SPU交集判断。
- Source: `DiscountActivityServiceImpl.validateDiscountActivityProductConflicts()` lines 128-149

### R14 — 限时折扣已关闭不能修改/删除
CLOSE状态的活动不能update(`DISCOUNT_ACTIVITY_UPDATE_FAIL_STATUS_CLOSED`)；ENABLE状态的活动不能delete(`DISCOUNT_ACTIVITY_DELETE_FAIL_STATUS_NOT_CLOSED`)。
- Source: `DiscountActivityServiceImpl.updateDiscountActivity()` lines 80-81, `deleteDiscountActivity()` line 188

### R15 — 满减送活动时间+商品范围严格冲突检测[重要]
同时间段内不能存在商品范围重叠的满减送活动。校验策略：
- 时间不重叠则不冲突
- 商品范围为ALL的活动与任何活动冲突
- CATEGORY范围：分类交集非空即冲突
- SPU范围：SPU交集非空即冲突
- CATEGORY vs SPU：SPU的分类在CATEGORY列表中即冲突
- Source: `RewardActivityServiceImpl.validateRewardActivitySpuConflicts()` lines 117-175

### R16 — 拼团活动SPU唯一性
同一SPU不能同时参与多个ENABLE状态的拼团活动。
- Source: `CombinationActivityServiceImpl.validateProductConflict()` lines 82-93

### R17 — 砍价活动库存乐观锁扣减
`updateStock`方法通过SQL层原子扣减，返回0表示库存不足。
- Source: `BargainActivityServiceImpl.updateBargainActivityStock()` lines 84-94

### R18 — 砍价活动参与资格校验
参与砍价需校验：①活动存在 ②已启用 ③库存>0 ④在活动时间范围内
- Source: `BargainActivityServiceImpl.validateBargainActivityCanJoin()` lines 161-176

### R19 — 积分商城活动商品冲突
同一商品(SPU)不能同时参与多个ENABLE状态的积分商城活动。
- Source: `PointActivityServiceImpl.validatePointActivityProductConflicts()` lines 243-264

### R20 — 积分商城商品单次购买限制
积分商品的`count`字段作为单次购买限制，`validateJoinPointActivity`中校验购买数量不超过该限制。
- Source: `PointActivityServiceImpl.validateJoinPointActivity()` line 300

## 5. 依赖与协作

### 5.1 模块内依赖
- `SeckillActivity` → `SeckillConfig`: 校验秒杀时段配置的存在性和时间有效性
- `RewardActivity` → `ProductScope`: 处理商品范围冲突检测逻辑
- `Coupon` → `CouponTemplate`: 获取模板配置（有效期/商品范围/领取方式）

### 5.2 模块间依赖
- 所有活动类型 → `product`: 通过`ProductSpuApi`/`ProductSkuApi`校验商品存在性
- `RewardActivity`/`CouponTemplate` → `product`: 校验分类存在性(`ProductCategoryApi`)
- `Coupon`/`RewardActivity` → `trade`: 订单创建时匹配适用的优惠和活动
- `BargainRecord`/`CombinationRecord` → `trade`: 订单创建时关联活动记录
- `SeckillActivity`/`PointActivity` → `trade`: 订单创建时校验活动库存扣减

## 6. 不变式与约束

### I01 — 秒杀活动数据一致性
- 活动总库存(stock) <= 总库存(totalStock)（创建时相等，更新时可能更新totalStock）
- 活动库存的变化必须同时更新`seckill_activity`和`seckill_product`表
- 扣减使用乐观锁，保障并发安全

### I02 — 优惠券状态机
```
UNUSED → USED (用户使用)
UNUSED → EXPIRE (定时器过期)
USED → UNUSED/EXPIRE (售后退还)
```
- 删除优惠券仅允许UNUSED或EXPIRE状态

### I03 — 优惠券模板有效性不变式
- `takeCount`(已领取) <= `totalCount`(总发放数) 当totalCount非不限时
- `takeLimitCount`(每人限领) >= 1
- 固定日期类型模板：`validStartTime < validEndTime`
- 领取后类型模板：`validDayCount`(领取后几天有效) > 0

### I04 — 活动时间范围不变式
- 所有活动类型：`startTime < endTime`
- 活动启用期间才能参与
- 满减送活动不同活动间在同一时间段不能有商品范围重叠

### I05 — 商品参与活动限制
同一商品(SPU)在同一时间：
- 只能参与一个秒杀活动
- 只能参与一个限时折扣活动
- 只能参与一个拼团活动
- 只能参与一个砍价活动
- 只能参与一个积分商城活动
- 可同时参与秒杀+满减送(不同营销类型可叠加)

### I06 — Banner位置不变式
同一位置(position)可展示多个Banner，按sort排序。

## 7. 验收标准

### AC01 — 聚合根纯净性
聚合根类无MyBatis/Spring注解，不注入Mapper。
- 验证方法: grep聚合根文件确认无上述注解

### AC02 — 值对象不可变性
所有值对象为final class或record，字段均为final，无setter。
- 验证方法: 检查每个值对象文件

### AC03 — 仓储接口在领域层
Repository定义在`domain/{aggregate}/repository/`，无infrastructure imports。
- 验证方法: 检查import语句

### AC04 — 仓储实现在基础设施层
Repository实现在`infrastructure/{aggregate}/`，使用MyBatis Mapper。
- 验证方法: 确认实现类位置

### AC05 — 库存扣减并发安全
所有活动类型库存扣减使用乐观锁(updateStockDecr返回影响行数)，非先查后更模式。
- 验证方法: 代码审查确认SQL层原子扣减

### AC06 — 商品冲突校验完备
所有营销活动创建/更新时都进行SPU级冲突校验（秒杀/折扣/拼团/砍价/积分），满减送额外校验时间+范围。全局禁止同类型活动商品重叠。
- 验证方法: 冲突校验逻辑的单元测试覆盖

### AC07 — 优惠券领用闭环
领用→使用→退还(或过期)全链路状态正确流转，CAS更新防止重复使用。
- 验证方法: 领用使用退还链路的集成测试

### AC08 — 编译通过
- 验证方法: `mvn compile -pl develop-module-mall/develop-module-promotion-server`

## 8. 目录结构规划

```
develop-module-promotion-server/src/main/java/com/develop/mvp/pk/module/promotion/
├── domain/
│   ├── banner/
│   │   ├── Banner.java                      (聚合根)
│   │   ├── BannerFactory.java
│   │   ├── valueobject/
│   │   │   └── BannerId.java
│   │   ├── repository/
│   │   │   └── BannerRepository.java
│   ├── seckill/
│   │   ├── SeckillActivity.java              (聚合根)
│   │   ├── SeckillActivityFactory.java
│   │   ├── valueobject/
│   │   │   ├── SeckillActivityId.java
│   │   │   └── SeckillProduct.java            (record)
│   │   ├── repository/
│   │   │   └── SeckillActivityRepository.java
│   │   └── event/
│   │       └── SeckillActivityStatusChangedEvent.java
│   ├── coupon/
│   │   ├── CouponTemplate.java               (聚合根)
│   │   ├── CouponTemplateFactory.java
│   │   ├── Coupon.java                       (聚合根)
│   │   ├── valueobject/
│   │   │   ├── CouponTemplateId.java
│   │   │   └── CouponId.java
│   │   └── repository/
│   │       ├── CouponTemplateRepository.java
│   │       └── CouponRepository.java
│   ├── discount/
│   │   ├── DiscountActivity.java              (聚合根)
│   │   ├── DiscountActivityFactory.java
│   │   ├── valueobject/
│   │   │   └── DiscountProduct.java
│   │   ├── repository/
│   │   │   └── DiscountActivityRepository.java
│   │   └── event/
│   │       └── DiscountActivityStatusChangedEvent.java
│   ├── reward/
│   │   ├── RewardActivity.java                (聚合根)
│   │   ├── RewardActivityFactory.java
│   │   ├── valueobject/
│   │   │   └── RewardRule.java
│   │   ├── repository/
│   │   │   └── RewardActivityRepository.java
│   │   └── event/
│   │       └── RewardActivityStatusChangedEvent.java
│   ├── combination/
│   │   ├── CombinationActivity.java            (聚合根)
│   │   ├── CombinationActivityFactory.java
│   │   ├── valueobject/
│   │   │   └── CombinationProduct.java
│   │   ├── repository/
│   │   │   └── CombinationActivityRepository.java
│   │   └── event/
│   │       └── CombinationActivityStatusChangedEvent.java
│   ├── bargain/
│   │   ├── BargainActivity.java                (聚合根)
│   │   ├── BargainActivityFactory.java
│   │   ├── repository/
│   │   │   └── BargainActivityRepository.java
│   │   └── event/
│   │       └── BargainActivityStatusChangedEvent.java
│   ├── point/
│   │   ├── PointActivity.java                  (聚合根)
│   │   ├── PointActivityFactory.java
│   │   ├── valueobject/
│   │   │   └── PointProduct.java
│   │   ├── repository/
│   │   │   └── PointActivityRepository.java
│   │   └── event/
│   │       └── PointActivityStatusChangedEvent.java
│   ├── service/
│   │   ├── SeckillConflictDomainService.java    (领域服务)
│   │   ├── PromotionProductScopeDomainService.java
│   │   └── RewardConflictDomainService.java
│   └── event/
│       ├── DomainEvent.java                    (抽象接口)
│       └── DomainEventPublisher.java
├── application/{aggregate}/
│   └── PromotionApplicationService.java
├── infrastructure/{aggregate}/
│   └── *RepositoryImpl.java
└── convert/
```

## 9. 回滚条件

以下任一情况应回滚当前步骤：
1. 编译失败
2. 并发场景下库存扣减出现超卖（单元测试覆盖并发场景）
3. 优惠券领用或使用出现重复/错误
4. 营销活动商品冲突检测逻辑遗漏导致同一SPU同时参与多个同类型活动
5. 原有管理端/用户端接口行为变化

## 10. 分步执行计划

### Step 1: 完善已有聚合根(SeckillActivity/CouponTemplate/Banner)
- 1.1 `SeckillActivity` 补充：updateProfile()、close()、delete()、validateJoin()方法，完整库存管理
- 1.2 `CouponTemplate` 补充：updateProfile()、close()、validateTakeable()方法，商品范围校验
- 1.3 `Banner` 补充：updateProfile()、recordBrowse()方法

### Step 2: 新建聚合根
- 2.1 创建 `DiscountActivity` 聚合根（含商品冲突校验、状态管理）
- 2.2 创建 `RewardActivity` 聚合根（含时间+商品范围冲突检测、规则管理）
- 2.3 创建 `CombinationActivity` 聚合根
- 2.4 创建 `BargainActivity` 聚合根
- 2.5 创建 `PointActivity` 聚合根
- 2.6 创建 `Coupon` 聚合根（优惠券实例，状态机和领用逻辑）

### Step 3: 创建领域服务
- 3.1 `SeckillConflictDomainService` — 秒杀时段冲突校验
- 3.2 `RewardConflictDomainService` — 满减送时间+范围冲突校验
- 3.3 `PromotionProductScopeDomainService` — 商品范围校验（SPU/CATEGORY/ALL）

### Step 4: 完善仓储接口与实现
- 4.1 为所有新建聚合根创建Repository接口和实现
- 4.2 确保所有库存扣减使用乐观锁

### Step 5: 创建应用服务
- 5.1 抽取业务逻辑到ApplicationService
- 5.2 ApplicationService只做编排

### Step 6: 验证
- 6.1 运行全部单元测试
- 6.2 编译通过
- 6.3 商品冲突校验全覆盖
