package kr.sooragenius.shop;

import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import kr.sooragenius.shop.order.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ShopApplication {
    private final ItemOrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

    @KafkaListener(groupId = "ShopPaymentGroup", topics = {"ItemOrderTopic2"} )
    public void paymentListener(ItemOrderEventDTO.NewItemOrder order) throws InterruptedException {
        orderService.pay(order);
    }
}
