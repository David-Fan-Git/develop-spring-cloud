# DDD Skill: AggregateRoot_Mall_Skill

## 1. 技能名称
`AggregateRoot_Mall_Skill` — 商城(Mall)聚合根的领域建模与重构技能

## 2. 适用场景
商城模块四个子域：商品(Product)、营销(Promotion)、交易(Trade)、统计(Statistics)。

## 3. DDD 构造块

### 3.1 聚合根
- Product: `Spu` (商品), `Sku` (SKU), `Category` (分类), `Brand` (品牌)
- Promotion: `Coupon` (优惠券), `Activity` (活动)
- Trade: `Order` (订单), `Cart` (购物车), `AfterSale` (售后)
- Statistics: `SalesStats`, `ProductStats`

### 3.2 值对象
- `Price`, `Money`, `Quantity`, `OrderNo`, `SkuCode`
- 不可变，自校验

### 3.3 仓储接口
每个聚合根对应一个Repository接口

### 3.4 领域服务
- `PriceCalculator` — 价格计算（含优惠券/满减/会员折扣）
- `StockReserver` — 库存预留

## 4. 职责边界
- **聚合负责**: 订单状态流转、优惠券使用规则、价格计算
- **严禁外泄**: 直接操作Mapper、直接库存操作

## 5. 验收标准
- AC01: 聚合根无MyBatis/Spring注解
- AC02: 值对象不可变
- AC03: 仓储接口在领域层
- AC04: 编译通过
