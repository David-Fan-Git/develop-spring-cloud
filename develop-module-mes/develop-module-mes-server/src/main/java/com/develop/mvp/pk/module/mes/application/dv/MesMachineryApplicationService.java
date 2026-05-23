package com.develop.mvp.pk.module.mes.application.dv;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.mes.domain.dv.MesMachinery;
import com.develop.mvp.pk.module.mes.domain.dv.repository.MesMachineryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class MesMachineryApplicationService {
    private final MesMachineryRepository repo;
    @Transactional public Long create(String name, String code, String type, Integer status) { var m = MesMachinery.of(null, name).code(code).type(type).status(status); repo.save(m); return m.id(); }
    @Transactional public void update(Long id, String name, String code, String type, Integer status) { repo.save(MesMachinery.of(id, name).code(code).type(type).status(status)); }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public MesMachinery get(Long id) { return repo.findById(id); }
    public List<MesMachinery> getList() { return repo.findAll(); }
    public PageResult<MesMachinery> getPage(String name, String type, Integer status, Integer pageNo, Integer pageSize) { return repo.findPage(name, type, status, pageNo, pageSize); }
}
