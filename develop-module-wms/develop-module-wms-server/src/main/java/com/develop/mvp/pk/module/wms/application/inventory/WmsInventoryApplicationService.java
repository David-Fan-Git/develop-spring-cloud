package com.develop.mvp.pk.module.wms.application.inventory;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.wms.domain.inventory.WmsInventory;
import com.develop.mvp.pk.module.wms.domain.inventory.repository.WmsInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class WmsInventoryApplicationService {
    private final WmsInventoryRepository repo;
    @Transactional public Long create(Long itemId, Long warehouseId, Integer stock) { var i = WmsInventory.of(null, itemId, warehouseId).stock(stock); repo.save(i); return i.id(); }
    @Transactional public void updateStock(Long id, Integer stock) { var i = repo.findById(id); if (i != null) { i.stock(stock); repo.save(i); } }
    public WmsInventory get(Long id) { return repo.findById(id); }
    public Optional<WmsInventory> getByItemAndWarehouse(Long itemId, Long warehouseId) { return repo.findByItemIdAndWarehouseId(itemId, warehouseId); }
    public PageResult<WmsInventory> getPage(Long warehouseId, Long itemId, Integer pageNo, Integer pageSize) { return repo.findPage(warehouseId, itemId, pageNo, pageSize); }
}
