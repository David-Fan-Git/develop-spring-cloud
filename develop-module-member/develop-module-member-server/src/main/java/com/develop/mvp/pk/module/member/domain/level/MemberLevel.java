package com.develop.mvp.pk.module.member.domain.level;
// DDD 角色：会员等级聚合根
import java.util.Objects;
public final class MemberLevel {
    private final Long id; private final String name; private Integer level, experience, discountPercent, status;
    public MemberLevel(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static MemberLevel of(Long id, String name) { return new MemberLevel(id, name); }
    public Long id() { return id; } public String name() { return name; }
    public Integer level() { return level; } public Integer experience() { return experience; }
    public Integer discountPercent() { return discountPercent; } public Integer status() { return status; }
    public MemberLevel level(Integer v) { level = v; return this; } public MemberLevel experience(Integer v) { experience = v; return this; }
    public MemberLevel discountPercent(Integer v) { discountPercent = v; return this; } public MemberLevel status(Integer v) { status = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof MemberLevel l && id.equals(l.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
