package com.develop.mvp.pk.module.iot.application.device;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.iot.domain.device.IotDevice;
import com.develop.mvp.pk.module.iot.domain.device.repository.IotDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class IotDeviceApplicationService {
    private final IotDeviceRepository repo;
    @Transactional public Long create(String name, String deviceKey, Long productId, Integer status) { var d = IotDevice.of(null, name).deviceKey(deviceKey).productId(productId).status(status); repo.save(d); return d.id(); }
    @Transactional public void update(Long id, String name, Integer status) { var d = repo.findById(id); if (d != null) { d.deviceKey(d.deviceKey()).status(status); repo.save(d); } }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public IotDevice get(Long id) { return repo.findById(id); }
    public List<IotDevice> getByProductId(Long productId) { return repo.findByProductId(productId); }
    public PageResult<IotDevice> getPage(String name, Long productId, Integer status, Integer pageNo, Integer pageSize) { return repo.findPage(name, productId, status, pageNo, pageSize); }
}
