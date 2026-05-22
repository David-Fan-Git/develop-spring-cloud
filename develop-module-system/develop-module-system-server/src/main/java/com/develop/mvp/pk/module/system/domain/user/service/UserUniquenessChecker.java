package com.develop.mvp.pk.module.system.domain.user.service;

import com.develop.mvp.pk.module.system.domain.user.valueobject.Email;
import com.develop.mvp.pk.module.system.domain.user.valueobject.Mobile;
import com.develop.mvp.pk.module.system.domain.user.valueobject.UserId;
import com.develop.mvp.pk.module.system.domain.user.valueobject.Username;

// Skill: AggregateRoot_User_Validation_Skill — 领域服务接口 UserUniquenessChecker
// DDD 角色：领域层接口，检查用户名/邮箱/手机号唯一性（需要跨聚合查询，属于领域服务）
// 不变式 I01/I02/I03：用户名/邮箱/手机号在同一租户内不可重复
// 验收标准 AC13：唯一性校验通过此接口完成，不在聚合根内直接查 DB

public interface UserUniquenessChecker {
    boolean isUsernameUnique(Username username, UserId excludeUserId);
    boolean isEmailUnique(Email email, UserId excludeUserId);
    boolean isMobileUnique(Mobile mobile, UserId excludeUserId);
}
