package com.develop.mvp.pk.module.system.infrastructure.permission;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.MenuDO;
import com.develop.mvp.pk.module.system.dal.mysql.permission.MenuMapper;
import com.develop.mvp.pk.module.system.domain.permission.Menu;
import com.develop.mvp.pk.module.system.domain.permission.MenuFactory;
import com.develop.mvp.pk.module.system.domain.permission.repository.MenuRepository;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.MenuId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuMapper menuMapper;

    public MenuRepositoryImpl(MenuMapper menuMapper) { this.menuMapper = menuMapper; }

    @Override
    @Transactional
    public Menu save(Menu menu) {
        MenuDO d = toDataObject(menu);
        if (menuMapper.selectById(menu.id().value()) == null) menuMapper.insert(d);
        else menuMapper.updateById(d);
        return menu;
    }

    @Override
    @Transactional
    public void delete(MenuId id) { menuMapper.deleteById(id.value()); }

    @Override
    public Menu findById(MenuId id) {
        MenuDO d = menuMapper.selectById(id.value());
        return d != null ? toDomain(d) : null;
    }

    @Override
    public List<Menu> findByIds(Collection<MenuId> ids) {
        if (CollUtil.isEmpty(ids)) return Collections.emptyList();
        return menuMapper.selectByIds(ids.stream().map(MenuId::value).collect(Collectors.toList()))
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Menu> findAll() {
        return menuMapper.selectList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Menu> findByPermission(String permission) {
        return menuMapper.selectListByPermission(permission).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Menu> findByParentIdAndName(Long parentId, String name) {
        return Optional.ofNullable(menuMapper.selectByParentIdAndName(parentId, name)).map(this::toDomain);
    }

    @Override
    public Optional<Menu> findByComponentName(String componentName) {
        return Optional.ofNullable(menuMapper.selectByComponentName(componentName)).map(this::toDomain);
    }

    @Override
    public long countByParentId(MenuId parentId) {
        return menuMapper.selectCountByParentId(parentId.value());
    }

    private MenuDO toDataObject(Menu m) {
        MenuDO d = new MenuDO();
        d.setId(m.id().value()); d.setName(m.name().value());
        d.setPermission(m.permission().value()); d.setType(m.type().code());
        d.setSort(m.sort()); d.setParentId(m.parentId().value());
        d.setPath(m.path()); d.setIcon(m.icon());
        d.setComponent(m.component()); d.setComponentName(m.componentName());
        d.setStatus(m.status()); d.setVisible(m.visible());
        d.setKeepAlive(m.keepAlive()); d.setAlwaysShow(m.alwaysShow());
        return d;
    }

    private Menu toDomain(MenuDO d) {
        return MenuFactory.reconstitute(d.getId(), d.getName(), d.getPermission(), d.getType(),
                d.getSort(), d.getParentId(), d.getPath(), d.getIcon(), d.getComponent(),
                d.getComponentName(), d.getStatus(), d.getVisible(), d.getKeepAlive(), d.getAlwaysShow());
    }
}
