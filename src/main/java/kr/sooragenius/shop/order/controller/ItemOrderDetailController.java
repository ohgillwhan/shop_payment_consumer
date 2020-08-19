package kr.sooragenius.shop.order.controller;

import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import kr.sooragenius.shop.order.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-detail")
public class ItemOrderDetailController {
    private final ItemOrderService itemOrderService;
    @PostMapping("/cancel")
    public ItemOrderDetailDTO.Response cancel(@RequestBody ItemOrderDetailDTO.RequestCancel requestCancel) {
        return itemOrderService.cancelDetail(requestCancel);
    }
}
