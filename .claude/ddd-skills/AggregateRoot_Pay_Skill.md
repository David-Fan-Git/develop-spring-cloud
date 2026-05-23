# DDD Skill: AggregateRoot_Pay_Skill

## 1. 技能名称
`AggregateRoot_Pay_Skill` — 支付(Pay)聚合根的领域建模与重构技能

## 2. 适用场景
支付模块：支付应用(App)、支付渠道(Channel)、支付订单(Order)、退款订单(Refund)、钱包(Wallet)。

## 3. DDD 构造块

### 3.1 聚合根
- `PayApp` — 支付应用聚合根
- `PayChannel` — 支付渠道聚合根  
- `PayOrder` — 支付订单聚合根
- `PayRefund` — 退款订单聚合根
- `PayWallet` — 钱包聚合根

### 3.2 值对象
- `PayAppId`, `PayChannelCode`, `PayOrderNo`, `PayAmount`, `PayStatus`, `RefundAmount`
- 不可变类，构造方法自校验

### 3.3 仓储接口
每个聚合根对应一个Repository接口，定义在domain层

### 3.4 领域服务
- `PaymentGateway` — 支付网关接口（在领域层定义，infrastructure层实现微信/支付宝等）
- `RefundCalculator` — 退款金额计算

## 4. 职责边界
- **聚合负责**: 支付状态流转、退款校验、金额有效性校验
- **严禁外泄**: 直接调用第三方支付API、直接操作Mapper

## 5. 验收标准
- AC01: 聚合根无MyBatis/Spring注解
- AC02: 值对象不可变(final class, final字段，无setter)
- AC03: 仓储接口在领域层，无infrastructure imports
- AC04: 仓储实现在infrastructure层
- AC05: 编译通过
- AC06: Controller使用ApplicationService
