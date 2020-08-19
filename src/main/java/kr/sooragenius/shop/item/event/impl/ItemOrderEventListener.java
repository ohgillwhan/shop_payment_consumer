package kr.sooragenius.shop.item.event.impl;

import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.ItemOptionService;
import kr.sooragenius.shop.item.service.ItemService;
import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import kr.sooragenius.shop.order.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemOrderEventListener {
    private final ItemOptionService itemOptionService;
    @EventListener
    public void itemOrderEvent(ItemOrderEventDTO.NewItemOrder newItemOrder) {
        ItemOptionDTO.StockUpdate stockUpdate = new ItemOptionDTO.StockUpdate(newItemOrder.getOptionId(), newItemOrder.getStock());
        itemOptionService.minusStockById(stockUpdate);
    }

    @EventListener
    public void cancelEvent(ItemOrderEventDTO.ItemCancel itemCancel) {
        ItemOptionDTO.StockUpdate stockUpdate = new ItemOptionDTO.StockUpdate(itemCancel.getOptionId(), itemCancel.getStock());
        itemOptionService.plusStockById(stockUpdate);
    }
}
