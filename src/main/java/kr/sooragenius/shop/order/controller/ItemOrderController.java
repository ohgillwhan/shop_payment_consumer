package kr.sooragenius.shop.order.controller;

import kr.sooragenius.shop.order.dto.ItemOrderDTO;
import kr.sooragenius.shop.order.service.ItemOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class ItemOrderController {
    private final ItemOrderService itemOrderService;
    @PostMapping(value = {"", "/"})
    public String order(@RequestBody ItemOrderDTO.Request request) {
        try {
            ItemOrderDTO.Response order = itemOrderService.order(request);
        }catch(Exception ex) {
            ex.printStackTrace();
            return "fail";
        }

        return "success";
    }
}
