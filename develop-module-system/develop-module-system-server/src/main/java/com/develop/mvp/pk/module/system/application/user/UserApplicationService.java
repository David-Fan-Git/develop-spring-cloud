package com.develop.mvp.pk.module.system.application.user;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.framework.datapermission.core.util.DataPermissionUtils;
import com.develop.mvp.pk.module.system.domain.user.User;
import com.develop.mvp.pk.module.system.domain.user.UserFactory;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEventPublisher;
import com.develop.mvp.pk.module.system.domain.user.repository.UserPageQuery;
import com.develop.mvp.pk.module.system.domain.user.repository.UserRepository;
import com.develop.mvp.pk.module.system.domain.user.service.PasswordEncoder;
import com.develop.mvp.pk.module.system.domain.user.service.UserUniquenessChecker;
import com.develop.mvp.pk.module.system.domain.user.valueobject.*;
import com.develop.mvp.pk.module.system.service.dept.DeptService;
import com.develop.mvp.pk.module.system.service.dept.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
public class UserApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserUniquenessChecker uniquenessChecker;
    private final DomainEventPublisher eventPublisher;
    private final DeptService deptService;
    private final PostService postService;

    public UserApplicationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                   UserUniquenessChecker uniquenessChecker,
                                   DomainEventPublisher eventPublisher,
                                   DeptService deptService, PostService postService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.uniquenessChecker = uniquenessChecker;
        this.eventPublisher = eventPublisher;
        this.deptService = deptService;
        this.postService = postService;
    }

    @Transactional
    public Long createUser(Long id, String username, String rawPassword, Long tenantId,
                            Long deptId, String email, String mobile,
                            String nickname, String avatar, Integer sex, String remark,
                            Set<Long> postIds) {
        validateDeptAndPosts(deptId, postIds);
        assertUsernameUnique(Username.of(username), null);
        if (email != null) assertEmailUnique(Email.of(email), null);
        if (mobile != null) assertMobileUnique(Mobile.of(mobile), null);

        EncodedPassword encoded = passwordEncoder.encode(RawPassword.of(rawPassword));
        User user = UserFactory.create(id, username, encoded, tenantId, deptId,
                email, mobile, nickname, avatar, sex, remark, postIds);
        userRepository.save(user);
        publishEvents(user);
        return id;
    }

    @Transactional
    public void updateUser(Long id, String username, String email, String mobile,
                            String nickname, String avatar, Integer sex, String remark,
                            Long deptId, Set<Long> postIds) {
        User user = findExistingUser(id);
        DataPermissionUtils.executeIgnore(() -> {
            assertUsernameUnique(Username.of(username), UserId.of(id));
            if (email != null) assertEmailUnique(Email.of(email), UserId.of(id));
            if (mobile != null) assertMobileUnique(Mobile.of(mobile), UserId.of(id));
            return null;
        });
        validateDeptAndPosts(deptId, postIds);
        user.updateContact(Email.of(email), Mobile.of(mobile), uniquenessChecker);
        user.updateProfile(UserProfile.of(nickname, avatar, sex, remark));
        user.syncPosts(postIds);
        userRepository.save(user);
        publishEvents(user);
    }

    @Transactional
    public void updateUserStatus(Long id, Integer statusCode) {
        User user = findExistingUser(id);
        UserStatus newStatus = UserStatus.of(statusCode);
        if (newStatus.isDisabled()) {
            user.disable();
        } else {
            user.enable();
        }
        userRepository.save(user);
        publishEvents(user);
    }

    @Transactional
    public void changePassword(Long id, String oldRawPassword, String newRawPassword) {
        User user = findExistingUser(id);
        user.changePassword(RawPassword.of(oldRawPassword),
                RawPassword.of(newRawPassword), passwordEncoder);
        userRepository.save(user);
        publishEvents(user);
    }

    @Transactional
    public void resetPassword(Long id, String newRawPassword) {
        User user = findExistingUser(id);
        user.resetPassword(RawPassword.of(newRawPassword), passwordEncoder);
        userRepository.save(user);
        publishEvents(user);
    }

    @Transactional
    public void updateProfile(Long id, String email, String mobile,
                               String nickname, String avatar, Integer sex, String remark) {
        User user = findExistingUser(id);
        if (email != null) assertEmailUnique(Email.of(email), UserId.of(id));
        if (mobile != null) assertMobileUnique(Mobile.of(mobile), UserId.of(id));
        user.updateContact(Email.of(email), Mobile.of(mobile), uniquenessChecker);
        user.updateProfile(UserProfile.of(nickname, avatar, sex, remark));
        userRepository.save(user);
        publishEvents(user);
    }

    @Transactional
    public void recordLogin(Long id, String loginIp) {
        User user = findExistingUser(id);
        user.recordLogin(LoginRecord.of(loginIp));
        userRepository.save(user);
        publishEvents(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findExistingUser(id);
        user.markDeleted();
        userRepository.delete(user.id());
        publishEvents(user);
    }

    @Transactional
    public void deleteUserList(List<Long> ids) {
        for (Long id : ids) {
            User user = findExistingUser(id);
            user.markDeleted();
            userRepository.delete(user.id());
            publishEvents(user);
        }
    }

    // ── 查询 ──

    public User getUser(Long id) {
        return userRepository.findById(UserId.of(id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(Username.of(username)).orElse(null);
    }

    public PageResult<User> getUserPage(UserPageQuery query) {
        return userRepository.findPage(query);
    }

    public List<User> getUserList(Collection<Long> ids) {
        return userRepository.findByIds(ids.stream().map(UserId::of).collect(Collectors.toList()));
    }

    public List<User> getUserListByDeptIds(Collection<Long> deptIds) {
        return userRepository.findByDeptIds(deptIds);
    }

    public List<User> getUserListByPostIds(Collection<Long> postIds) {
        return userRepository.findByPostIds(postIds);
    }

    public List<User> getUserListByStatus(Integer status) {
        return userRepository.findByStatus(UserStatus.of(status));
    }

    public List<User> getUserListByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public void validateUserList(Collection<Long> ids) {
        List<User> users = userRepository.findByIds(
                ids.stream().map(UserId::of).collect(Collectors.toList()));
        Map<Long, User> userMap = CollectionUtils.convertMap(users, u -> u.id().value());
        for (Long id : ids) {
            User user = userMap.get(id);
            if (user == null) throw exception(USER_NOT_EXISTS);
            if (user.isDisabled()) throw exception(USER_IS_DISABLE, user.profile().nickname());
        }
    }

    /** 获取部门范围条件 */
    public Set<Long> getDeptCondition(Long deptId) {
        if (deptId == null) return Collections.emptySet();
        Set<Long> deptIds = CollectionUtils.convertSet(
                deptService.getChildDeptList(deptId),
                d -> d.getId());
        deptIds.add(deptId);
        return deptIds;
    }

    private User findExistingUser(Long id) {
        User user = userRepository.findById(UserId.of(id));
        if (user == null) throw exception(USER_NOT_EXISTS);
        return user;
    }

    private void validateDeptAndPosts(Long deptId, Set<Long> postIds) {
        deptService.validateDeptList(CollectionUtils.singleton(deptId));
        postService.validatePostList(postIds);
    }

    private void assertUsernameUnique(Username username, UserId excludeId) {
        Optional<User> existing = userRepository.findByUsername(username);
        if (existing.isPresent() && (excludeId == null || !existing.get().id().equals(excludeId))) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    private void assertEmailUnique(Email email, UserId excludeId) {
        if (!email.isPresent()) return;
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent() && (excludeId == null || !existing.get().id().equals(excludeId))) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    private void assertMobileUnique(Mobile mobile, UserId excludeId) {
        if (!mobile.isPresent()) return;
        Optional<User> existing = userRepository.findByMobile(mobile);
        if (existing.isPresent() && (excludeId == null || !existing.get().id().equals(excludeId))) {
            throw exception(USER_MOBILE_EXISTS);
        }
    }

    private void publishEvents(User user) {
        for (DomainEvent event : user.pullEvents()) {
            eventPublisher.publish(event);
        }
    }
}
