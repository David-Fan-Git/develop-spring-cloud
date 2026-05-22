package com.develop.mvp.pk.module.system.domain.permission;

// Skill: AggregateRoot_Role_Menu_Skill — 聚合根 Menu
// DDD 角色：菜单聚合根，树形结构，封装菜单属性与层级约束
// 验收标准 AC02：无 MyBatis/Spring 注解

import com.develop.mvp.pk.module.system.domain.permission.event.*;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.*;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;

import java.util.*;

public final class Menu {
    private final MenuId id;
    private final MenuName name;
    private final MenuPermission permission;
    private final MenuType type;
    private final Integer sort;
    private final MenuId parentId;
    private final String path;
    private final String icon;
    private final String component;
    private final String componentName;
    private final Integer status;
    private final Boolean visible;
    private final Boolean keepAlive;
    private final Boolean alwaysShow;
    private final List<DomainEvent> events = new ArrayList<>();

    Menu(MenuId id, MenuName name, MenuPermission permission, MenuType type, Integer sort,
         MenuId parentId, String path, String icon, String component, String componentName,
         Integer status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.permission = permission != null ? permission : MenuPermission.empty();
        this.type = Objects.requireNonNull(type);
        this.sort = sort != null ? sort : 0;
        this.parentId = Objects.requireNonNull(parentId);
        this.path = path;
        this.icon = icon;
        this.component = type.isButton() ? "" : component;
        this.componentName = type.isButton() ? "" : componentName;
        this.status = status != null ? status : 0;
        this.visible = visible != null ? visible : true;
        this.keepAlive = keepAlive != null ? keepAlive : false;
        this.alwaysShow = alwaysShow != null ? alwaysShow : false;
    }

    // ── 业务方法 ──

    /** 规则 MR01/MR02：校验父菜单合法性（由应用层调用） */
    public void validateParentAgainst(Menu parent) {
        if (parentId.isRoot()) return;
        if (parent == null) throw new IllegalArgumentException("父菜单不存在");
        if (!parent.type().isDirOrMenu()) throw new IllegalArgumentException("父菜单必须是目录或菜单类型");
        if (parentId.equals(id)) throw new IllegalArgumentException("不能设置自己为父菜单");
    }

    /** 规则 MR05：是否有子菜单（由应用层检查） */

    public void markDeleted() {
        events.add(new MenuDeletedEvent(this.id.value()));
    }

    /** 规则 MR06：按钮类型清空组件属性（构造时已处理） */

    // ── 查询方法 ──

    public MenuId id() { return id; }
    public MenuName name() { return name; }
    public MenuPermission permission() { return permission; }
    public MenuType type() { return type; }
    public Integer sort() { return sort; }
    public MenuId parentId() { return parentId; }
    public String path() { return path; }
    public String icon() { return icon; }
    public String component() { return component; }
    public String componentName() { return componentName; }
    public Integer status() { return status; }
    public Boolean visible() { return visible; }
    public Boolean keepAlive() { return keepAlive; }
    public Boolean alwaysShow() { return alwaysShow; }

    public List<DomainEvent> pullEvents() { List<DomainEvent> r = new ArrayList<>(events); events.clear(); return r; }

    @Override public boolean equals(Object o) { return o instanceof Menu m && id.equals(m.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
