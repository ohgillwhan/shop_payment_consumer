package kr.sooragenius.shop.item.event.impl;

import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.service.ItemService;
import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import kr.sooragenius.shop.order.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemOrderEventListener {
    private final ItemService itemService;
    @EventListener
    public void itemOrderEvent(ItemOrderEventDTO.NewItemOrder newItemOrder) {
        ItemDTO.StockUpdate stockUpdate = new ItemDTO.StockUpdate(newItemOrder.getItemId(), - newItemOrder.getStockCount());
        itemService.minusStockById(stockUpdate);
    }
}
