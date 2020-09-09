package kr.sooragenius.shop.order.service;

import kr.sooragenius.shop.order.ItemOrder;
import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import kr.sooragenius.shop.order.enums.OrderStatus;
import kr.sooragenius.shop.order.service.infra.ItemOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ItemOrderService {
    private final ItemOrderRepository itemOrderRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void pay(ItemOrderEventDTO.NewItemOrder order) throws InterruptedException {
        System.out.println(order.getOrderId() + Thread.currentThread().getName());
        OrderStatus status[] = {OrderStatus.COMPLETE, OrderStatus.CANCEL};

        OrderStatus nextStatus = status[new Random().nextInt(2)];
        ItemOrder itemOrder = itemOrderRepository.findById(order.getOrderId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        Thread.sleep(new Random().nextInt(10000));
        itemOrder.updateOrderStatus(nextStatus);

    }
}
