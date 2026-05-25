package com.develop.mvp.pk.module.system.api.dept;

import com.develop.mvp.pk.module.system.application.dept.port.inbound.DeptUseCase;
import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.system.api.dept.dto.PostRespDTO;
import com.develop.mvp.pk.module.system.dal.dataobject.dept.PostDO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

import static com.develop.mvp.pk.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class PostApiImpl implements PostApi {

    @Resource
    private DeptUseCase postUseCase;

    @Override
    public CommonResult<Boolean> validPostList(Collection<Long> ids) {
        postUseCase.validatePostList(ids);
        return success(true);
    }

    @Override
    public CommonResult<List<PostRespDTO>> getPostList(Collection<Long> ids) {
        List<PostDO> list = postUseCase.getPostList(ids);
        return success(BeanUtils.toBean(list, PostRespDTO.class));
    }

}
