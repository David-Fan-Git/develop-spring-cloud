package com.develop.mvp.pk.module.system.infrastructure.user.external;

// Skill: AggregateRoot_User_Validation_Skill — 基础设施适配器
// DDD 角色：PasswordEncoder 领域服务接口的实现，委托 Spring Security BCrypt
// 验收标准 AC15：密码加密/匹配通过领域层接口调用

import com.develop.mvp.pk.module.system.domain.user.service.PasswordEncoder;
import com.develop.mvp.pk.module.system.domain.user.valueobject.EncodedPassword;
import com.develop.mvp.pk.module.system.domain.user.valueobject.RawPassword;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public EncodedPassword encode(RawPassword rawPassword) {
        return EncodedPassword.of(delegate.encode(rawPassword.rawValue()));
    }

    @Override
    public boolean matches(RawPassword rawPassword, EncodedPassword encodedPassword) {
        return delegate.matches(rawPassword.rawValue(), encodedPassword.encodedValue());
    }
}
