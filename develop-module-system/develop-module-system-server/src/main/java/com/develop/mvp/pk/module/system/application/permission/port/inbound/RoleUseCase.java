package com.develop.mvp.pk.module.system.application.permission.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleDO;
import com.develop.mvp.pk.module.system.domain.permission.Role;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface RoleUseCase {

    Role createRole(String name, String code, Integer sort, Integer status, String remark, Integer type);

    Role updateRole(Long id, String name, String code, Integer sort, Integer status, String remark);

    Role updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds);

    Role deleteRole(Long id);

    Role getRole(Long id);

    void validateRoleDuplicate(String name, String code, Long id);

    Role validateRoleForUpdate(Long id);

    // ---- Additional methods from RoleApplicationService ----

    Long createRole(RoleSaveReqVO createReqVO, Integer type);

    void updateRole(RoleSaveReqVO updateReqVO);

    void deleteRoleList(List<Long> ids);

    RoleDO getRoleDO(Long id);

    RoleDO getRoleFromCache(Long id);

    List<RoleDO> getRoleListByStatus(Collection<Integer> statuses);

    List<RoleDO> getRoleList();

    List<RoleDO> getRoleList(Collection<Long> ids);

    List<RoleDO> getRoleListFromCache(Collection<Long> ids);

    PageResult<RoleDO> getRolePage(RolePageReqVO reqVO);

    boolean hasAnySuperAdmin(Collection<Long> ids);

    void validateRoleList(Collection<Long> ids);
}
