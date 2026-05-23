package com.develop.mvp.pk.module.ai.domain.model.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.ai.domain.model.AiModel;
import java.util.*;
public interface AiModelRepository {
    AiModel save(AiModel m); void delete(Long id);
    AiModel findById(Long id); List<AiModel> findAll();
    PageResult<AiModel> findPage(String name, String platform, String type, Integer status, Integer pageNo, Integer pageSize);
}
