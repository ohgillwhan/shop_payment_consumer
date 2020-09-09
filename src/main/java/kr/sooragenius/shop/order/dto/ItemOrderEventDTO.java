package kr.sooragenius.shop.order.dto;

import kr.sooragenius.shop.order.ItemOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

public class ItemOrderEventDTO {
    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NewItemOrder implements Serializable {
        private long orderId;

        public static NewItemOrder of(ItemOrder itemOrder) {
            NewItemOrder newItemOrder = new NewItemOrder();
            newItemOrder.orderId = itemOrder.getId();

            return newItemOrder;
        }
    }

}
