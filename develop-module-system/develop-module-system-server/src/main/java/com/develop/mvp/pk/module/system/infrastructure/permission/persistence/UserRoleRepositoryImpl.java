package com.develop.mvp.pk.module.system.infrastructure.permission.persistence;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.UserRoleDO;
import com.develop.mvp.pk.module.system.dal.mysql.permission.UserRoleMapper;
import com.develop.mvp.pk.module.system.domain.permission.repository.UserRoleRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertSet;

@Repository
public class UserRoleRepositoryImpl implements UserRoleRepository {
    private final UserRoleMapper mapper;
    public UserRoleRepositoryImpl(UserRoleMapper mapper) { this.mapper = mapper; }

    @Override
    public void assign(Long userId, Set<Long> roleIds) {
        Set<Long> db = convertSet(mapper.selectListByUserId(userId), UserRoleDO::getRoleId);
        Set<Long> ids = CollUtil.emptyIfNull(roleIds);
        Collection<Long> create = CollUtil.subtract(ids, db);
        Collection<Long> delete = CollUtil.subtract(db, ids);
        if (!create.isEmpty()) mapper.insertBatch(CollectionUtils.convertList(create,
                roleId -> new UserRoleDO().setUserId(userId).setRoleId(roleId)));
        if (!delete.isEmpty()) mapper.deleteListByUserIdAndRoleIdIds(userId, delete);
    }

    @Override
    public Set<Long> findByUserId(Long userId) {
        return convertSet(mapper.selectListByUserId(userId), UserRoleDO::getRoleId);
    }

    @Override
    public Set<Long> findByRoleIds(Collection<Long> roleIds) {
        return convertSet(mapper.selectListByRoleIds(roleIds), UserRoleDO::getUserId);
    }

    @Override
    public void deleteByUserId(Long userId) { mapper.deleteListByUserId(userId); }

    @Override
    public void deleteByRoleId(Long roleId) { mapper.deleteListByRoleId(roleId); }
}
