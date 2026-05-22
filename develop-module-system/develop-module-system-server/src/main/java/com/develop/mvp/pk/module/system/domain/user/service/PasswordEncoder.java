package com.develop.mvp.pk.module.system.domain.user.service;

import com.develop.mvp.pk.module.system.domain.user.valueobject.EncodedPassword;
import com.develop.mvp.pk.module.system.domain.user.valueobject.RawPassword;

// Skill: AggregateRoot_User_Validation_Skill — 领域服务接口 PasswordEncoder
// DDD 角色：领域层接口，由基础设施层 BCryptPasswordEncoderAdapter 实现
// 不变式 I04：密码永远以 BCrypt 密文存储
// 验收标准 AC15：密码加密/匹配通过领域层接口调用，不直接依赖 Spring Security

public interface PasswordEncoder {
    EncodedPassword encode(RawPassword rawPassword);
    boolean matches(RawPassword rawPassword, EncodedPassword encodedPassword);
}
