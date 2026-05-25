package com.develop.mvp.pk.module.system.application.user.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.user.User;
import com.develop.mvp.pk.module.system.domain.user.repository.UserPageQuery;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * user aggregate use-case boundary for legacy entries and new adapters.
 */
public interface UserUseCase {

    Long createUser(Long id, String username, String rawPassword, Long tenantId,
                    Long deptId, String email, String mobile,
                    String nickname, String avatar, Integer sex, String remark,
                    Set<Long> postIds);

    void updateUser(Long id, String username, String email, String mobile,
                    String nickname, String avatar, Integer sex, String remark,
                    Long deptId, Set<Long> postIds);

    void updateUserStatus(Long id, Integer statusCode);

    void changePassword(Long id, String oldRawPassword, String newRawPassword);

    void resetPassword(Long id, String newRawPassword);

    void updateProfile(Long id, String email, String mobile,
                       String nickname, String avatar, Integer sex, String remark);

    void recordLogin(Long id, String loginIp);

    void deleteUser(Long id);

    void deleteUserList(List<Long> ids);

    User getUser(Long id);

    User getUserByUsername(String username);

    PageResult<User> getUserPage(UserPageQuery query);

    List<User> getUserList(Collection<Long> ids);

    List<User> getUserListByDeptIds(Collection<Long> deptIds);

    List<User> getUserListByPostIds(Collection<Long> postIds);

    List<User> getUserListByStatus(Integer status);

    List<User> getUserListByNickname(String nickname);

    void validateUserList(Collection<Long> ids);

    Set<Long> getDeptCondition(Long deptId);
}
