package com.develop.mvp.pk.module.system.infrastructure.user.persistence;

// Skill: AggregateRoot_User_Validation_Skill — 领域服务实现
// DDD 角色：基础设施层实现 UserUniquenessChecker，委托 UserRepository
// 验收标准 AC13：唯一性校验通过此接口完成

import com.develop.mvp.pk.module.system.domain.user.repository.UserRepository;
import com.develop.mvp.pk.module.system.domain.user.service.UserUniquenessChecker;
import com.develop.mvp.pk.module.system.domain.user.valueobject.*;
import org.springframework.stereotype.Component;

@Component
public class UserUniquenessCheckerImpl implements UserUniquenessChecker {

    private final UserRepository userRepository;

    public UserUniquenessCheckerImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isUsernameUnique(Username username, UserId excludeUserId) {
        return userRepository.findByUsername(username)
                .map(u -> excludeUserId != null && u.id().equals(excludeUserId))
                .orElse(true);
    }

    @Override
    public boolean isEmailUnique(Email email, UserId excludeUserId) {
        if (!email.isPresent()) return true;
        return userRepository.findByEmail(email)
                .map(u -> excludeUserId != null && u.id().equals(excludeUserId))
                .orElse(true);
    }

    @Override
    public boolean isMobileUnique(Mobile mobile, UserId excludeUserId) {
        if (!mobile.isPresent()) return true;
        return userRepository.findByMobile(mobile)
                .map(u -> excludeUserId != null && u.id().equals(excludeUserId))
                .orElse(true);
    }
}
