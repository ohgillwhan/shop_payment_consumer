package kr.sooragenius.shop.order.dto;

import kr.sooragenius.shop.order.ItemOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

public class ItemOrderEventDTO {
    @Data
    @Builder @NoArgsConstructor @AllArgsConstructor
    public static class NewItemOrder {
        private long itemId;
        private long optionId;
        private long stockCount;

        public static Object of(ItemOrderDetailDTO.Request detailRequest) {
            NewItemOrder newItemOrder = new NewItemOrder();
            newItemOrder.itemId = detailRequest.getItemId();
            newItemOrder.optionId = detailRequest.getOptionId();
            newItemOrder.stockCount = 1;

            return newItemOrder;
        }
    }
}
