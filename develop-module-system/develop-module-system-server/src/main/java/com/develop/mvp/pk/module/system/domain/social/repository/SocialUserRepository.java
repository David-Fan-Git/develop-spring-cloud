package com.develop.mvp.pk.module.system.domain.social.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.social.SocialUser;
import java.util.List;
import java.util.Optional;

public interface SocialUserRepository {
    SocialUser save(SocialUser u);
    void delete(Long id);
    SocialUser findById(Long id);
    Optional<SocialUser> findByUserTypeAndOpenid(Integer userType, Integer type, String openid);
    List<SocialUser> findByUserIdAndUserType(Long userId, Integer userType);
    PageResult<SocialUser> findPage(String nickname, Integer userType, Integer type, Integer pageNo, Integer pageSize);
}
