package com.develop.mvp.pk.module.wms.domain.inventory.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.wms.domain.inventory.WmsInventory;
import java.util.*;
public interface WmsInventoryRepository {
    WmsInventory save(WmsInventory i); WmsInventory findById(Long id);
    List<WmsInventory> findByWarehouseId(Long warehouseId); Optional<WmsInventory> findByItemIdAndWarehouseId(Long itemId, Long warehouseId);
    PageResult<WmsInventory> findPage(Long warehouseId, Long itemId, Integer pageNo, Integer pageSize);
}
