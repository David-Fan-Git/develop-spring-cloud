package com.develop.mvp.pk.module.system.application.permission.service;

import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.RoleUseCase;
import com.develop.mvp.pk.module.system.domain.permission.Role;
import com.develop.mvp.pk.module.system.domain.permission.RoleFactory;
import com.develop.mvp.pk.module.system.domain.permission.repository.RoleRepository;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.DataScope;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.RoleId;
import com.develop.mvp.pk.module.system.enums.permission.DataScopeEnum;
import com.develop.mvp.pk.module.system.enums.permission.RoleCodeEnum;
import com.develop.mvp.pk.module.system.enums.permission.RoleTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Set;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
public class RoleApplicationService implements RoleUseCase {

    private final RoleRepository roleRepository;

    public RoleApplicationService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role createRole(String name, String code, Integer sort, Integer status, String remark, Integer type) {
        validateRoleDuplicate(name, code, null);
        Role role = RoleFactory.create(null, name, code, sort,
                status != null ? status : CommonStatusEnum.ENABLE.getStatus(),
                type != null ? type : RoleTypeEnum.CUSTOM.getType(),
                remark, null, DataScopeEnum.ALL.getScope(), null);
        return roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateRole(Long id, String name, String code, Integer sort, Integer status, String remark) {
        Role role = validateRoleForUpdate(id);
        validateRoleDuplicate(name, code, id);
        role.changeBaseInfo(name, code, sort, status, remark);
        return roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        Role role = validateRoleForUpdate(id);
        role.changeDataScope(DataScope.of(dataScope, dataScopeDeptIds));
        return roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role deleteRole(Long id) {
        Role role = validateRoleForUpdate(id);
        role.markDeleted();
        roleRepository.delete(RoleId.of(id));
        return role;
    }

    @Override
    public Role getRole(Long id) {
        return roleRepository.findById(RoleId.of(id));
    }

    @Override
    public void validateRoleDuplicate(String name, String code, Long id) {
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        roleRepository.findByName(name).ifPresent(role -> {
            if (!role.hasId(id)) {
                throw exception(ROLE_NAME_DUPLICATE, name);
            }
        });
        if (!StringUtils.hasText(code)) {
            return;
        }
        roleRepository.findByCode(code).ifPresent(role -> {
            if (!role.hasId(id)) {
                throw exception(ROLE_CODE_DUPLICATE, code);
            }
        });
    }

    @Override
    public Role validateRoleForUpdate(Long id) {
        Role role = roleRepository.findById(RoleId.of(id));
        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
        if (role.isSystem()) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
        return role;
    }
}
