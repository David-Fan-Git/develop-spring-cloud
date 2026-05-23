# DDD Skill: AggregateRoot_MallTrade_Skill

## 1. 技能名称
`AggregateRoot_MallTrade_Skill` — 商城交易域(Trade)聚合根的领域建模与重构技能

## 2. 适用场景
商城交易子域：购物车(Cart)、交易订单(TradeOrder)、售后(AfterSale)、配送(DeliveryExpress/DeliveryTemplate)、分销(BrokerageRecord/BrokerageWithdraw)、交易配置(TradeConfig)。

## 3. DDD 构造块

### 3.1 聚合根
- **Cart** — 购物车聚合根，含选中/取消选中、数量变更、SPU软删除自动清理
- **TradeOrder** — 交易订单聚合根（旗舰聚合根），含完整状态机(UNPAID→UNDELIVERED→DELIVERED→COMPLETED)、营销信息、售后状态链路、调价、评价
- **AfterSale** — 售后单聚合根，含完整状态机(APPLY→SELLER_AGREE/SELLER_DISAGREE→BUYER_DELIVERY→WAIT_REFUND→COMPLETE)
- **BrokerageRecord** — 分销佣金记录聚合根，含两级分佣、冻结/结算
- **BrokerageWithdraw** — 佣金提现聚合根，含审核流转、转账结果同步

### 3.2 值对象
- `CartId` — 购物车标识值对象
- `TradeOrderId`, `OrderItem` — 订单标识、行项目值对象（orderItem含商品信息/价格/售后状态/评价状态）
- `AfterSaleId`, `AfterSaleStatus` — 售后标识、状态值对象
- `DeliveryExpressId`, `DeliveryTemplateId` — 快递与模板标识
- `BrokerageRecordId`, `BrokerageWithdrawId` — 佣金记录与提现标识
- `ReceiverInfo` — 收货信息值对象（name/mobile/areaId/detailAddress）
- `Money` — 金额值对象（分单位，不可变）
- `OrderMarketInfo` — 订单营销信息值对象（couponId/couponPrice/pointPrice/vipPrice等）
- 不可变类，构造方法自校验

### 3.3 仓储接口
- `CartRepository` — findByUserIdAndSkuId/save/delete/deleteBySpuId/findByUserId/batchUpdateSelected
- `TradeOrderRepository` — findById/save/delete/page/findByUserId/findByStatusAndCreateTime/findByStatusAndDeliveryTime/updateByIdAndStatus(CAS)
- `TradeOrderItemRepository` — findByOrderId/saveBatch/updateAfterSaleStatus(CAS)/updateBatch
- `AfterSaleRepository` — findById/save/delete/page/findByUserId/findByOrderItemId/updateByIdAndStatus(CAS)
- `DeliveryExpressRepository` — findById/save/findAll
- `BrokerageRecordRepository` — save/updateByIdAndStatus(CAS)/findByBizTypeAndBizId
- `BrokerageWithdrawRepository` — findById/save/page/updateByIdAndStatus(CAS)
- 所有Repository定义在domain层，无infrastructure imports

### 3.4 领域事件
- `TradeOrderCreatedEvent` — 订单创建事件（含订单号/金额/营销信息）
- `TradeOrderStatusChangedEvent` — 订单状态变更事件（含旧状态/新状态/时间）
- `TradeOrderPaidEvent` — 订单支付成功事件
- `TradeOrderDeliveredEvent` — 订单发货事件
- `TradeOrderCompletedEvent` — 订单完成事件（收货）
- `TradeOrderCanceledEvent` — 订单取消事件
- `AfterSaleCreatedEvent` — 售后单创建事件
- `AfterSaleStatusChangedEvent` — 售后单状态变更事件
- `AfterSaleRefundedEvent` — 售后退款完成事件
- `BrokerageRecordCreatedEvent` — 佣金记录创建事件
- `BrokerageSettledEvent` — 佣金结算事件

### 3.5 工厂
- `CartFactory` — 创建购物车项，含SKU校验
- `TradeOrderFactory` — 创建订单聚合根（核心工厂），含价格计算的接收、收货地址填充、营销信息注入、订单号生成
- `AfterSaleFactory` — 创建售后单，含订单项校验、退款金额校验
- `BrokerageRecordFactory` — 创建佣金记录，含分佣计算

### 3.6 领域服务
- `TradeOrderStatusDomainService` — 订单状态流转校验服务（自动收货/自动取消/售后取消）
- `TradePriceCalculationService` — 价格计算领域服务（协调多个PriceCalculator）
- `AfterSaleValidateDomainService` — 售后申请前置校验服务（订单状态/拼团状态/退款金额）
- `BrokerageCalculationService` — 佣金计算领域服务（两级分佣/固定比例/冻结天数）
- `BrokerageWithdrawDomainService` — 提现审批转账领域服务

## 4. 职责边界

### R01 — 购物车同SKU合并数量，不同SKU新增条目
同一用户+同一SKU已存在时更新数量(count叠加)；不存在时插入新记录。
- Source: `CartServiceImpl.addCart()` lines 46-63

### R02 — 购物车添加/更新时校验SKU库存
`checkProductSku()`校验SKU存在性及数量是否超过库存。
- Source: `CartServiceImpl.checkProductSku()` lines 185-194

### R03 — 购物车自动清理已删除SPU的商品
查询购物车列表时，如果SPU已被删除则同步删除购物车记录（延迟删除策略）。
- Source: `CartServiceImpl.deleteCartIfSpuDeleted()` lines 164-173

### R04 — 订单状态严格状态机流转[重要]
```
UNPAID(待支付) → UNDELIVERED(待发货) [支付成功]
UNDELIVERED → DELIVERED(已发货) [管理员发货]
DELIVERED → COMPLETED(已完成) [确认收货/自动收货]
UNPAID → CANCELED(已取消) [用户取消/超时取消/拼团关闭]
COMPLETED → CANCELED [全部售后成功退款]
```
- Source: `TradeOrderUpdateServiceImpl`:
  - `updateOrderPaid()` line 285-318 (UNPAID→UNDELIVERED)
  - `deliveryOrder()` line 372-416 (UNDELIVERED→DELIVERED)
  - `receiveOrder0()` line 513-528 (DELIVERED→COMPLETED)
  - `cancelOrder0()` line 627-642 (UNPAID→CANCELED)
  - `cancelOrderByAfterSale()` line 652-664 (COMPLETED→CANCELED)

### R05 — 订单支付回调多重校验
支付回调时校验：①支付单存在 ②支付单已成功 ③支付金额与订单金额一致 ④支付单绑定的商户订单号与订单ID一致。任一不匹配拒绝更新。
- Source: `TradeOrderUpdateServiceImpl.validatePayOrderPaid()` lines 343-370

### R06 — 订单发货前校验售后状态
发货前校验`refundStatus`必须为NONE，否则禁止发货。
- Source: `TradeOrderUpdateServiceImpl.validateOrderDeliverable()` lines 441-451

### R07 — 订单调价约束
- 只能调价一次（`adjustPrice > 0`的订单不可再调）
- 已支付订单不可调价
- 调价后支付金额不能<=0
- 调价需同步分摊到各OrderItem并更新支付单金额
- Source: `TradeOrderUpdateServiceImpl.updateOrderPrice()` lines 698-739

### R08 — 订单收货地址仅待发货状态可修改
`UNDELIVERED`状态之前(即UNPAID)可修改收货地址。更准确说，只有待发货状态的订单才可修改。
- Source: `TradeOrderUpdateServiceImpl.updateOrderAddress()` lines 742-756

### R09 — 订单超时自动取消/自动收货
- 自动取消：查询超过`payExpireTime`未支付的UNPAID订单，逐个取消
- 自动收货：查询超过`receiveExpireTime`已发货未收货的DELIVERED订单，逐个完成
- 自动评价：查询超过`commentExpireTime`未评价的COMPLETED订单，系统自动好评
- Source: `TradeOrderUpdateServiceImpl.cancelOrderBySystem()` lines 579-599, `receiveOrderBySystem()` lines 474-495, `createOrderItemCommentBySystem()` lines 901-921

### R10 — 订单取消的支付延迟保护
取消前校验支付单状态，如果支付单已成功（延迟回调），则不允许取消。
- Source: `TradeOrderUpdateServiceImpl.cancelOrderByMember()` lines 566-572

### R11 — 售后申请前置校验[重要]
创建售后前校验：①订单项存在 ②订单项未申请过售后 ③退款金额不超过商品实付 ④订单未取消 ⑤订单已支付 ⑥退货退款需已发货 ⑦拼团进行中不允许售后
- Source: `AfterSaleServiceImpl.validateOrderItemApplicable()` lines 126-169

### R12 — 售后审批双向流转
- 同意：REFUND类型直接进入WAIT_REFUND；RETURN_AND_REFUND类型进入SELLER_AGREE
- 拒绝：进入SELLER_DISAGREE，需回滚订单项售后状态
- 审批前置条件：售后单状态必须为APPLY
- Source: `AfterSaleServiceImpl.agreeAfterSale()` lines 194-210, `disagreeAfterSale()` lines 213-230

### R13 — 售后用户退货后管理员收货/拒收
- 收货：BUYER_DELIVERY→WAIT_REFUND，等待退款
- 拒收：BUYER_DELIVERY→SELLER_REFUSE，回滚订单项售后状态
- Source: `AfterSaleServiceImpl.receiveAfterSale()` lines 284-297, `refuseAfterSale()` lines 300-324

### R14 — 售后退款零金额快速完成
特殊场景（积分兑换）：退款金额为0时直接标记COMPLETE，不发起支付退款单。
- Source: `AfterSaleServiceImpl.refundAfterSale()` lines 346-370

### R15 — 退款完成联动订单状态
退款成功后：①更新订单项售后状态为SUCCESS ②累加订单`refundPrice`和`refundPoint` ③如果全部订单项都售后成功(`isAllOrderItemAfterSaleSuccess`)，订单refundStatus=ALL并取消订单
- Source: `TradeOrderUpdateServiceImpl.updateOrderItemWhenAfterSaleSuccess()` lines 813-834

### R16 — 用户取消售后
下列状态的售后单用户可取消：APPLY / SELLER_AGREE / BUYER_DELIVERY。取消后回滚订单项售后状态。
- Source: `AfterSaleServiceImpl.cancelAfterSale()` lines 455-479

### R17 — 分销两级分佣[重要]
- 一级分佣：根据`brokerageFirstPercent`比例或SKU固定佣金`firstBrokeragePrice`计算
- 二级分佣：根据`brokerageSecondPercent`比例或SKU固定佣金`secondBrokeragePrice`计算
- 优先使用固定佣金，无固定值时按比例计算
- Source: `BrokerageRecordServiceImpl.addBrokerage()` lines 78-106, `calculatePrice()` lines 143-153

### R18 — 分销佣金冻结和解冻
- 创建佣金时如果配置了冻结天数(`brokerageFrozenDays > 0`)，佣金进入冻结状态
- 定时器`unfreezeRecord()`扫描到期冻结记录，解冻为可用佣金
- Source: `BrokerageRecordServiceImpl.addBrokerage()` lines 165-207, `unfreezeRecord()` lines 239-255

### R19 — 佣金提现审批流程
- 创建提现：校验最低提现金额，扣减可用佣金
- 审核通过：API转账(支付宝/微信/钱包)或手动打款
- 审核不通过：退还佣金至用户余额
- Source: `BrokerageWithdrawServiceImpl.auditBrokerageWithdraw()` lines 82-116

### R20 — 订单创建后置处理器链[重要]
订单创建采用Handler链模式，按顺序执行：
`TradeBargainOrderHandler` / `TradeBrokerageOrderHandler` / `TradeCombinationOrderHandler` / `TradeCouponOrderHandler` / `TradeMemberPointOrderHandler` / `TradePointOrderHandler` / `TradeProductSkuOrderHandler` / `TradeSeckillOrderHandler` / `TradeStatusSyncToWxaOrderHandler`
- beforeOrderCreate: 订单创建前校验
- afterOrderCreate: 订单创建后处理（库存扣减、优惠券使用、积分扣减）
- afterPayOrder: 支付后处理
- afterCancelOrder: 取消后处理
- afterDeliveryOrder: 发货后处理
- afterReceiveOrder: 收货后处理
- Source: `TradeOrderUpdateServiceImpl.createOrder()` lines 186-204, `afterCreateTradeOrder()` lines 248-269

## 5. 依赖与协作

### 5.1 模块内依赖
- `TradeOrder` → `Cart`: 下单后删除购物车商品
- `TradeOrder` → `TradePriceService`: 价格计算依赖PriceCalculator链
- `TradeOrder` ← `AfterSale`: 售后操作联动订单的售后状态和退款金额
- `AfterSale` → `DeliveryExpress`: 校验快递公司存在性
- `BrokerageRecord` → `BrokerageUser`: 更新用户佣金余额
- `BrokerageWithdraw` → `BrokerageRecord`: 提现扣减佣金/审核失败退还佣金

### 5.2 模块间依赖
- `TradeOrder` → `PayOrderApi`: 创建支付单和退款单
- `TradeOrder` → `ProductSkuApi`/`ProductSpuApi`: 获取商品信息、扣减库存
- `TradeOrder` → `CouponApi`: 使用/退还优惠券
- `TradeOrder` → `MemberAddressApi`: 获取用户默认地址
- `TradeOrder` → `CombinationRecordApi`: 拼团校验
- `TradeOrder` → `DiscountActivityApi`/`RewardActivityApi`: 价格计算
- `TradeOrder` → `ProductCommentApi`: 创建订单评价
- `TradeOrder` → `SocialClientApi`: 微信订阅消息通知
- `BrokerageWithdraw` → `PayTransferApi`/`PayWalletApi`: 转账/钱包提现
- `AfterSale` → `PayRefundApi`: 发起退款

## 6. 不变式与约束

### I01 — 订单状态机[重要]
```
UNPAID → [支付成功] → UNDELIVERED → [发货] → DELIVERED → [收货] → COMPLETED
  ↓                        ↓                                     ↓
  [取消]                  [取消]                                [全部退款]
  ↓                        ↓                                     ↓
CANCELED                CANCELED                              CANCELED
```
- UNDELIVERED状态可修改收货地址
- COMPLETED+CANCELED禁止任何操作除售后外
- 支付状态`payStatus`与订单状态保持同步

### I02 — 退款金额不变式
- `refundPrice <= payPrice`（累积退款金额不超过支付金额）
- 每个OrderItem的退款金额不超过其`payPrice`
- 全部退款(`refundStatus=ALL`)时订单自动取消

### I03 — 订单金额一致性
- `payPrice = totalPrice - discountPrice - couponPrice - pointPrice - vipPrice + deliveryPrice + adjustPrice`
- `adjustPrice`初始为0，调价后累加
- 所有金额单位为分

### I04 — 评价约束
- 只有在COMPLETED状态的订单才能评价
- 一个订单项只能评价一次(`commentStatus`)
- 全部订单项评价完成后订单`commentStatus=true`

### I05 — 售后单唯一性
- 一个订单项同时只能有一个进行中的售后单(`afterSaleStatus != NONE`禁止再次售后申请)
- 取消/拒绝的售后单可重新申请(`afterSaleStatus`回滚为NONE)

### I06 — 分销约束
- 分销功能启用(`brokerageEnabled=true`)才产生佣金
- 两级分佣：一级用户必须有`brokerageEnabled=true`，二级用户通过一级的`bindUserId`关联
- 佣金冻结期间不可用

## 7. 验收标准

### AC01 — 聚合根纯净性
聚合根无MyBatis/Spring注解，不注入Mapper。
- 验证方法: grep聚合根文件确认无注解

### AC02 — 值对象不可变性
值对象为final class或record，字段final，无setter，构造自校验。
- 验证方法: 检查值对象文件

### AC03 — 仓储接口在领域层
Repository定义在`domain/{aggregate}/repository/`。
- 验证方法: 检查import语句

### AC04 — 仓储实现在基础设施层
Repository实现在`infrastructure/{aggregate}/`。
- 验证方法: 确认文件位置

### AC05 — 订单状态流转完整覆盖
所有状态机路径(R04/I01)都有对应的聚合根业务方法，非法流转抛出异常。
- 验证方法: 状态流转单元测试全路径覆盖

### AC06 — 订单金额一致性
价格计算、调价分摊、退款金额累计后，金额一致性约束(I03)始终成立。
- 验证方法: 金额一致性集成测试

### AC07 — 售后生命周期全链路
APPLY→审批→(退货→)退款→完成/取消，每个环节状态校验正确，退款金额联动订单。
- 验证方法: 售后全链路集成测试

### AC08 — 购物车同步清理
SPU删除后购物车自动清理已删除商品。
- 验证方法: 购物车列表查询后验证清理

### AC09 — 编译通过
- 验证方法: `mvn compile -pl develop-module-mall/develop-module-trade-server`

## 8. 目录结构规划

```
develop-module-trade-server/src/main/java/com/develop/mvp/pk/module/trade/
├── domain/
│   ├── cart/
│   │   ├── Cart.java                        (聚合根)
│   │   ├── CartFactory.java
│   │   ├── valueobject/
│   │   │   └── CartId.java
│   │   └── repository/
│   │       └── CartRepository.java
│   ├── tradeorder/
│   │   ├── TradeOrder.java                  (旗舰聚合根)
│   │   ├── TradeOrderFactory.java
│   │   ├── valueobject/
│   │   │   ├── TradeOrderId.java
│   │   │   ├── OrderItem.java               (record)
│   │   │   ├── ReceiverInfo.java            (record)
│   │   │   └── OrderMarketInfo.java         (record)
│   │   ├── repository/
│   │   │   └── TradeOrderRepository.java
│   │   └── event/
│   │       ├── TradeOrderCreatedEvent.java
│   │       ├── TradeOrderStatusChangedEvent.java
│   │       ├── TradeOrderPaidEvent.java
│   │       ├── TradeOrderDeliveredEvent.java
│   │       ├── TradeOrderCompletedEvent.java
│   │       └── TradeOrderCanceledEvent.java
│   ├── aftersale/
│   │   ├── AfterSale.java                   (聚合根)
│   │   ├── AfterSaleFactory.java
│   │   ├── valueobject/
│   │   │   └── AfterSaleId.java
│   │   ├── repository/
│   │   │   └── AfterSaleRepository.java
│   │   └── event/
│   │       ├── AfterSaleCreatedEvent.java
│   │       ├── AfterSaleStatusChangedEvent.java
│   │       └── AfterSaleRefundedEvent.java
│   ├── brokerage/
│   │   ├── BrokerageRecord.java              (聚合根)
│   │   ├── BrokerageRecordFactory.java
│   │   ├── BrokerageWithdraw.java            (聚合根)
│   │   ├── valueobject/
│   │   │   ├── BrokerageRecordId.java
│   │   │   └── BrokerageWithdrawId.java
│   │   └── repository/
│   │       ├── BrokerageRecordRepository.java
│   │       └── BrokerageWithdrawRepository.java
│   ├── service/
│   │   ├── TradeOrderStatusDomainService.java
│   │   ├── TradePriceCalculationService.java
│   │   ├── AfterSaleValidateDomainService.java
│   │   ├── BrokerageCalculationService.java
│   │   └── BrokerageWithdrawDomainService.java
│   └── event/
│       ├── DomainEvent.java
│       └── DomainEventPublisher.java
├── application/{aggregate}/
│   └── TradeOrderApplicationService.java
├── infrastructure/{aggregate}/
│   ├── CartRepositoryImpl.java
│   ├── TradeOrderRepositoryImpl.java
│   ├── AfterSaleRepositoryImpl.java
│   ├── BrokerageRecordRepositoryImpl.java
│   └── BrokerageWithdrawRepositoryImpl.java
├── service/price/
│   └── calculator/                          (PriceCalculator链)
└── convert/
```

## 9. 回滚条件

以下任一情况应回滚当前步骤：
1. 编译失败
2. 订单状态机缺少合法流转路径或存在非法流转路径
3. 金额计算不一致（价格/调价/退款）
4. 并发场景下库存/优惠券超卖
5. 售后流程中断导致订单和订单项状态不一致
6. 原有Controller/API行为发生变化

## 10. 分步执行计划

### Step 1: 完善已有聚合根
- 1.1 完善`TradeOrder`：补充updateRemark/updateReceiver/updatePrice/updateCommentStatus/updateRefund等方法
- 1.2 完善`Cart`：补充updateCount/updateSelected/checkSkuStock等方法
- 1.3 完善`AfterSale`：补充全状态机方法(approve/reject/deliver/receive/refund/cancel)

### Step 2: 新建聚合根
- 2.1 创建`BrokerageRecord`聚合根（两级分佣、冻结/结算）
- 2.2 创建`BrokerageWithdraw`聚合根（提现审核、转账）

### Step 3: 创建领域服务
- 3.1 `TradeOrderStatusDomainService` — 自动取消/自动收货/自动评价的调度逻辑
- 3.2 `TradePriceCalculationService` — 价格计算编排（保持现有PriceCalculator链模式）
- 3.3 `AfterSaleValidateDomainService` — 售后申请前置校验
- 3.4 `BrokerageCalculationService` — 分佣计算

### Step 4: 完善仓储接口与实现
- 4.1 为BrokerageRecord/BrokerageWithdraw创建Repository
- 4.2 补充TradeOrderRepository的CAS更新方法

### Step 5: 创建应用服务
- 5.1 订单应用服务(TradeOrderApplicationService)：下单/支付回调/发货/收货/取消/调价/售后联动
- 5.2 购物车应用服务：增删改查
- 5.3 售后应用服务：申请/审批/退货/退款
- 5.4 佣金应用服务：分佣/提现

### Step 6: 验证
- 6.1 运行全部单元测试
- 6.2 编译通过
- 6.3 状态机全路径覆盖确认
