package com.develop.mvp.pk.module.system.domain.tenant;

// Skill: AggregateRoot_Tenant_Validation_Skill — 聚合根 Tenant
// DDD 角色：SaaS 租户的领域聚合根，封装租户完整生命周期和业务规则
// 职责边界：参见技能文档 4.1 节 R01-R09
// 依赖倒置：仅在构造时接收值对象，不依赖基础设施层
// 验收标准 AC01/AC02：无 MyBatis/Spring 注解，不注入 Mapper

import com.develop.mvp.pk.module.system.domain.tenant.event.*;
import com.develop.mvp.pk.module.system.domain.tenant.service.TenantUniquenessChecker;
import com.develop.mvp.pk.module.system.domain.tenant.valueobject.*;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;

import java.time.LocalDateTime;
import java.util.*;

public final class Tenant {

    // ── 聚合根标识 ──
    private final TenantId id;

    // ── 核心标识值对象 ──
    private TenantName name;

    // ── 外部聚合 ID 引用 ──
    private Long contactUserId;

    // ── 可变属性 ──
    private String contactName;
    private String contactMobile;
    private TenantStatus status;
    private List<String> websites;
    private TenantPackageRef packageRef;
    private TenantExpireTime expireTime;
    private Integer accountCount;

    // ── 领域事件收集 ──
    private final List<DomainEvent> events = new ArrayList<>();

    Tenant(TenantId id, TenantName name, Long contactUserId, String contactName,
           String contactMobile, TenantStatus status, List<String> websites,
           TenantPackageRef packageRef, TenantExpireTime expireTime, Integer accountCount) {
        this.id = Objects.requireNonNull(id, "tenantId 不能为空");
        this.name = Objects.requireNonNull(name, "tenantName 不能为空");
        this.contactUserId = contactUserId;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
        this.status = status != null ? status : TenantStatus.ENABLED; // 规则 R01
        this.websites = websites != null ? new ArrayList<>(websites) : new ArrayList<>();
        this.packageRef = Objects.requireNonNull(packageRef, "packageRef 不能为空");
        this.expireTime = Objects.requireNonNull(expireTime, "expireTime 不能为空");
        this.accountCount = accountCount != null ? accountCount : 0;
    }

    // ── 业务方法 ──

    /** 规则 R08：禁用租户 */
    public void disable() {
        if (this.status.isDisabled()) return;
        this.status = this.status.disable();
        events.add(new TenantDisabledEvent(this.id.value()));
    }

    /** 启用租户 */
    public void enable() {
        this.status = this.status.enable();
    }

    /** 更新租户基本信息 */
    public void updateProfile(TenantName newName, String contactName, String contactMobile,
                              List<String> websites,
                              TenantUniquenessChecker checker) {
        if (!this.name.equals(newName) && !checker.isNameUnique(newName, this.id)) {
            throw new IllegalArgumentException("租户名称已被使用"); // 规则 R02, 不变式 I01
        }
        if (websites != null) {
            for (String website : websites) {
                if (!this.websites.contains(website) && !checker.isWebsiteUnique(website, this.id)) {
                    throw new IllegalArgumentException("域名已被使用: " + website); // 规则 R03, 不变式 I02
                }
            }
        }
        this.name = newName;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
        this.websites = websites != null ? new ArrayList<>(websites) : this.websites;
    }

    /** 规则 R09：变更租户套餐 */
    public void changePackage(TenantPackageRef newPackageRef) {
        if (!this.packageRef.equals(newPackageRef)) {
            this.packageRef = Objects.requireNonNull(newPackageRef);
        }
    }

    /** 更新过期时间和账号配额 */
    public void updateExpiration(TenantExpireTime newExpireTime, Integer accountCount) {
        this.expireTime = Objects.requireNonNull(newExpireTime);
        this.accountCount = accountCount != null ? accountCount : this.accountCount;
    }

    /** 更新管理员用户ID */
    public void setContactUser(Long userId) {
        this.contactUserId = userId;
    }

    /** 规则 R04：标记删除（系统租户不可删除，由应用层校验） */
    public void markDeleted() {
        if (this.packageRef.isSystem()) {
            throw new IllegalStateException("系统租户不能删除"); // 不变式 I04 防御性校验
        }
        events.add(new TenantDeletedEvent(this.id.value(), this.name.value()));
    }

    /**
     * 规则 R07：校验租户是否有效（存在+启用+未过期）
     * 返回 null 表示有效，返回错误消息表示无效
     */
    public String validateActive() {
        if (this.status.isDisabled()) {
            return "租户已被禁用"; // 不变式 I03
        }
        if (this.expireTime.isExpired()) {
            return "租户已过期"; // 不变式 I05
        }
        return null;
    }

    /** 是否为系统租户（规则 R04） */
    public boolean isSystem() {
        return this.packageRef.isSystem();
    }

    // ── 查询方法 ──

    public TenantId id() { return id; }
    public TenantName name() { return name; }
    public Long contactUserId() { return contactUserId; }
    public String contactName() { return contactName; }
    public String contactMobile() { return contactMobile; }
    public TenantStatus status() { return status; }
    public List<String> websites() { return Collections.unmodifiableList(websites); }
    public TenantPackageRef packageRef() { return packageRef; }
    public TenantExpireTime expireTime() { return expireTime; }
    public Integer accountCount() { return accountCount; }

    public boolean isEnabled() { return status.isEnabled(); }
    public boolean isDisabled() { return status.isDisabled(); }
    public boolean isExpired() { return expireTime.isExpired(); }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> result = new ArrayList<>(events);
        events.clear();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tenant that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Tenant{id=" + id + ", name=" + name + '}';
    }
}
