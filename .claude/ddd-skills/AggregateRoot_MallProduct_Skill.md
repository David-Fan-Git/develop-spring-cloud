# DDD Skill: AggregateRoot_MallProduct_Skill

## 1. 技能名称
`AggregateRoot_MallProduct_Skill` — 商城商品域(Product)聚合根的领域建模与重构技能

## 2. 适用场景
商城商品子域：商品分类(Category)、商品品牌(Brand)、商品SPU(Spu)、商品SKU(Sku)、商品属性(Property)、商品评论(Comment)、商品收藏(Favorite)、商品浏览记录(BrowseHistory)。

## 3. DDD 构造块

### 3.1 聚合根
- **ProductCategory** — 商品分类聚合根，支持两级层级结构，含启用/禁用，parentId=0L为根节点
- **ProductBrand** — 商品品牌聚合根，含启用/禁用，name唯一约束
- **ProductSpu** — 商品SPU聚合根（旗舰聚合根），含上下架、回收站、SKU管理、库存/销量变更，状态枚举: ENABLE/DISABLE/RECYCLE
- **ProductComment** — 商品评论聚合根，含可见性控制、商家回复、评分(描述/服务/物流)
- **ProductFavorite** — 商品收藏聚合根，per user+spu唯一

### 3.2 值对象
- `ProductCategoryId`, `ProductCategoryName` — 分类标识与名称值对象（final class，构造自校验）
- `ProductBrandId`, `ProductBrandName` — 品牌标识与名称值对象，name不可为空
- `ProductSpuId`, `ProductSpuStatus` — SPU标识与状态值对象（状态枚举支持 enabled()/disabled()/recycle()/isEnabled()/isDisabled()/isRecycle()）
- `ProductSku`, `SkuProperty` — SKU与属性值对象（record类型，包含price/marketPrice/costPrice/stock/properties）
- `ProductPropertyId`, `ProductPropertyName` — 属性项标识
- `ProductPropertyValueId`, `ProductPropertyValueName` — 属性值标识
- 不可变类，构造方法自校验，使用record或final class

### 3.3 仓储接口
- `ProductCategoryRepository` — findById/save/delete/findByParentId/findByStatus
- `ProductBrandRepository` — findById/save/delete/findByName/findByStatus
- `ProductSpuRepository` — findById/save/delete/page/findByCategoryId/findByStatus/findByStock/findByIdIncludeDeleted
- `ProductSkuRepository` — findBySpuId/saveBatch/deleteBySpuId/updateStock(乐观锁)
- `ProductPropertyRepository` — findById/save/delete/findByName
- `ProductPropertyValueRepository` — findByPropertyId/save/deleteByPropertyId
- `ProductCommentRepository` — findById/save/page/findByUserIdAndOrderItemId
- `ProductFavoriteRepository` — findByUserIdAndSpuId/save/delete/findByUserId/page
- `ProductBrowseHistoryRepository` — findByUserIdAndSpuId/save/delete/findByUserId/page
- 所有Repository定义在domain层，无infrastructure imports

### 3.4 领域事件
- `ProductCategoryStatusChangedEvent` — 分类状态变更(启用/禁用)
- `ProductBrandStatusChangedEvent` — 品牌状态变更
- `ProductSpuCreatedEvent` — SPU创建
- `ProductSpuStatusChangedEvent` — SPU状态变更(上架/下架/回收站)
- `ProductSpuDeletedEvent` — SPU删除
- `ProductCommentCreatedEvent` — 评论创建
- `ProductFavoriteToggledEvent` — 收藏/取消收藏

### 3.5 工厂
- `ProductCategoryFactory` — 创建分类聚合根，校验层级约束
- `ProductBrandFactory` — 创建品牌聚合根，校验name唯一性
- `ProductSpuFactory` — 创建SPU聚合根，包含SKU初始化、默认状态设置
- `ProductCommentFactory` — 创建评论聚合根

### 3.6 领域服务
- `ProductCategoryDomainService` — 分类层级校验服务、分类与SPU绑定校验
- `ProductSpuDomainService` — SPU唯一性校验、SPU与活动冲突校验

## 4. 职责边界

### R01 — 商品分类必须二级或以上才能绑定SPU
分类层级必须 >= 2（即最末级分类），一级分类不能直接关联商品。
- Source: `ProductCategoryServiceImpl.validateCategoryList()` line 126-128, `ProductSpuServiceImpl.validateCategory()` line 129-132

### R02 — 商品分类父节点不能是二级分类（最多两级深度）
父分类必须是一级分类(parentId=0L)或根节点。父分类如果是二级分类则抛出`CATEGORY_PARENT_NOT_FIRST_LEVEL`。
- Source: `ProductCategoryServiceImpl.validateParentProductCategory()` line 93-96

### R03 — 删除分类时需确保无子分类和绑定的SPU
必须先查询是否有子分类(`selectCountByParentId`)和是否有绑定的SPU(`getSpuCountByCategoryId`)，任一存在则禁止删除。
- Source: `ProductCategoryServiceImpl.deleteCategory()` line 67-81

### R04 — 品牌名称必须唯一
创建和更新时都需要校验`selectByName`是否已被占用，排除自身id。
- Source: `ProductBrandServiceImpl.validateBrandNameUnique()` line 71-84

### R05 — SPU分类和品牌必须为启用状态
创建/更新SPU时校验分类`validateCategory()`和品牌`validateProductBrand()`必须为ENABLE状态。
- Source: `ProductSpuServiceImpl.createSpu()` line 60-61, `ProductCategoryServiceImpl.validateCategory()` line 138-146

### R06 — SPU必须至少包含一个SKU
创建SPU时校验SKU列表非空`skus.isEmpty()`，空则抛出`SKU_NOT_EXISTS`。
- Source: `ProductSkuServiceImpl.validateSkuList()` line 89-92, `ProductSpu.publish()` line 98-100

### R07 — SPU状态流转约束
- 上架：不能在回收站状态上架(prerequisite: status != RECYCLE, 必须有SKU)
- 下架：任意状态可下架
- 回收站：仅ENABLE/DISABLE状态可进回收站
- 删除：仅RECYCLE状态可彻底删除(`SPU_NOT_RECYCLE`)
- Source: `ProductSpuServiceImpl.deleteSpu()` line 167-169, `ProductSpu` aggregate root lines 95-127

### R08 — SKU属性严格校验
- 单规格：自动赋予默认属性
- 多规格：所有SKU必须有相同数量的属性条目
- 同一SKU内不能有重复属性(propertyId重复)
- 不同SKU之间属性组合不能重复(属性值集合去重校验)
- Source: `ProductSkuServiceImpl.validateSkuList()` lines 88-143

### R09 — SKU库存扣减必须防止超卖
扣减使用`updateStockDecr`返回影响行数判断，为0表示库存不足(`SKU_STOCK_NOT_ENOUGH`)。
- Source: `ProductSkuServiceImpl.updateSkuStock()` lines 256-268

### R10 — SPU级价格从SKU派生
SPU的price/marketPrice/costPrice取各SKU的最小值，stock取各SKU之和。
- Source: `ProductSpuServiceImpl.initSpuFromSkus()` lines 104-119

### R11 — 商品评论确保每个订单项只能评论一次
通过`selectByUserIdAndOrderItemId`校验，重复评论抛出`COMMENT_ORDER_EXISTS`。
- Source: `ProductCommentServiceImpl.validateCommentExists()` lines 86-91

### R12 — 商品收藏确保用户+SPU唯一
收藏前检查`selectByUserIdAndSpuId`，已存在则抛出`FAVORITE_EXISTS`。取消收藏需记录存在。
- Source: `ProductFavoriteServiceImpl.createFavorite()` lines 31-35, `deleteFavorite()` lines 43-49

### R13 — 浏览历史最多保存100条/用户
新浏览记录插入前检查总数，超过100条则删除最早一条。
- Source: `ProductBrowseHistoryServiceImpl.createBrowseHistory()` lines 30-53, 常量`USER_STORE_MAXIMUM=100`

## 5. 依赖与协作

### 5.1 模块内依赖
- `ProductSpu` → `ProductCategory`: 校验分类合法性和层级
- `ProductSpu` → `ProductBrand`: 校验品牌合法性
- `ProductSpu` ↔ `ProductSku`: 级联创建/更新/删除，库存同步
- `ProductSku` → `ProductProperty`/`ProductPropertyValue`: 校验属性存在
- `ProductComment` → `ProductSpu`/`ProductSku`: 校验商品存在(含已删除)
- `ProductCategory` ← `ProductSpu`: 删除分类时检查SPU绑定

### 5.2 模块间依赖
- `ProductSpu`/`ProductSku` → `trade`: 通过API提供商品信息(价格/库存/状态)给交易和营销模块
- `ProductComment` → `member`: 通过`MemberUserApi`获取用户信息
- `ProductCategory` → `promotion`: 通过API提供分类校验给优惠券和满减送活动
- `ProductFavorite` → 无外部依赖
- `ProductBrowseHistory` → 无外部依赖

## 6. 不变式与约束

### I01 — 分类层级不变式
分类最多两层：根节点(level=0) → 一级分类(level=1) → 二级分类(level=2)。SPU只能挂在二级分类下。
- 约束方式：validateParentProductCategory拒绝二级分类作为父节点

### I02 — SPU状态机[重要]
```
→ ENABLE ←→ DISABLE ←→ RECYCLE → (deleted)
   ↑___________________________|
```
- ENABLE可下架到DISABLE，可进回收站到RECYCLE
- DISABLE可上架到ENABLE，可进回收站到RECYCLE
- RECYCLE只能恢复上架到ENABLE或彻底删除
- 上架必须有至少一个SKU

### I03 — 价格不变式
- SPU.price ≤ SPU.marketPrice（如果两者都非null）
- 所有价格 >= 0
- SPU.price = min(skus.price)
- SPU.stock = sum(skus.stock)
- SKU的库存扣减(spu.stock += incrCount)在SPU级别保障同步

### I04 — SKU属性完整约束
- 多规格SKU必须都有相同数量的属性
- 单一SKU内属性propertyId不能重复
- 全部SKU中属性组合(propertyValueId集合)不能重复

## 7. 验收标准

### AC01 — 聚合根纯净性
聚合根类无MyBatis/Spring注解(@Service/@Repository/@Table/@TableName等)，不注入Mapper。
- 验证方法: grep聚合根文件确认无上述注解

### AC02 — 值对象不可变性
所有值对象为final class或record，字段均为final，无setter方法，构造时自校验合法性。
- 验证方法: 检查每个值对象文件

### AC03 — 仓储接口位置
所有Repository接口定义在`domain/{aggregate}/repository/`包下，无infrastructure层import。
- 验证方法: 检查Repository接口的import语句

### AC04 — 仓储实现位置
Repository实现在`infrastructure/{aggregate}/`包下，使用MyBatis Mapper。
- 验证方法: 确认实现类在infrastructure层

### AC05 — 应用服务不包含领域逻辑
ApplicationService只负责事务编排、调用Repository和发布事件，不含if-else领域校验。
- 验证方法: 审查ApplicationService代码

### AC06 — 规格校验完整性
单规格SKU自动补充默认属性，多规格进行完整的属性维度校验(存在性/重复/数量一致/组合去重)。
- 验证方法: 单元测试覆盖单规格和多规格场景

### AC07 — 库存扣减防超卖
SKU库存扣减使用乐观锁(updateStockDecr返回int)，为0时抛出异常。
- 验证方法: 并发测试/代码审查确认乐观锁机制

### AC08 — 状态流转合法性
SPU状态流转按I02状态机严格执行，不合法的流转抛出IllegalStateException。
- 验证方法: 状态流转单元测试覆盖所有合法/非法路径

### AC09 — 编译通过
- 验证方法: `mvn compile -pl develop-module-mall/develop-module-product-server`

## 8. 目录结构规划

```
develop-module-product-server/src/main/java/com/develop/mvp/pk/module/product/
├── domain/
│   ├── productcategory/
│   │   ├── ProductCategory.java              (聚合根)
│   │   ├── ProductCategoryFactory.java
│   │   ├── valueobject/
│   │   │   ├── ProductCategoryId.java
│   │   │   └── ProductCategoryName.java
│   │   ├── repository/
│   │   │   └── ProductCategoryRepository.java
│   │   └── event/
│   │       └── ProductCategoryStatusChangedEvent.java
│   ├── productbrand/
│   │   ├── ProductBrand.java                 (聚合根)
│   │   ├── ProductBrandFactory.java
│   │   ├── valueobject/
│   │   │   ├── ProductBrandId.java
│   │   │   └── ProductBrandName.java
│   │   ├── repository/
│   │   │   └── ProductBrandRepository.java
│   │   └── event/
│   │       └── ProductBrandStatusChangedEvent.java
│   ├── productspu/
│   │   ├── ProductSpu.java                   (旗舰聚合根)
│   │   ├── ProductSpuFactory.java
│   │   ├── valueobject/
│   │   │   ├── ProductSpuId.java
│   │   │   ├── ProductSpuStatus.java
│   │   │   ├── ProductSku.java               (record)
│   │   │   └── SkuProperty.java              (record)
│   │   ├── repository/
│   │   │   ├── ProductSpuRepository.java
│   │   │   └── ProductSpuPageQuery.java
│   │   └── event/
│   │       ├── ProductSpuCreatedEvent.java
│   │       ├── ProductSpuStatusChangedEvent.java
│   │       └── ProductSpuDeletedEvent.java
│   ├── productcomment/
│   │   ├── ProductComment.java               (聚合根)
│   │   ├── ProductCommentFactory.java
│   │   ├── valueobject/
│   │   │   ├── ProductCommentId.java
│   │   │   └── CommentScore.java
│   │   ├── repository/
│   │   │   └── ProductCommentRepository.java
│   │   └── event/
│   │       └── ProductCommentCreatedEvent.java
│   ├── productfavorite/
│   │   ├── ProductFavorite.java              (聚合根)
│   │   ├── valueobject/
│   │   │   └── ProductFavoriteId.java
│   │   ├── repository/
│   │   │   └── ProductFavoriteRepository.java
│   │   └── event/
│   │       └── ProductFavoriteToggledEvent.java
│   └── event/
│       ├── DomainEvent.java                  (抽象接口)
│       └── DomainEventPublisher.java
├── application/{aggregate}/
│   └── ProductSpuApplicationService.java     (应用服务)
├── infrastructure/{aggregate}/
│   ├── ProductCategoryRepositoryImpl.java
│   ├── ProductBrandRepositoryImpl.java
│   ├── ProductSpuRepositoryImpl.java
│   ├── ProductSkuRepositoryImpl.java
│   └── ... (其他仓储实现)
└── convert/                                  (MapStruct转换器)
```

## 9. 回滚条件

以下任一情况应回滚当前步骤：
1. 编译失败（`mvn compile` 不通过）
2. 领域事件发布后未正确消费导致数据不一致
3. 聚合根的接口变更导致应用服务层大面积编译错误
4. 原有业务接口（Controller/API）行为发生变化
5. 单元测试覆盖率低于原有水平

## 10. 分步执行计划

### Step 1: 完善已有聚合根
- 1.1 审查 `ProductCategory` 聚合根，补充 `delete()` 方法（校验无子分类和SPU绑定）
- 1.2 审查 `ProductBrand` 聚合根，补充name唯一性校验
- 1.3 审查 `ProductSpu` 聚合根，补充 `updateSkus()` 的价格重算逻辑和状态校验完整性

### Step 2: 新建领域对象
- 2.1 创建 `ProductComment` 聚合根（包含visible控制、reply方法、评分值对象）
- 2.2 创建 `ProductFavorite` 聚合根（唯一性约束保障在Factory层）
- 2.3 创建对应值对象和事件

### Step 3: 完善仓储接口与实现
- 3.1 为 `ProductComment`/`ProductFavorite` 创建Repository接口和实现
- 3.2 为已有聚合根补齐缺失的查询方法（如批量查询、分页查询、按状态查询）

### Step 4: 创建应用服务
- 4.1 抽取原有Service中的业务逻辑到ApplicationService
- 4.2 ApplicationService只负责：①调用Repository ②调用聚合根业务方法 ③发布事件
- 4.3 Controller改为调用ApplicationService

### Step 5: 验证
- 5.1 运行全部单元测试
- 5.2 编译通过
- 5.3 确认Controller使用ApplicationService
