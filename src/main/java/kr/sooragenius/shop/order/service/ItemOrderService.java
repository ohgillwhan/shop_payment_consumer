package kr.sooragenius.shop.order.service;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.service.infra.ItemOptionRepository;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.service.MemberService;
import kr.sooragenius.shop.member.service.infra.MemberRepository;
import kr.sooragenius.shop.order.ItemOrder;
import kr.sooragenius.shop.order.ItemOrderDetail;
import kr.sooragenius.shop.order.dto.ItemOrderDTO;
import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import kr.sooragenius.shop.order.dto.ItemOrderEventDTO;
import kr.sooragenius.shop.order.service.infra.ItemOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemOrderService {
    private final ItemRepository itemRepository;
    private final ItemOrderRepository itemOrderRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ItemOrderDTO.Response order(ItemOrderDTO.Request request) {
        Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 계정입니다"));

        List<ItemOrderDetailDTO.Request> orderDetailRequests = request.getOrderDetailRequests();
        if(orderDetailRequests == null || orderDetailRequests.isEmpty()) throw new RuntimeException("구매할 물품이 없습니다");

        ItemOrder itemOrder = ItemOrder.of(member);

        for(ItemOrderDetailDTO.Request detailRequest : request.getOrderDetailRequests()) {

            Item item = itemRepository.findById(detailRequest.getItemId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다"));

            ItemOption itemOption = null;
            Long optionId = detailRequest.getOptionId();
            if (optionId != null && optionId > 0L) {
                itemOption = itemOptionRepository.findById(optionId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));
            }

            applicationEventPublisher.publishEvent(ItemOrderEventDTO.NewItemOrder.of(detailRequest));

            itemOrder.addOrderDetails(item, itemOption, detailRequest.getOrderStatus(), detailRequest);
        };

        itemOrder = itemOrderRepository.save(itemOrder);

        return ItemOrderDTO.Response.of(itemOrder, itemOrder.getItemOrderDetails());
    }

    @Transactional
    public ItemOrderDetailDTO.Response cancelDetail(ItemOrderDetailDTO.RequestCancel request) {
        ItemOrder itemOrder = itemOrderRepository.findById(request.getOrderId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다"));

        ItemOrderDetailDTO.Response cancelResponse = itemOrder.cancelOrderDetail(request.getDetailId());

        applicationEventPublisher.publishEvent(ItemOrderEventDTO.ItemCancel.of(cancelResponse));


        return cancelResponse;
    }
}
