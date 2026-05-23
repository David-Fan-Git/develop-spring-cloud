# DDD Skill: AggregateRoot_MemberUser_Skill

## 1. 技能名称
`AggregateRoot_MemberUser_Skill` — 会员用户(MemberUser)聚合根的领域建模与重构技能

## 2. 适用场景
会员用户的完整生命周期管理：创建(含自动注册)、更新基本信息、修改手机/密码、登录记录、积分变更、等级变更、状态管理、查询。

## 3. DDD 构造块

### 3.1 聚合根：MemberUser
`com.develop.mvp.pk.module.member.domain.user.MemberUser`
封装会员用户的业务规则和生命周期。

### 3.2 值对象
- `Mobile` — 手机号，非空+格式校验
- `EncodedPassword` — BCrypt加密密码
- `RawPassword` — 明文密码，长度≥6
- `Nickname` — 昵称，非空
- `UserStatus` — 状态 ENABLE/DISABLE
- `LoginRecord` — 登录记录(ip, date)

### 3.3 仓储接口
`domain/user/repository/MemberUserRepository` — 领域层接口

### 3.4 领域服务
`PasswordEncoder` — 密码加密/匹配接口

### 3.5 领域事件
- `MemberUserCreatedEvent` — 用户创建
- `MemberUserDeletedEvent` — 用户删除
- `MemberUserPasswordChangedEvent` — 密码变更

## 4. 职责边界
- **聚合负责**: 密码加密、状态管理、昵称默认值、手机唯一性校验(委托领域服务)
- **严禁外泄**: 直接操作Mapper、短信验证码校验、微信API调用、MQ消息发送

## 5. 验收标准
- AC01: MemberUser 无MyBatis/Spring注解
- AC02: 值对象不可变(final字段，无setter)
- AC03: MemberUserRepository接口在领域层
- AC04: MemberUserRepositoryImpl在infrastructure层
- AC05: 编译通过
- AC06: MemberUserController使用ApplicationService
