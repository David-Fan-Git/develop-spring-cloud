package com.develop.mvp.pk.module.ai.application.model;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.ai.domain.model.AiModel;
import com.develop.mvp.pk.module.ai.domain.model.repository.AiModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class AiModelApplicationService {
    private final AiModelRepository repo;
    @Transactional public Long create(String name, String platform, String apiKey, String type, Integer status) { var m = AiModel.of(null, name).platform(platform).apiKey(apiKey).type(type).status(status); repo.save(m); return m.id(); }
    @Transactional public void update(Long id, String name, String apiKey, Integer status) { var m = repo.findById(id); if (m != null) { m.apiKey(apiKey).status(status); repo.save(m); } }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public AiModel get(Long id) { return repo.findById(id); }
    public List<AiModel> getList() { return repo.findAll(); }
    public PageResult<AiModel> getPage(String name, String platform, String type, Integer status, Integer pageNo, Integer pageSize) { return repo.findPage(name, platform, type, status, pageNo, pageSize); }
}
