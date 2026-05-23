package com.develop.mvp.pk.module.member.domain.user.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.member.domain.user.MemberUser;
import java.util.*;

public interface MemberUserRepository {
    MemberUser save(MemberUser u); void delete(Long id);
    MemberUser findById(Long id); Optional<MemberUser> findByMobile(String mobile);
    List<MemberUser> findByIds(Collection<Long> ids);
    PageResult<MemberUser> findPage(String nickname, String mobile, Integer status, Integer pageNo, Integer pageSize);
    long count();
}
