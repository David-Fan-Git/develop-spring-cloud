# DDD Skill: AggregateRoot_Pay_Skill

## 1. 技能名称

`AggregateRoot_Pay_Skill` — 支付（Pay）模块全部9个聚合根的领域建模与重构技能

## 2. 适用场景

本技能针对 **支付模块** 的完整生命周期管理，覆盖以下聚合根和业务操作：

### 2.1 聚合根清单

| 序号 | 聚合根 | 领域路径 | 角色 |
|------|--------|---------|------|
| A1 | `PayApp` | `domain/app/PayApp.java` | 支付应用（商户应用） |
| A2 | `PayChannel` | `domain/channel/PayChannel.java` | 支付渠道（支付宝/微信等） |
| A3 | `PayOrder` | `domain/order/PayOrder.java` | 支付订单（主单） |
| A4 | `PayRefund` | `domain/refund/PayRefund.java` | 退款订单 |
| A5 | `PayTransfer` | `domain/transfer/PayTransfer.java` | 转账单 |
| A6 | `PayWallet` | `domain/wallet/PayWallet.java` | 用户钱包 |
| A7 | `PayWalletRecharge` | `domain/wallet/PayWalletRecharge.java` | 钱包充值记录（聚合内部实体） |
| A8 | `PayWalletRechargePackage` | `domain/wallet/PayWalletRechargePackage.java` | 钱包充值套餐 |
| A9 | `PayWalletTransaction` | `domain/wallet/PayWalletTransaction.java` | 钱包交易流水（聚合内部实体） |

### 2.2 业务操作覆盖

- 支付应用：创建、更新、删除、启用/禁用、有效性校验（存在+启用）
- 支付渠道：创建、更新、删除、启用/禁用、配置解析、有效性校验
- 支付订单：创建、提交（发起支付）、支付成功回调、支付失败回调、过期关闭、价格更新、退款金额更新、同步查询三方、订单过期定时任务
- 退款订单：创建（发起退款）、退款成功回调、退款失败回调、同步查询三方
- 转账单：创建（发起转账）、转账成功/失败/处理中回调、同步查询三方、重试发起
- 钱包：创建（按需）、余额扣减、余额增加、余额冻结、余额解冻、支付扣款、退款回退
- 钱包充值：创建充值单、支付成功回调、申请退款、退款结果处理
- 钱包充值套餐：创建、更新、删除、有效性校验（存在+启用）
- 钱包交易流水：创建流水、分页查询、汇总查询

## 3. DDD 构造块

### 3.1 聚合根边界定义

#### A1: PayApp（支付应用聚合根）

```
com.develop.mvp.pk.module.pay.domain.app.PayApp
```

**聚合边界**：
- PayApp（根实体）
- 不包含：PayChannel、PayOrder、PayRefund、PayTransfer — 这些是外部聚合，仅通过 appId 引用

**已存在代码**：
- `PayApp.java` — 聚合根（fluent setter 模式，需完善为行为丰富的方法）
- `PayAppFactory.java` — 工厂（create + restore）
- `AppStatus.java` — 值对象
- `PayAppRepository.java` — 仓储接口
- `PayAppApplicationService.java` — 应用服务
- 领域事件：`PayAppCreatedEvent`、`PayAppStatusUpdatedEvent`

#### A2: PayChannel（支付渠道聚合根）

```
com.develop.mvp.pk.module.pay.domain.channel.PayChannel
```

**聚合边界**：
- PayChannel（根实体）
- 不包含：PayApp、PayOrder、PayRefund、PayTransfer — 通过 appId/channelId 引用

**已存在代码**：
- `PayChannel.java` — 聚合根
- `PayChannelFactory.java` — 工厂
- `ChannelCode.java` — 值对象
- `PayChannelRepository.java` — 仓储接口
- `PayChannelApplicationService.java` — 应用服务
- 领域事件：`PayChannelCreatedEvent`、`PayChannelUpdatedEvent`

#### A3: PayOrder（支付订单聚合根）

```
com.develop.mvp.pk.module.pay.domain.order.PayOrder
```

**聚合边界**：
- PayOrder（根实体）
- PayOrderExtension（聚合内部实体，表示每次支付尝试的拓展单）
- 不包含：PayApp、PayChannel、PayRefund — 通过 appId/channelId 引用

**已存在代码**：
- `PayOrder.java` — 聚合根
- `PayOrderFactory.java` — 工厂（create + restore）
- `OrderStatus.java` — 值对象（状态枚举+状态机方法）
- `OrderPrice.java` — 值对象（金额不可变+运算方法）
- `PayOrderRepository.java` — 仓储接口
- `PayOrderApplicationService.java` — 应用服务
- 领域事件：`PayOrderCreatedEvent`、`PayOrderPaidEvent`、`PayOrderClosedEvent`、`PayOrderRefundedEvent`

**重构前代码中的额外实体**：
- `PayOrderExtensionDO` — 应重构为聚合内部实体 `PayOrderExtension`，生命周期由 PayOrder 管理

#### A4: PayRefund（退款订单聚合根）

```
com.develop.mvp.pk.module.pay.domain.refund.PayRefund
```

**聚合边界**：
- PayRefund（根实体）
- 不包含：PayOrder、PayChannel — 通过 orderId/channelId 引用

**已存在代码**：
- `PayRefund.java` — 聚合根
- `PayRefundFactory.java` — 工厂
- `RefundStatus.java` — 值对象（状态枚举）
- `PayRefundRepository.java` — 仓储接口
- `PayRefundApplicationService.java` — 应用服务
- 领域事件：`PayRefundCreatedEvent`、`PayRefundSuccessEvent`、`PayRefundFailedEvent`

#### A5: PayTransfer（转账单聚合根）

```
com.develop.mvp.pk.module.pay.domain.transfer.PayTransfer
```

**聚合边界**：
- PayTransfer（根实体）
- 不包含：PayApp、PayChannel — 通过 appId/channelId 引用

**已存在代码**：
- `PayTransfer.java` — 聚合根
- `PayTransferFactory.java` — 工厂
- `TransferStatus.java` — 值对象
- `PayTransferRepository.java` — 仓储接口
- `PayTransferApplicationService.java` — 应用服务
- 领域事件：`PayTransferCreatedEvent`、`PayTransferSuccessEvent`、`PayTransferClosedEvent`

#### A6: PayWallet（钱包聚合根）

```
com.develop.mvp.pk.module.pay.domain.wallet.PayWallet
```

**聚合边界**：
- PayWallet（根实体）
- PayWalletTransaction（聚合内部实体，不可变的交易流水记录）
- PayWalletRecharge（聚合内部实体，充值记录）
- 不包含：PayOrder、PayRefund — 通过 bizId 引用

**已存在代码**：
- `PayWallet.java` — 聚合根
- `PayWalletFactory.java` — 工厂
- `WalletBalance.java` — 值对象（余额+冻结金额的不可变值对象）
- `PayWalletRepository.java` — 仓储接口
- `PayWalletApplicationService.java` — 应用服务（getOrCreate, addBalance, deductBalance, freezePrice）
- `PayWalletTransaction.java` — 聚合内部实体
- `PayWalletTransactionRepository.java` — 仓储接口
- `PayWalletRecharge.java` — 聚合内部实体
- `PayWalletRechargeRepository.java` — 仓储接口
- 领域事件：`PayWalletCreatedEvent`、`PayWalletBalanceChangedEvent`

#### A7: PayWalletRecharge（钱包充值记录 — 聚合内部实体）

```
com.develop.mvp.pk.module.pay.domain.wallet.PayWalletRecharge
```

**边界**：PayWalletRecharge 是 PayWallet 聚合的内部实体，其生命周期完全由 PayWallet 管理。不独立对外暴露仓储操作。

#### A8: PayWalletRechargePackage（钱包充值套餐实体）

```
com.develop.mvp.pk.module.pay.domain.wallet.PayWalletRechargePackage
```

**边界**：独立实体（非聚合根），通过 packageId 被 PayWalletRecharge 引用。有自己的仓储和生命周期，但不作为聚合根。

#### A9: PayWalletTransaction（钱包交易流水 — 聚合内部实体）

```
com.develop.mvp.pk.module.pay.domain.wallet.PayWalletTransaction
```

**边界**：PayWalletTransaction 是 PayWallet 聚合的内部实体，创建后不可变，仅用于查询。

### 3.2 值对象（Value Objects）

**已实现的值对象**（在 `domain/*/valueobject/` 下已有）：

| 值对象 | 所属聚合 | 类名 | 封装字段 | 不可变 | 自校验 |
|--------|---------|------|---------|--------|--------|
| 应用状态 | PayApp | `AppStatus` | `Integer value` | ✅ final | ENABLE(0)/DISABLE(1) |
| 渠道编码 | PayChannel | `ChannelCode` | `String value` | ✅ final | 非空、非空白、isWechat/isAlipay/isWallet |
| 订单状态 | PayOrder | `OrderStatus` | `Integer value` | ✅ final | WAITING(0)/SUCCESS(10)/REFUND(20)/CLOSED(30)，状态机方法 |
| 订单金额 | PayOrder | `OrderPrice` | `Integer cents` | ✅ final | 非null且>=0，提供add/subtract/canRefund |
| 退款状态 | PayRefund | `RefundStatus` | `Integer value` | ✅ final | WAITING(0)/SUCCESS(10)/FAILURE(20)，isTerminal |
| 转账状态 | PayTransfer | `TransferStatus` | `Integer value` | ✅ final | WAITING(0)/PROCESSING(5)/SUCCESS(10)/CLOSED(20) |
| 钱包余额 | PayWallet | `WalletBalance` | `int balance, int freezePrice` | ✅ final | canDeduct/deduct/add/freeze/unfreeze |

**需新增的值对象**：

| 值对象 | 所属聚合 | 类名 | 封装字段 | 不可变 | 自校验 |
|--------|---------|------|---------|--------|--------|
| 支付金额（通用） | 全局 | `PayMoney` | `Integer cents` | ✅ final | 非null且>=0，分单位 |
| 费率 | PayChannel | `FeeRate` | `Double rate` | ✅ final | 0 <= rate <= 1 |
| 应用Key | PayApp | `AppKey` | `String value` | ✅ final | 非空、格式校验 |
| 商户订单号 | PayOrder | `MerchantOrderId` | `String value` | ✅ final | 非空 |
| 订单号生成器 | 全局 | 领域服务 | `OrderNoGenerator` | — | 基于Redis生成唯一no |
| 充值套餐状态 | PayWalletRechargePackage | `PackageStatus` | `Integer value` | ✅ final | ENABLE(0)/DISABLE(1) |
| 钱包业务类型 | PayWallet | `WalletBizType` | `Integer type, String desc` | ✅ final | PAYMENT/RECHARGE/PAYMENT_REFUND等 |
| 交易流水号 | PayWalletTransaction | `TransactionNo` | `String value` | ✅ final | 前缀"W"+redis自增 |

### 3.3 仓储接口（Repository，领域层）

每个聚合根对应一个 Repository 接口：

| 仓储接口 | 领域层路径 | 状态 |
|---------|-----------|------|
| `PayAppRepository` | `domain/app/repository/` | 已存在，需完善 |
| `PayChannelRepository` | `domain/channel/repository/` | 已存在 |
| `PayOrderRepository` | `domain/order/repository/` | 已存在，需增加extension相关方法 |
| `PayRefundRepository` | `domain/refund/repository/` | 已存在 |
| `PayTransferRepository` | `domain/transfer/repository/` | 已存在，需增加updateByIdAndStatus |
| `PayWalletRepository` | `domain/wallet/repository/` | 已存在，余额操作方法 |
| `PayWalletTransactionRepository` | `domain/wallet/repository/` | 已存在 |
| `PayWalletRechargeRepository` | `domain/wallet/repository/` | 已存在 |

### 3.4 领域服务（Domain Service）

| 领域服务 | 职责 | 原因 |
|---------|------|------|
| `PayAppUniquenessChecker` | 检查 appKey 在全局唯一 | 需要跨聚合查询 |
| `PaymentGateway` | 支付网关接口（领域层定义），向三方发起支付/退款/转账 | 防腐层，隔离第三方支付 |
| `OrderNoGenerator` | 生成全局唯一的订单号/退款号/转账号 | 需要Redis支持，通过接口倒置 |
| `ChannelFeeCalculator` | 根据费率和金额计算渠道手续费 | 领域层计算规则 |
| `WalletLockManager` | 钱包余额操作的分布式锁 | 并发控制，通过接口倒置 |

**注意**：`PaymentGateway` 在领域层定义为接口，基础设施层使用 PayClientFactory 实现，适配支付宝/微信等不同渠道。

### 3.5 领域事件（Domain Events）

**已存在的事件**：

| 事件 | 触发时机 | 携带数据 | 消费者 |
|------|---------|---------|--------|
| `PayAppCreatedEvent` | 支付应用创建成功后 | appId, appKey, name | 操作日志 |
| `PayAppStatusUpdatedEvent` | 应用状态变更后 | appId, status | 禁用关联渠道（可选） |
| `PayChannelCreatedEvent` | 渠道创建成功后 | channelId, code, appId | 操作日志 |
| `PayChannelUpdatedEvent` | 渠道更新后 | channelId, code | 操作日志 |
| `PayOrderCreatedEvent` | 支付订单创建后 | orderId, no, price | 操作日志 |
| `PayOrderPaidEvent` | 支付成功后 | orderId, no, channelId, channelOrderNo, price, channelFeePrice | 通知业务方、操作日志 |
| `PayOrderClosedEvent` | 订单关闭后 | orderId, no | 操作日志 |
| `PayOrderRefundedEvent` | 订单发生退款后 | orderId, refundPrice, totalRefundPrice | 操作日志 |
| `PayRefundCreatedEvent` | 退款单创建后 | refundId, no, orderId, refundPrice | 操作日志 |
| `PayRefundSuccessEvent` | 退款成功后 | refundId, no, orderId, refundPrice, successTime | 通知业务方、操作日志 |
| `PayRefundFailedEvent` | 退款失败后 | refundId, no, orderId, errorCode, errorMsg | 操作日志 |
| `PayTransferCreatedEvent` | 转账单创建后 | transferId, no, price | 操作日志 |
| `PayTransferSuccessEvent` | 转账成功后 | transferId, no, channelTransferNo, successTime | 通知业务方、操作日志 |
| `PayTransferClosedEvent` | 转账关闭后 | transferId, no, errorCode, errorMsg | 操作日志 |
| `PayWalletCreatedEvent` | 钱包创建后 | walletId, userId, userType | 暂无可扩展 |
| `PayWalletBalanceChangedEvent` | 钱包余额变化后 | walletId, userId, bizType, price, afterBalance | 通知、日志 |

### 3.6 工厂（Factory）

**已存在的工厂**（每个聚合一个工厂类，提供 `create` 和 `restore` 静态方法）：

| 工厂 | 路径 | 状态 |
|------|------|------|
| `PayAppFactory` | `domain/app/PayAppFactory.java` | 已存在，需完善create参数 |
| `PayChannelFactory` | `domain/channel/PayChannelFactory.java` | 已存在 |
| `PayOrderFactory` | `domain/order/PayOrderFactory.java` | 已存在 |
| `PayRefundFactory` | `domain/refund/PayRefundFactory.java` | 已存在 |
| `PayTransferFactory` | `domain/transfer/PayTransferFactory.java` | 已存在 |
| `PayWalletFactory` | `domain/wallet/PayWalletFactory.java` | 已存在 |

## 4. 职责边界

### 4.1 聚合根必须负责的规则

#### PayApp（支付应用）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R01 | 创建时，默认状态为 ENABLE(0) | `PayAppFactory.create()` L6 — `status(0)` |
| R02 | `appKey` 在全局不可重复 | `validateAppKeyUnique()` L70-82 |
| R03 | 支付应用存在且已启用才能用于支付流程 | `validatePayApp()` L155-164 |
| R04 | 有关联支付订单的应用不可删除 | `deleteApp()` L97-99 |
| R05 | 有关联退款订单的应用不可删除 | `deleteApp()` L100-102 |

#### PayChannel（支付渠道）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R06 | 同一应用下，渠道编码（code）不可重复 | `createChannel()` L53-56 |
| R07 | 渠道必须存在且启用才能用于支付流程 | `validPayChannel()` L145-152 |
| R08 | 渠道配置解析依赖渠道编码类型（支付宝JSON/微信JSON/无配置） | `parseConfig()` L83-97 |
| R09 | 渠道配置必须通过自身配置类的 `validate()` 方法校验 | `parseConfig()` L94-96 |
| R10 | 渠道费率（feeRate）用于计算渠道手续费 | `updateOrderSuccess()` L367-368 |

#### PayOrder（支付订单）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R11 | 订单状态流转：WAITING(0) -> SUCCESS(10) -> REFUND(20)，或 WAITING -> CLOSED(30) | `notifyOrder()` L278-290 |
| R12 | 提交支付时，订单必须在 WAITING 状态，未过期，且无已支付的拓展单 | `validateOrderCanSubmit()` L189-207 |
| R13 | 商户订单号在同一个应用下唯一 | `createOrder()` L121-127 |
| R14 | 订单过期时间到达后不可提交支付 | `validateOrderCanSubmit()` L200-202 |
| R15 | 订单价格更新仅在 WAITING 状态下允许 | `updatePayOrderPrice()` L439-441 |
| R16 | 退款总金额不能超过订单原价 | `updateOrderRefundPrice()` L419-421 |
| R17 | 退款仅在订单 SUCCESS 或 REFUND 状态下允许 | `updateOrderRefundPrice()` L416-418 |
| R18 | 支付拓展单（Extension）更新使用乐观锁（status条件更新） | `updateOrderSuccess()` L327-331 |
| R19 | 支付订单更新使用乐观锁（status条件更新） | `updateOrderSuccess()` L361-371 |
| R20 | 渠道手续费 = 订单金额 * 渠道费率，向下取整 | `updateOrderSuccess()` L367-368 — `MoneyUtils.calculateRatePrice()` |
| R21 | 支付成功回调需确保幂等（已支付则直接返回） | `updateOrderSuccess()` L351-354 |
| R22 | 支付成功时需创建支付通知任务 | `notifyOrderSuccess()` L302-303 |
| R23 | 支付关闭回调时，已支付的拓展单不更新为关闭（退款场景） | `updateOrderExtensionClosed()` L392-395 |
| R24 | 订单同步定时任务：查询三方后回调，三方返回关闭时不关闭（以回调为准） | `syncOrder()` L507-509 |
| R25 | 订单过期定时任务：检查拓展单无已支付状态，兜底关闭 | `expireOrder()` L544-599 |
| R26 | 支付拓展单状态不可回退（WAITING->SUCCESS 单向, WAITING->CLOSED 单向） | 所有状态更新方法 |

#### PayRefund（退款订单）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R27 | 退款状态流转：WAITING(0) -> SUCCESS(10) | FAILURE(20)（终态） | `notifyRefund()` L203-213 |
| R28 | 退款只能在订单 SUCCESS 或 REFUND 状态下发起 | `validatePayOrderCanRefund()` L161-163 |
| R29 | 退款金额 + 已退金额 <= 订单金额 | `validatePayOrderCanRefund()` L166-168 |
| R30 | 同一订单下不能有正在退款中的退款单 | `validatePayOrderCanRefund()` L170-173 |
| R31 | 商户退款号在同一应用下唯一 | `createRefund()` L106-110 |
| R32 | 退款单更新使用乐观锁（status条件更新） | `notifyRefundSuccess()` L235-237 |
| R33 | 退款单状态必须是 WAITING 才能被更新 | `notifyRefundSuccess()` L226-228 |
| R34 | 退款成功时需更新关联支付订单的已退金额 | `notifyRefundSuccess()` L242 |
| R35 | 退款成功/失败时需创建退款通知任务 | `notifyRefundSuccess()` L245-246, `notifyRefundFailure()` L276-277 |
| R36 | 退款同步定时任务：查询三方后回调结果 | `syncRefund()` L301-319 |

#### PayTransfer（转账单）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R37 | 转账状态流转：WAITING(0) -> PROCESSING(5) -> SUCCESS(10) | CLOSED(20)（终态） | `notifyTransfer()` L142-156 |
| R38 | 转账单只有 CLOSED 状态才能重新发起 | `validateTransferCanCreate()` L124 |
| R39 | 重新发起转账时，价格和渠道编码必须与原始一致 | `validateTransferCanCreate()` L128-133 |
| R40 | 转账单必须是 WAITING 或 PROCESSING 状态才能更新为 SUCCESS 或 CLOSED | `notifyTransferSuccess()` L193-194 |
| R41 | 转账单必须是 WAITING 状态才能更新为 PROCESSING | `notifyTransferProgressing()` L168-169 |
| R42 | 转账单更新使用乐观锁（status条件更新，支持多状态） | `notifyTransferSuccess()` L198-200 |
| R43 | 转账成功/关闭/处理中时需创建转账通知任务 | `notifyTransferSuccess()` L210, `notifyTransferClosed()` L240 |
| R44 | 转账同步定时任务：查询三方后回调结果 | `syncTransfer()` L281-298 |

#### PayWallet（钱包）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R45 | 钱包按需创建（getOrCreate），使用 Redis 双重检查锁避免并发 | `getOrCreateWallet()` L62-76 |
| R46 | 钱包余额不可为负数（SQL 层面保障 `balance >= 0`） | `reduceWalletBalance()` L160-161 — `updateWhenConsumption` |
| R47 | 钱包余额变更必须在 Redis 锁保护下进行 | `reduceWalletBalance()` L155, `addWalletBalance()` L202 |
| R48 | 钱包余额扣减时，余额不足则返回 WALLET_BALANCE_NOT_ENOUGH 异常 | `reduceWalletBalance()` L172-173 |
| R49 | 冻结金额时，可用余额必须足够：`balance - freezePrice >= amount` | `freezePrice()` L231-235 |
| R50 | 解冻金额时，冻结余额必须足够：`freezePrice >= amount` | `unfreezePrice()` L239-243 |
| R51 | 余额变更后必须创建对应的钱包交易流水 | `reduceWalletBalance()` L182-186, `addWalletBalance()` L223-227 |

#### PayWalletRecharge（钱包充值）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R52 | 充值总价 = 支付价 + 赠送价（换算逻辑在 CONVERT/BO 层） | `createWalletRecharge()` L94 |
| R53 | 充值单创建时需同时创建支付订单 | `createWalletRecharge()` L98-104 |
| R54 | 充值单支付回调幂等：已支付且支付单号相同则直接返回 | `updateWalletRechargerPaid()` L128-137 |
| R55 | 充值支付回调需校验支付单：已支付、金额匹配、商户订单匹配 | `validatePayOrderPaid()` L311-338 |
| R56 | 充值支付成功后，增加钱包余额（payPrice + bonusPrice） | `updateWalletRechargerPaid()` L153 |
| R57 | 充值退款前提：已支付且未退过款 | `validateWalletRechargeCanRefund()` L285-302 |
| R58 | 充值退款时，需先冻结钱包余额，再发起退款 | `refundWalletRecharge()` L204-218 |
| R59 | 充值退款结果处理：成功时扣减余额，失败时解冻 | `updateWalletRechargeRefunded()` L223-253 |

#### PayWalletRechargePackage（充值套餐）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R60 | 套餐名必须唯一 | `validateRechargePackageNameUnique()` L73-87 |
| R61 | 套餐必须存在且启用才能用于充值 | `validWalletRechargePackage()` L38-46 |

#### PayWalletTransaction（钱包交易流水）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R62 | 交易流水创建后不可修改（不可变记录） | 设计原则 |
| R63 | 交易流水的 no 前缀为 "W" | `WALLET_NO_PREFIX` L38 |
| R64 | 交易流水的 price 正数为收入，负数为支出 | `isIncome()` / `isExpense()` 方法 |

#### 通用规则（跨聚合）

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R65 | 所有支付通知任务在事务提交后异步执行 | `PayNotifyServiceImpl.createPayNotifyTask()` L120-128 |
| R66 | 通知任务有重试机制，按频次数组递增间隔 | `NOTIFY_FREQUENCY` + `processNotifyResult()` L270-297 |
| R67 | 回调地址格式：`{基础URL}/{渠道ID}` | `genChannelOrderNotifyUrl()` L258-260 |
| R68 | 所有操作支持租户隔离 | 各 `notify*` 方法中 `TenantUtils.execute()` |
| R69 | 支付订单/退款/转账的编号使用 Redis 自增生成，带业务前缀 | `PayNoRedisDAO.generate()` |
| R70 | 乐观锁更新（status条件更新）失败时抛出异常，防止并发覆盖 | 所有 `updateByIdAndStatus` 调用 |

### 4.2 严禁外泄的职责（不可放在 Pay 聚合内）

| 禁止行为 | 原因 | 应由谁处理 |
|---------|------|----------|
| 直接操作数据库/调用 Mapper | 破坏持久化无关性 | Repository 实现 |
| 直接调用第三方支付 API（支付宝/微信 SDK） | 防腐层隔离 | PaymentGateway 接口的实现 |
| 发送微信订阅消息/上传物流信息 | 基础设施关注点 | 领域事件订阅者 |
| 记录操作日志 | 基础设施关注点 | 领域事件订阅者 |
| Excel 导入导出逻辑 | 基础设施/应用关注点 | 应用层 Service |
| 处理 UI 层的 VO 转换 | 表示层关注点 | Controller/Convert |
| Redis 锁操作 | 基础设施关注点 | Repository 实现或基础设施层 |
| HTTP 回调通知（PayNotify） | 基础设施关注点 | PayNotifyApplicationService |
| 检查商户应用是否存在且启用（由 PayOrder 等调用） | PayApp 是独立聚合 | 应用层传入校验结果或通过领域服务接口 |

## 5. 依赖与协作

### 5.1 领域层依赖（向内）

各聚合根仅依赖：
- 自身值对象
- 领域服务接口（`PaymentGateway`, `OrderNoGenerator`, `PayAppUniquenessChecker` 等）
- 仓储接口
- 领域事件发布器

### 5.2 跨聚合协作（仅通过 ID 引用）

| 外部聚合 | 引用方式 | 协作场景 |
|---------|---------|---------|
| PayApp | `appId: Long` | PayOrder/PayRefund/PayTransfer 通过 appId 引用支付应用 |
| PayChannel | `channelId: Long` | PayOrder 通过 channelId/channelCode 引用支付渠道 |
| PayOrder | `orderId: Long` | PayRefund 通过 orderId 引用支付订单 |

### 5.3 基础设施依赖（向外，通过接口倒置）

```
领域层定义接口                   基础设施层实现
─────────────                   ──────────────
PayAppRepository        ←──     PayAppRepositoryImpl (委托 PayAppMapper)
PayChannelRepository    ←──     PayChannelRepositoryImpl (委托 PayChannelMapper)
PayOrderRepository      ←──     PayOrderRepositoryImpl (委托 PayOrderMapper + PayOrderExtensionMapper)
PayRefundRepository     ←──     PayRefundRepositoryImpl (委托 PayRefundMapper)
PayTransferRepository   ←──     PayTransferRepositoryImpl (委托 PayTransferMapper)
PayWalletRepository     ←──     PayWalletRepositoryImpl (委托 PayWalletMapper)
PaymentGateway          ←──     PayClientAdapter (委托 PayClientFactory + 各支付客户端)
OrderNoGenerator        ←──     RedisOrderNoGenerator (委托 PayNoRedisDAO)
DomainEventPublisher    ←──     SpringDomainEventPublisher (委托 Spring ApplicationEventPublisher)
```

## 6. 不变式与约束（Invariants）

### 6.1 PayApp 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I01 | `appKey` 在全局不可重复 | 跨聚合唯一性 | 创建/修改时 |
| I02 | `status` 只能是 ENABLE(0) 或 DISABLE(1) | 聚合内部（值对象） | 状态变更时 |
| I03 | 被禁用的支付应用不能被用于支付流程 | 聚合外部 | 提交支付/退款/转账时 |
| I04 | 有关联订单或退款的应用不可被删除 | 跨聚合约束 | 删除时 |

### 6.2 PayChannel 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I05 | 同一 app 下 `code` 不可重复 | 跨聚合唯一性 | 创建时 |
| I06 | `status` 只能是 ENABLE(0) 或 DISABLE(1) | 聚合内部 | 状态变更时 |
| I07 | 被禁用的渠道不能被用于支付流程 | 聚合外部 | 提交支付时 |
| I08 | `feeRate` 必须在 [0, 1] 范围内 | 聚合内部 | 创建/更新时 |

### 6.3 PayOrder 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I09 | 状态流转必须遵循：WAITING -> SUCCESS -> REFUND，或 WAITING -> CLOSED | 聚合内部 | 状态变更时 |
| I10 | 同一应用下 `merchantOrderId` 不可重复 | 跨聚合唯一性 | 创建时 |
| I11 | `refundPrice` 不可超过 `price` | 聚合内部 | 退款更新时 |
| I12 | `price` 在 WAITING 状态下可修改，支付后不可修改 | 聚合内部 | 价格更新时 |
| I13 | 过期的订单不可提交支付 | 聚合内部 | 提交支付时 |
| I14 | 渠道手续费 = `price * channelFeeRate`，单位为分 | 聚合内部 | 支付成功回调时 |
| I15 | 支付拓展单的 no 在全局唯一 | 跨聚合唯一性 | 创建拓展单时 |
| I16 | 每个支付订单在同一时刻只能有一个 WAITING 状态的拓展单被提交 | 聚合内部 | 提交支付时 |
| I17 | 支付成功回调必须幂等（重复回调不改变结果） | 聚合外部 | 回调处理时 |

### 6.4 PayRefund 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I18 | 状态流转：WAITING -> SUCCESS | FAILURE（终态不可逆） | 聚合内部 | 状态变更时 |
| I19 | 同一订单下不能有并发的 WAITING 退款单 | 跨聚合约束 | 创建退款时 |
| I20 | `refundPrice` + 订单已有 `refundPrice` <= 订单 `price` | 跨聚合约束 | 创建退款时 |
| I21 | `merchantRefundId` 在同一应用下唯一 | 跨聚合唯一性 | 创建时 |

### 6.5 PayTransfer 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I22 | 状态流转：WAITING -> PROCESSING -> SUCCESS | CLOSED（终态不可逆） | 聚合内部 | 状态变更时 |
| I23 | 只有 CLOSED 状态的转账单可以重新发起 | 聚合内部 | 重新发起时 |
| I24 | 重新发起时，`price` 和 `channelCode` 必须与原始一致 | 聚合内部 | 重新发起时 |

### 6.6 PayWallet 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I25 | `balance` 不可为负数（数据库/仓储层强制约束） | 聚合内部 | 扣减余额时 |
| I26 | `freezePrice` 不可超过 `balance` | 聚合内部 | 冻结时 |
| I27 | `balance - freezePrice` 为可用余额，必须 >= 0 | 聚合内部 | 任意余额变更时 |
| I28 | 同一用户同类型只有一个钱包 | 跨聚合唯一性 | 创建钱包时 |
| I29 | 每笔余额变更必须对应一条交易流水记录 | 聚合内部 | 余额变更时 |

### 6.7 PayWalletRecharge 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I30 | `totalPrice = payPrice + bonusPrice` | 聚合内部 | 创建时 |
| I31 | 充值只能支付一次（`payStatus` 从 false -> true 单向） | 聚合内部 | 支付回调时 |
| I32 | 退款前必须先冻结对应余额 | 跨聚合约束 | 发起退款时 |

### 6.8 PayWalletRechargePackage 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I33 | `name` 在全局不可重复 | 跨聚合唯一性 | 创建/修改时 |
| I34 | `status` 只能是 ENABLE(0) 或 DISABLE(1) | 聚合内部 | 状态变更时 |
| I35 | `payPrice > 0` 且 `bonusPrice >= 0` | 聚合内部 | 创建/更新时 |

### 6.9 PayWalletTransaction 不变式

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I36 | 创建后不可修改（仅 insert，无 update） | 聚合内部 | 设计约束 |
| I37 | `no` 在全局唯一，前缀"W" | 跨聚合唯一性 | 创建时 |
| I38 | `balance` 必须等于交易后钱包的余额快照 | 聚合内部 | 创建时 |

## 7. 验收标准

| 编号 | 验收标准 | 验证方法 |
|------|---------|---------|
| AC01 | 所有聚合根类不包含任何 MyBatis/Spring 注解（`@TableName`, `@TableId` 等） | 代码审查 |
| AC02 | 所有聚合根类不直接注入或调用 Mapper/Repository 实现类 | 代码审查 |
| AC03 | 所有聚合根构造方法或工厂方法确保不变式在创建时得到满足 | 代码审查/单元测试 |
| AC04 | `AppStatus`、`ChannelCode`、`OrderStatus`、`OrderPrice`、`RefundStatus`、`TransferStatus`、`WalletBalance` 为不可变值对象（final 类 + final 字段 + 无 setter） | 代码审查 |
| AC05 | 各 Repository 接口定义在领域层包（`domain/*/repository/`），不 import 任何 MyBatis 类 | 代码审查 |
| AC06 | 各 RepositoryImpl 在基础设施层（`infrastructure/*/`），import MyBatis 类并负责 DO<->领域模型映射 | 代码审查 |
| AC07 | 各聚合根的公共方法名称体现业务语义（`markPaid()`, `markClosed()`, `markRefund()`, `deductBalance()`, `freeze()` 等） | 代码审查 |
| AC08 | PayOrderApplicationService / PayRefundApplicationService 等仅保留编排逻辑，所有业务规则迁移到聚合根或值对象 | 代码审查 |
| AC09 | 支付状态流转全部在聚合根内通过 `OrderStatus` 值对象控制 | 代码审查 |
| AC10 | 退款金额不超过订单金额的校验在 PayOrder 聚合根而非 Service 层 | 代码审查 |
| AC11 | 钱包余额变更通过 `PayWallet.deductBalance()` / `addBalance()` 等方法完成 | 代码审查 |
| AC12 | 乐观锁更新（status条件更新）通过 Repository 的 `updateByIdAndStatus()` 方法实现 | 代码审查 |
| AC13 | 第三方支付调用通过 `PaymentGateway` 领域服务接口（非直接支付宝/微信 SDK） | 代码审查 |
| AC14 | 各领域事件被正确发布和消费（`PayOrderPaidEvent` -> 通知任务、`PayRefundSuccessEvent` -> 通知任务） | 集成测试 |
| AC15 | `PayWalletRechargeServiceImpl` 中的微信订阅消息和物流上传移至领域事件订阅者 | 代码审查 |
| AC16 | 通知任务创建逻辑移至应用服务层（PayNotifyApplicationService）或事件订阅者 | 代码审查 |
| AC17 | Redis 锁操作封装在基础设施层，领域层不直接依赖 Redis | 代码审查 |
| AC18 | 所有值对象的自校验在构造方法中完成（非法参数抛出IllegalArgumentException） | 单元测试 |
| AC19 | 每个类/方法注释中标注遵循的技能名称和对应规则编号 | 代码审查 |
| AC20 | 编译通过，所有现有单元测试通过 | 运行编译和测试 |

## 8. 目录结构规划（重构目标状态）

```
develop-module-pay/develop-module-pay-server/src/main/java/com/develop/mvp/pk/module/pay/
├── domain/                                                    # 领域层
│   ├── app/                                                   # PayApp 聚合
│   │   ├── PayApp.java                                        # 聚合根
│   │   ├── PayAppFactory.java                                 # 工厂
│   │   ├── valueobject/
│   │   │   └── AppStatus.java                                 # 值对象
│   │   ├── event/
│   │   │   ├── PayAppCreatedEvent.java
│   │   │   └── PayAppStatusUpdatedEvent.java
│   │   └── repository/
│   │       └── PayAppRepository.java                          # 仓储接口
│   ├── channel/                                               # PayChannel 聚合
│   │   ├── PayChannel.java                                    # 聚合根
│   │   ├── PayChannelFactory.java                             # 工厂
│   │   ├── valueobject/
│   │   │   ├── ChannelCode.java                               # 值对象
│   │   │   └── FeeRate.java                                   # 值对象（费率，需新增）
│   │   ├── event/
│   │   │   ├── PayChannelCreatedEvent.java
│   │   │   └── PayChannelUpdatedEvent.java
│   │   └── repository/
│   │       └── PayChannelRepository.java                      # 仓储接口
│   ├── order/                                                 # PayOrder 聚合
│   │   ├── PayOrder.java                                      # 聚合根
│   │   ├── PayOrderExtension.java                             # 聚合内部实体（需新建）
│   │   ├── PayOrderFactory.java                               # 工厂
│   │   ├── valueobject/
│   │   │   ├── OrderStatus.java                               # 值对象
│   │   │   ├── OrderPrice.java                                # 值对象
│   │   │   └── MerchantOrderId.java                           # 值对象（需新增）
│   │   ├── event/
│   │   │   ├── PayOrderCreatedEvent.java
│   │   │   ├── PayOrderPaidEvent.java
│   │   │   ├── PayOrderClosedEvent.java
│   │   │   └── PayOrderRefundedEvent.java
│   │   └── repository/
│   │       ├── PayOrderRepository.java                        # 仓储接口
│   │       └── PayOrderPageQuery.java                         # 分页查询
│   ├── refund/                                                # PayRefund 聚合
│   │   ├── PayRefund.java                                     # 聚合根
│   │   ├── PayRefundFactory.java                              # 工厂
│   │   ├── valueobject/
│   │   │   └── RefundStatus.java                              # 值对象
│   │   ├── event/
│   │   │   ├── PayRefundCreatedEvent.java
│   │   │   ├── PayRefundSuccessEvent.java
│   │   │   └── PayRefundFailedEvent.java
│   │   └── repository/
│   │       ├── PayRefundRepository.java                       # 仓储接口
│   │       └── PayRefundPageQuery.java                        # 分页查询
│   ├── transfer/                                              # PayTransfer 聚合
│   │   ├── PayTransfer.java                                   # 聚合根
│   │   ├── PayTransferFactory.java                            # 工厂
│   │   ├── valueobject/
│   │   │   └── TransferStatus.java                            # 值对象
│   │   ├── event/
│   │   │   ├── PayTransferCreatedEvent.java
│   │   │   ├── PayTransferSuccessEvent.java
│   │   │   └── PayTransferClosedEvent.java
│   │   └── repository/
│   │       ├── PayTransferRepository.java                     # 仓储接口
│   │       └── PayTransferPageQuery.java                      # 分页查询
│   ├── wallet/                                                # PayWallet 聚合
│   │   ├── PayWallet.java                                     # 聚合根
│   │   ├── PayWalletRecharge.java                             # 聚合内部实体（充值记录）
│   │   ├── PayWalletTransaction.java                          # 聚合内部实体（交易流水）
│   │   ├── PayWalletRechargePackage.java                      # 独立实体（充值套餐）
│   │   ├── PayWalletFactory.java                              # 工厂
│   │   ├── valueobject/
│   │   │   ├── WalletBalance.java                             # 值对象
│   │   │   ├── WalletBizType.java                             # 值对象（需新增）
│   │   │   └── PackageStatus.java                             # 值对象（需新增）
│   │   ├── event/
│   │   │   ├── PayWalletCreatedEvent.java
│   │   │   └── PayWalletBalanceChangedEvent.java
│   │   ├── repository/
│   │   │   ├── PayWalletRepository.java                       # 仓储接口
│   │   │   ├── PayWalletTransactionRepository.java            # 仓储接口
│   │   │   └── PayWalletRechargeRepository.java               # 仓储接口
│   │   └── service/
│   │       └── WalletUniquenessChecker.java                   # 领域服务接口（需新增）
│   ├── service/                                               # 公共领域服务接口
│   │   ├── PaymentGateway.java                                # 支付网关接口（防腐层）
│   │   ├── OrderNoGenerator.java                              # 编号生成器接口
│   │   ├── PayAppUniquenessChecker.java                       # AppKey唯一性校验
│   │   └── ChannelFeeCalculator.java                          # 渠道手续费计算
│   └── shared/                                                # 共享值对象
│       └── valueobject/
│           └── PayMoney.java                                  # 通用金额值对象（需新增）
├── application/                                               # 应用层
│   ├── app/
│   │   └── PayAppApplicationService.java                      # 应用服务
│   ├── channel/
│   │   └── PayChannelApplicationService.java                  # 应用服务
│   ├── order/
│   │   ├── PayOrderApplicationService.java                    # 应用服务（编排支付提交、回调）
│   │   ├── command/
│   │   │   ├── SubmitOrderCommand.java
│   │   │   └── NotifyOrderCommand.java
│   │   └── query/
│   │       └── PayOrderPageQuery.java
│   ├── refund/
│   │   ├── PayRefundApplicationService.java                   # 应用服务（编排退款创建、回调）
│   │   ├── command/
│   │   │   └── CreateRefundCommand.java
│   │   └── query/
│   │       └── PayRefundPageQuery.java
│   ├── transfer/
│   │   ├── PayTransferApplicationService.java                 # 应用服务
│   │   └── command/
│   │       └── CreateTransferCommand.java
│   └── wallet/
│       ├── PayWalletApplicationService.java                   # 应用服务（钱包CRUD、余额操作）
│       ├── PayWalletRechargeApplicationService.java           # 需新建——充值编排
│       └── command/
│           ├── RechargeCommand.java
│           └── RefundRechargeCommand.java
├── infrastructure/                                            # 基础设施层
│   ├── app/
│   │   └── PayAppRepositoryImpl.java                          # 仓储实现
│   ├── channel/
│   │   ├── PayChannelRepositoryImpl.java                      # 仓储实现
│   │   └── PayClientAdapter.java                              # PaymentGateway 实现（防腐层适配器）
│   ├── order/
│   │   ├── PayOrderRepositoryImpl.java                        # 仓储实现
│   │   └── subscriber/
│   │       ├── PayOrderPaidNotifyTaskCreator.java              # 支付成功->创建通知任务
│   │       └── PayOrderRefundedPriceUpdater.java               # 退款->更新订单已退金额
│   ├── refund/
│   │   ├── PayRefundRepositoryImpl.java                       # 仓储实现
│   │   └── subscriber/
│   │       └── PayRefundNotifyTaskCreator.java                 # 退款成功/失败->创建通知任务
│   ├── transfer/
│   │   ├── PayTransferRepositoryImpl.java                     # 仓储实现
│   │   └── subscriber/
│   │       └── PayTransferNotifyTaskCreator.java               # 转账成功/失败->创建通知任务
│   ├── wallet/
│   │   ├── PayWalletRepositoryImpl.java                       # 仓储实现
│   │   ├── PayWalletTransactionRepositoryImpl.java            # 交易流水仓储实现
│   │   ├── PayWalletRechargeRepositoryImpl.java               # 充值记录仓储实现
│   │   └── subscriber/
│   │       ├── WalletRechargePaidMessageSender.java            # 充值成功->发送微信订阅消息
│   │       └── WalletRechargePaidShippingUploader.java         # 充值成功->上传物流信息
│   ├── redis/
│   │   ├── RedisOrderNoGenerator.java                         # OrderNoGenerator 实现
│   │   └── RedisWalletLockManager.java                        # 钱包锁实现
│   └── notify/
│       ├── PayNotifyRepositoryImpl.java                       # 通知任务仓储实现
│       └── PayNotifyApplicationService.java                   # 迁移通知编排逻辑
├── controller/                                                # 接口层（保留，精简）
│   ├── admin/                                                 # 管理后台接口
│   └── app/                                                   # 用户端接口
├── convert/                                                   # 转换层（保留）
│   ├── app/PayAppConvert.java
│   ├── channel/PayChannelConvert.java
│   ├── order/PayOrderConvert.java
│   ├── refund/PayRefundConvert.java
│   ├── transfer/PayTransferConvert.java
│   └── wallet/...
├── dal/                                                       # 数据访问层（保留，重构）
│   ├── dataobject/
│   └── mysql/
└── enums/                                                     # 枚举（保留，逐步替换为值对象）
```

## 9. 回滚条件

如果以下任一情况发生，应回滚当前修改并重新分析：

1. 编译失败
2. 聚合根内部注入了基础设施依赖（如 Mapper、RedisTemplate 等）
3. 值对象存在 setter 或可变字段（final 字段被修改）
4. 业务规则从聚合根泄漏回 ApplicationService 层
5. 支付状态流转不在聚合根/值对象内完成，而在 Service 层通过 setStatus 控制
6. 乐观锁更新被移除（导致并发覆盖风险）
7. 第三方支付 API 的直接调用侵入领域层
8. 原有支付流程出现行为回归（支付成功/失败/退款回调逻辑不正确）
9. 钱包余额操作丢失 Redis 锁保护（导致并发安全漏洞）
10. 单元测试无法通过

## 10. 分步执行计划

由于支付模块包含 9 个聚合根且逻辑复杂，分 8 个阶段逐步推进：

### 阶段 1：值对象完善
**处理内容**：补充缺失的值对象，加固已有值对象的不可变性
- 新增 `FeeRate`、`MerchantOrderId`、`WalletBizType`、`PackageStatus`、`PayMoney` 等值对象
- 审查已有值对象确保 `final class` + `final` 字段 + 无 setter
- 确认自校验逻辑在构造方法中完整

### 阶段 2：PayApp + PayChannel 聚合完善
**处理内容**：完善这两个相对简单的聚合根
- 完善 `PayApp` 行为方法：`enable()`, `disable()`, `validate()` 等
- 完善 `PayChannel` 行为方法：`enable()`, `disable()`, `updateConfig()`, `validate()` 等
- 确保 `PayAppRepository` 和 `PayChannelRepository` 接口完整
- 实现基础设施层仓储实现
- 迁移原有 `PayAppServiceImpl` / `PayChannelServiceImpl` 中的业务规则到聚合根

### 阶段 3：PayOrder 聚合深度重构
**处理内容**：支付订单是最复杂的聚合，需重构 PayOrderExtension 为聚合内部实体
- 创建 `PayOrderExtension` 内部实体类（替代 `PayOrderExtensionDO`）
- 将 `PayOrder` 的 fluent setter 模式替换为行为丰富的方法：`submit()`, `markPaid()`, `markClosed()`, `applyRefund()` 等
- 状态流转全部委托给 `OrderStatus` 值对象
- 乐观锁更新保留在 Repository 中
- 运费计算委托给 `ChannelFeeCalculator` 领域服务接口
- 支付网关调用委托给 `PaymentGateway` 领域服务接口

### 阶段 4：PayRefund + PayTransfer 聚合完善
**处理内容**：完善退款和转账聚合根
- 确保状态机方法完整：`markSuccess()`, `markFailure()` 等
- 乐观锁更新保留
- 第三方退款/转账调用委托给 `PaymentGateway`
- 通知任务创建移至应用服务或事件订阅者

### 阶段 5：PayWallet 聚合重构
**处理内容**：钱包是第二大复杂聚合
- 确保 `WalletBalance` 值对象的不可变性被正确使用（当前 PayWallet.fluent setter 与 WalletBalance 不匹配，需抉择）
- 余额操作：`deductBalance()`, `addBalance()`, `freeze()`, `unfreeze()` 方法内包含完整校验
- Redis 锁通过 `WalletLockManager` 领域服务接口（基础设施层实现）
- 每笔余额变更强制创建 `PayWalletTransaction`

### 阶段 6：PayWalletRecharge + PayWalletRechargePackage
**处理内容**：充值及其套餐
- 充值创建、支付回调、退款编排逻辑移至 `PayWalletRechargeApplicationService`
- 微信订阅消息和物流上传移至领域事件订阅者
- 充值套餐的 `validWalletRechargePackage()` 校验逻辑移至实体本身

### 阶段 7：通知系统（PayNotify）重构
**处理内容**：通知任务创建和分发
- 创建 `PayNotifyApplicationService` 承接原 `PayNotifyServiceImpl` 的编排逻辑
- 通知任务创建通过领域事件订阅者触发（替代原 Service 中的手动调用）
- 确保事务提交后异步执行的语义不变

### 阶段 8：编译验证 + 测试
**处理内容**：最终验证
- 全量编译验证
- 路由测试：确认所有 Controller 端点行为一致
- 状态机测试：确认所有状态流转正确
- 并发测试：确认乐观锁和 Redis 锁保护完整
- 边界测试：确认负数金额、超额退款、重复回调等边界场景
