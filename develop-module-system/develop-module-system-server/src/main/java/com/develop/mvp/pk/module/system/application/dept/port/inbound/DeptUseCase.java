package com.develop.mvp.pk.module.system.application.dept.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dept.DeptDO;
import com.develop.mvp.pk.module.system.dal.dataobject.dept.PostDO;
import com.develop.mvp.pk.module.system.domain.dept.Dept;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * dept aggregate use-case boundary for legacy entries and new adapters.
 */
public interface DeptUseCase {

    Long createDept(DeptSaveReqVO createReqVO);

    void updateDept(DeptSaveReqVO updateReqVO);

    void deleteDept(Long id);

    void deleteDeptList(List<Long> ids);

    DeptDO getDept(Long id);

    List<DeptDO> getDeptList(Collection<Long> ids);

    List<DeptDO> getDeptList(DeptListReqVO reqVO);

    Map<Long, DeptDO> getDeptMap(Collection<Long> ids);

    List<DeptDO> getChildDeptList(Long id);

    List<DeptDO> getChildDeptList(Collection<Long> ids);

    List<DeptDO> getDeptListByLeaderUserId(Long id);

    Set<Long> getChildDeptIdListFromCache(Long id);

    void validateDeptList(Collection<Long> ids);

    Long createPost(PostSaveReqVO createReqVO);

    void updatePost(PostSaveReqVO updateReqVO);

    void deletePost(Long id);

    void deletePostList(List<Long> ids);

    List<PostDO> getPostList(Collection<Long> ids);

    List<PostDO> getPostList(Collection<Long> ids, Collection<Integer> statuses);

    PageResult<PostDO> getPostPage(PostPageReqVO reqVO);

    PostDO getPost(Long id);

    void validatePostList(Collection<Long> ids);

    Long createDeptDomain(Long id, String name, Long parentId, Integer sort,
                          Long leaderUserId, String phone, String email);

    void updateDeptDomain(Long id, String name, Long parentId, Integer sort,
                          Long leaderUserId, String phone, String email);

    void deleteDeptDomain(Long id);

    Dept getDeptDomain(Long id);

    List<Dept> getDeptDomainList(Collection<Long> ids);

    List<Dept> getAllDeptDomains();

    List<Dept> getChildDeptDomainList(Long id);

    Set<Long> getChildDeptDomainIdsFromCache(Long id);
}
