package kr.sooragenius.shop.order.event;

import kr.sooragenius.shop.order.ItemOrder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ItemOrderEvent {
    private final ItemOrder itemOrder;
}
