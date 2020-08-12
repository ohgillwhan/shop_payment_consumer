package kr.sooragenius.shop.item.event.impl;

import kr.sooragenius.shop.order.event.ItemOrderEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ItemOrderEventListener {

    @EventListener
    public void itemOrderEvent(ItemOrderEvent itemOrderEvent) {
        System.out.println(itemOrderEvent);
    }
}
