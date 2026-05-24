package com.develop.mvp.pk.module.system.domain.permission;

// Skill: AggregateRoot_Role_Menu_Skill — 聚合根 Role
// DDD 角色：角色聚合根，封装角色身份、状态、数据范围，内部持有菜单关联
// 验收标准 AC01：无 MyBatis/Spring 注解

import com.develop.mvp.pk.module.system.domain.permission.event.*;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.*;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;

import java.util.*;

public final class Role {
    private final RoleId id;
    private RoleName name;
    private RoleCode code;
    private Integer sort;
    private RoleStatus status;
    private final RoleType type;
    private String remark;
    private final Long tenantId;
    private DataScope dataScope;
    private final Set<Long> menuIds;
    private final List<DomainEvent> events = new ArrayList<>();

    Role(RoleId id, RoleName name, RoleCode code, Integer sort, RoleStatus status,
         RoleType type, String remark, Long tenantId, DataScope dataScope, Set<Long> menuIds) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.code = Objects.requireNonNull(code);
        this.sort = sort != null ? sort : 0;
        this.status = status != null ? status : RoleStatus.ENABLED;
        this.type = type != null ? type : RoleType.CUSTOM;
        this.remark = remark;
        this.tenantId = tenantId;
        this.dataScope = dataScope != null ? dataScope : DataScope.all();
        this.menuIds = menuIds != null ? new HashSet<>(menuIds) : new HashSet<>();
    }

    // ── 业务方法 ──

    public void changeBaseInfo(String name, String code, Integer sort, Integer status, String remark) {
        this.name = RoleName.of(name);
        this.code = RoleCode.of(code);
        this.sort = sort != null ? sort : 0;
        this.status = status != null ? RoleStatus.of(status) : RoleStatus.ENABLED;
        this.remark = remark;
    }

    /** 规则 RR05：系统角色不可删除/修改 */
    public boolean isSystem() { return type.isSystem(); }

    public void changeDataScope(DataScope newDataScope) {
        this.dataScope = Objects.requireNonNull(newDataScope);
    }

    public void syncMenus(Set<Long> newMenuIds) {
        menuIds.clear();
        if (newMenuIds != null) menuIds.addAll(newMenuIds);
    }

    public void markDeleted() {
        if (isSystem()) throw new IllegalStateException("系统角色不能删除");
        events.add(new RoleDeletedEvent(this.id.value(), this.code.value()));
    }

    // ── 查询方法 ──

    public RoleId id() { return id; }
    public RoleName name() { return name; }
    public RoleCode code() { return code; }
    public Integer sort() { return sort; }
    public RoleStatus status() { return status; }
    public RoleType type() { return type; }
    public String remark() { return remark; }
    public Long tenantId() { return tenantId; }
    public DataScope dataScope() { return dataScope; }
    public Set<Long> menuIds() { return Collections.unmodifiableSet(menuIds); }
    public boolean isEnabled() { return status.isEnabled(); }
    public boolean hasId(Long otherId) { return id != null && id.value().equals(otherId); }

    public List<DomainEvent> pullEvents() { List<DomainEvent> r = new ArrayList<>(events); events.clear(); return r; }

    @Override public boolean equals(Object o) { return o instanceof Role r && Objects.equals(id, r.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
