package com.develop.mvp.pk.module.system.domain.user.repository;

import com.develop.mvp.pk.module.system.domain.user.User;
import com.develop.mvp.pk.module.system.domain.user.valueobject.*;
import com.develop.mvp.pk.framework.common.pojo.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

// Skill: AggregateRoot_User_Validation_Skill — 仓储接口 UserRepository
// DDD 角色：领域层接口，定义聚合根的持久化契约，不依赖任何基础设施
// 验收标准 AC06：定义在 domain.user 包，不 import MyBatis 类

public interface UserRepository {
    User create(String username, EncodedPassword encodedPassword, Long tenantId, Long deptId,
                String email, String mobile, String nickname, String avatar, Integer sex,
                String remark, java.util.Set<Long> postIds);
    void save(User user);
    void delete(UserId id);
    User findById(UserId id);
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);
    Optional<User> findByMobile(Mobile mobile);
    List<User> findByIds(Collection<UserId> ids);
    List<User> findByDeptIds(Collection<Long> deptIds);
    List<User> findByPostIds(Collection<Long> postIds);
    List<User> findByNickname(String nickname);
    List<User> findByStatus(UserStatus status);
    PageResult<User> findPage(UserPageQuery query);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    boolean existsByMobile(Mobile mobile);
    long count();
}
