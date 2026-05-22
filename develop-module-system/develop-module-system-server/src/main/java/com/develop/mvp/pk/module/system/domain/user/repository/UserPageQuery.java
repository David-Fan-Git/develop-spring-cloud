package com.develop.mvp.pk.module.system.domain.user.repository;

// Skill: AggregateRoot_User_Validation_Skill — 查询对象 UserPageQuery
// DDD 角色：值对象，封装分页查询条件

import java.util.Collection;
import java.util.Collections;

public final class UserPageQuery {

    private final String username;
    private final String mobile;
    private final Integer status;
    private final Collection<Long> deptIds;
    private final Collection<Long> userIds;
    private final int pageNo;
    private final int pageSize;

    private UserPageQuery(Builder builder) {
        this.username = builder.username;
        this.mobile = builder.mobile;
        this.status = builder.status;
        this.deptIds = builder.deptIds != null ? builder.deptIds : Collections.emptySet();
        this.userIds = builder.userIds != null ? builder.userIds : Collections.emptySet();
        this.pageNo = builder.pageNo;
        this.pageSize = builder.pageSize;
    }

    public static Builder builder() { return new Builder(); }

    public String username() { return username; }
    public String mobile() { return mobile; }
    public Integer status() { return status; }
    public Collection<Long> deptIds() { return deptIds; }
    public Collection<Long> userIds() { return userIds; }
    public int pageNo() { return pageNo; }
    public int pageSize() { return pageSize; }

    public static class Builder {
        private String username;
        private String mobile;
        private Integer status;
        private Collection<Long> deptIds;
        private Collection<Long> userIds;
        private int pageNo = 1;
        private int pageSize = 10;

        public Builder username(String v) { this.username = v; return this; }
        public Builder mobile(String v) { this.mobile = v; return this; }
        public Builder status(Integer v) { this.status = v; return this; }
        public Builder deptIds(Collection<Long> v) { this.deptIds = v; return this; }
        public Builder userIds(Collection<Long> v) { this.userIds = v; return this; }
        public Builder pageNo(int v) { this.pageNo = v; return this; }
        public Builder pageSize(int v) { this.pageSize = v; return this; }
        public UserPageQuery build() { return new UserPageQuery(this); }
    }
}
