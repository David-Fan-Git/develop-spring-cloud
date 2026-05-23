package com.develop.mvp.pk.module.member.application.user;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.member.domain.user.MemberUser;
import com.develop.mvp.pk.module.member.domain.user.repository.MemberUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MemberUserApplicationService {
    private final MemberUserRepository repo;

    @Transactional public Long create(String nickname, String mobile, Integer status) {
        var u = MemberUser.of(null, nickname).mobile(mobile).status(status); repo.save(u); return u.id(); }
    @Transactional public void update(Long id, String nickname, String mobile, Integer status) {
        repo.save(MemberUser.of(id, nickname).mobile(mobile).status(status)); }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public MemberUser get(Long id) { return repo.findById(id); }
    public List<MemberUser> getList(Collection<Long> ids) { return repo.findByIds(ids); }
    public PageResult<MemberUser> getPage(String nickname, String mobile, Integer status, Integer pageNo, Integer pageSize) {
        return repo.findPage(nickname, mobile, status, pageNo, pageSize); }
}
