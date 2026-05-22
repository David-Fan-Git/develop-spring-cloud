package com.develop.mvp.pk.module.system.infrastructure.permission;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleMenuDO;
import com.develop.mvp.pk.module.system.dal.mysql.permission.RoleMenuMapper;
import com.develop.mvp.pk.module.system.domain.permission.repository.RoleMenuRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertSet;

@Repository
public class RoleMenuRepositoryImpl implements RoleMenuRepository {
    private final RoleMenuMapper mapper;
    public RoleMenuRepositoryImpl(RoleMenuMapper mapper) { this.mapper = mapper; }

    @Override
    public void assign(Long roleId, Set<Long> menuIds) {
        Set<Long> db = convertSet(mapper.selectListByRoleId(roleId), RoleMenuDO::getMenuId);
        Set<Long> ids = CollUtil.emptyIfNull(menuIds);
        Collection<Long> create = CollUtil.subtract(ids, db);
        Collection<Long> delete = CollUtil.subtract(db, ids);
        if (!create.isEmpty()) mapper.insertBatch(CollectionUtils.convertList(create,
                menuId -> new RoleMenuDO().setRoleId(roleId).setMenuId(menuId)));
        if (!delete.isEmpty()) mapper.deleteListByRoleIdAndMenuIds(roleId, delete);
    }

    @Override
    public Set<Long> findByRoleId(Long roleId) {
        return convertSet(mapper.selectListByRoleId(roleId), RoleMenuDO::getMenuId);
    }

    @Override
    public Set<Long> findByRoleIds(Collection<Long> roleIds) {
        return convertSet(mapper.selectListByRoleId(roleIds), RoleMenuDO::getMenuId);
    }

    @Override
    public Set<Long> findByMenuId(Long menuId) {
        return convertSet(mapper.selectListByMenuId(menuId), RoleMenuDO::getRoleId);
    }

    @Override
    public void deleteByRoleId(Long roleId) { mapper.deleteListByRoleId(roleId); }

    @Override
    public void deleteByMenuId(Long menuId) { mapper.deleteListByMenuId(menuId); }
}
