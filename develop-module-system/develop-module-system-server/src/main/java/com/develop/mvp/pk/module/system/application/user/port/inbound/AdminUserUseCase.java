package com.develop.mvp.pk.module.system.application.user.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.auth.vo.AuthRegisterReqVO;
import com.develop.mvp.pk.module.system.controller.admin.user.vo.profile.UserProfileUpdatePasswordReqVO;
import com.develop.mvp.pk.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import com.develop.mvp.pk.module.system.controller.admin.user.vo.user.UserImportExcelVO;
import com.develop.mvp.pk.module.system.controller.admin.user.vo.user.UserImportRespVO;
import com.develop.mvp.pk.module.system.controller.admin.user.vo.user.UserPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.user.vo.user.UserSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.user.AdminUserDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * admin user use-case boundary for legacy service compatibility and future adapters.
 */
public interface AdminUserUseCase {

    Long createUser(UserSaveReqVO createReqVO);

    Long registerUser(AuthRegisterReqVO registerReqVO);

    void updateUser(UserSaveReqVO updateReqVO);

    void updateUserLogin(Long id, String loginIp);

    void updateUserProfile(Long id, UserProfileUpdateReqVO reqVO);

    void updateUserPassword(Long id, UserProfileUpdatePasswordReqVO reqVO);

    void updateUserPassword(Long id, String password);

    void updateUserStatus(Long id, Integer status);

    void deleteUser(Long id);

    void deleteUserList(List<Long> ids);

    AdminUserDO getUserByUsername(String username);

    AdminUserDO getUserByMobile(String mobile);

    PageResult<AdminUserDO> getUserPage(UserPageReqVO reqVO);

    AdminUserDO getUser(Long id);

    List<AdminUserDO> getUserListByDeptIds(Collection<Long> deptIds);

    List<AdminUserDO> getUserListByPostIds(Collection<Long> postIds);

    List<AdminUserDO> getUserList(Collection<Long> ids);

    Map<Long, AdminUserDO> getUserMap(Collection<Long> ids);

    void validateUserList(Collection<Long> ids);

    List<AdminUserDO> getUserListByNickname(String nickname);

    List<AdminUserDO> getUserListByStatus(Integer status);

    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    UserImportRespVO importUserList(List<UserImportExcelVO> importUsers, boolean isUpdateSupport);

    AdminUserDO validateUserExists(Long id);

    void validateUsernameUnique(Long id, String username);

    void validateEmailUnique(Long id, String email);

    void validateMobileUnique(Long id, String mobile);

    void validateOldPassword(Long id, String oldPassword);
}
