package com.develop.mvp.pk.module.iot.domain.device.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.iot.domain.device.IotDevice;
import java.util.*;
public interface IotDeviceRepository {
    IotDevice save(IotDevice d); void delete(Long id);
    IotDevice findById(Long id); Optional<IotDevice> findByDeviceKey(String key);
    List<IotDevice> findByProductId(Long productId);
    PageResult<IotDevice> findPage(String name, Long productId, Integer status, Integer pageNo, Integer pageSize);
}
