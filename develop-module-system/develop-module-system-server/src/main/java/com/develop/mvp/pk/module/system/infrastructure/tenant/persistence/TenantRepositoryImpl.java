package com.develop.mvp.pk.module.system.infrastructure.tenant.persistence;

// Skill: AggregateRoot_Tenant_Validation_Skill — 仓储实现 TenantRepositoryImpl
// DDD 角色：TenantRepository 的 MyBatis 实现，负责 DO ↔ 领域模型映射
// 验收标准 AC06：在基础设施层，import MyBatis 类

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.dal.dataobject.tenant.TenantDO;
import com.develop.mvp.pk.module.system.dal.mysql.tenant.TenantMapper;
import com.develop.mvp.pk.module.system.domain.tenant.Tenant;
import com.develop.mvp.pk.module.system.domain.tenant.TenantFactory;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantPageQuery;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantRepository;
import com.develop.mvp.pk.module.system.domain.tenant.valueobject.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TenantRepositoryImpl implements TenantRepository {

    private final TenantMapper tenantMapper;

    public TenantRepositoryImpl(TenantMapper tenantMapper) {
        this.tenantMapper = tenantMapper;
    }

    @Override
    @Transactional
    public Tenant create(String name, Long contactUserId, String contactName, String contactMobile,
                         TenantStatus status, List<String> websites, Long packageId,
                         java.time.LocalDateTime expireTime, Integer accountCount) {
        TenantStatus initialStatus = status != null ? status : TenantStatus.ENABLED;
        TenantDO tenantDO = new TenantDO();
        tenantDO.setName(name);
        tenantDO.setContactUserId(contactUserId);
        tenantDO.setContactName(contactName);
        tenantDO.setContactMobile(contactMobile);
        tenantDO.setStatus(initialStatus.code());
        tenantDO.setWebsites(websites);
        tenantDO.setPackageId(packageId);
        tenantDO.setExpireTime(expireTime);
        tenantDO.setAccountCount(accountCount);
        tenantMapper.insert(tenantDO);
        return TenantFactory.create(tenantDO.getId(), name, contactUserId, contactName, contactMobile,
                initialStatus, websites, packageId, expireTime, accountCount);
    }

    @Override
    @Transactional
    public Tenant save(Tenant tenant) {
        TenantDO tenantDO = toDataObject(tenant);
        if (tenantMapper.selectById(tenant.id().value()) == null) {
            tenantMapper.insert(tenantDO);
        } else {
            tenantMapper.updateById(tenantDO);
        }
        return tenant;
    }

    @Override
    @Transactional
    public void delete(TenantId id) {
        tenantMapper.deleteById(id.value());
    }

    @Override
    public Tenant findById(TenantId id) {
        TenantDO tenantDO = tenantMapper.selectById(id.value());
        return tenantDO != null ? toDomain(tenantDO) : null;
    }

    @Override
    public Optional<Tenant> findByName(TenantName name) {
        TenantDO tenantDO = tenantMapper.selectByName(name.value());
        return Optional.ofNullable(tenantDO).map(this::toDomain);
    }

    @Override
    public List<Tenant> findByWebsite(String website) {
        return tenantMapper.selectListByWebsite(website).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Tenant> findByPackageId(TenantPackageRef packageRef) {
        return tenantMapper.selectListByPackageId(packageRef.packageId()).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Tenant> findByStatus(TenantStatus status) {
        return tenantMapper.selectListByStatus(status.code()).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResult<Tenant> findPage(TenantPageQuery query) {
        var reqVO = new com.develop.mvp.pk.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO();
        reqVO.setName(query.name());
        reqVO.setContactName(query.contactName());
        reqVO.setContactMobile(query.contactMobile());
        reqVO.setStatus(query.status());
        reqVO.setCreateTime(query.createTime());
        reqVO.setPageNo(query.pageNo());
        reqVO.setPageSize(query.pageSize());

        PageResult<TenantDO> doPage = tenantMapper.selectPage(reqVO);
        List<Tenant> tenants = doPage.getList().stream()
                .map(this::toDomain).collect(Collectors.toList());
        return new PageResult<>(tenants, doPage.getTotal());
    }

    @Override
    public long countByPackageId(TenantPackageRef packageRef) {
        return tenantMapper.selectCountByPackageId(packageRef.packageId());
    }

    @Override
    public List<Tenant> findByIds(Collection<TenantId> ids) {
        if (CollUtil.isEmpty(ids)) return Collections.emptyList();
        List<Long> rawIds = ids.stream().map(TenantId::value).collect(Collectors.toList());
        return tenantMapper.selectByIds(rawIds).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Tenant> findAll() {
        return tenantMapper.selectList().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(TenantName name) {
        return tenantMapper.selectByName(name.value()) != null;
    }

    private TenantDO toDataObject(Tenant tenant) {
        TenantDO tenantDO = new TenantDO();
        tenantDO.setId(tenant.id().value());
        tenantDO.setName(tenant.name().value());
        tenantDO.setContactUserId(tenant.contactUserId());
        tenantDO.setContactName(tenant.contactName());
        tenantDO.setContactMobile(tenant.contactMobile());
        tenantDO.setStatus(tenant.status().code());
        tenantDO.setWebsites(tenant.websites());
        tenantDO.setPackageId(tenant.packageRef().packageId());
        tenantDO.setExpireTime(tenant.expireTime().value());
        tenantDO.setAccountCount(tenant.accountCount());
        return tenantDO;
    }

    private Tenant toDomain(TenantDO tenantDO) {
        return TenantFactory.reconstitute(
                tenantDO.getId(),
                tenantDO.getName(),
                tenantDO.getContactUserId(),
                tenantDO.getContactName(),
                tenantDO.getContactMobile(),
                tenantDO.getStatus(),
                tenantDO.getWebsites(),
                tenantDO.getPackageId(),
                tenantDO.getExpireTime(),
                tenantDO.getAccountCount(),
                tenantDO.getCreateTime(),
                tenantDO.getUpdateTime(),
                tenantDO.getCreator(),
                tenantDO.getUpdater(),
                tenantDO.getDeleted()
        );
    }
}
