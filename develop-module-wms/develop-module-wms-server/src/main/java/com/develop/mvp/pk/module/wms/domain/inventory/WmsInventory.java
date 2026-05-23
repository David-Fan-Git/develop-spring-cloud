package com.develop.mvp.pk.module.wms.domain.inventory;
import java.util.Objects;
public final class WmsInventory { private final Long id; private final Long itemId; private final Long warehouseId; private Integer stock, availableStock;
    public WmsInventory(Long id, Long itemId, Long warehouseId) { this.id = id; this.itemId = Objects.requireNonNull(itemId); this.warehouseId = Objects.requireNonNull(warehouseId); }
    public static WmsInventory of(Long id, Long itemId, Long warehouseId) { return new WmsInventory(id, itemId, warehouseId); }
    public Long id() { return id; } public Long itemId() { return itemId; } public Long warehouseId() { return warehouseId; }
    public Integer stock() { return stock; } public Integer availableStock() { return availableStock; }
    public WmsInventory stock(Integer v) { stock = v; return this; } public WmsInventory availableStock(Integer v) { availableStock = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof WmsInventory i && id.equals(i.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
