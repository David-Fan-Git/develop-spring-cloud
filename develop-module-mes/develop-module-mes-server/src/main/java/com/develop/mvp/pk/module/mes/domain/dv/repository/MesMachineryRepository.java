package com.develop.mvp.pk.module.mes.domain.dv.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.mes.domain.dv.MesMachinery;
import java.util.*;
public interface MesMachineryRepository {
    MesMachinery save(MesMachinery m); void delete(Long id);
    MesMachinery findById(Long id); Optional<MesMachinery> findByCode(String code);
    List<MesMachinery> findAll(); PageResult<MesMachinery> findPage(String name, String type, Integer status, Integer pageNo, Integer pageSize);
}
