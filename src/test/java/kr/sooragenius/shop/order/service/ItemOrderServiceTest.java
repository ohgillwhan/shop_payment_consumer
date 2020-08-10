package kr.sooragenius.shop.order.service;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.infra.ItemOptionRepository;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import kr.sooragenius.shop.member.service.infra.MemberRepository;
import kr.sooragenius.shop.order.ItemOrder;
import kr.sooragenius.shop.order.dto.ItemOrderDTO;
import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import kr.sooragenius.shop.order.service.infra.ItemOrderRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemOrderServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemOrderRepository itemOrderRepository;
    @Mock
    private ItemOptionRepository itemOptionRepository;
    @Mock
    private MemberRepository memberRepository;

    private ItemOrderService itemOrderService;
    private final PasswordEncoder passwordEncoder;

    @BeforeEach
    public void test() {
        itemOrderService = new ItemOrderService(itemRepository, itemOrderRepository, itemOptionRepository, memberRepository);
    }
    @Test
    @DisplayName("order - 계정 없을시 에러")
    void orderMemberError() {
        // given
        Item blackKakao = createItem(1L, "blackKakao", 1000L, 100L);
        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(null).build()
                ))
                .build();
        // when
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao));

        // then
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> itemOrderService.order(request));
        assertTrue(illegalArgumentException.getMessage().contains("존재하지 않는 계정입니다"));
    }
    @Test
    @DisplayName("order - 구매할 물품이 없을시 에러")
    void orderNoOrderDetailsError() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .build();
        // when
        when(memberRepository.findById(memberRequest.getId()))
                .thenReturn(Optional.of(Member.of(memberRequest, passwordEncoder)));

        // then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> itemOrderService.order(request));
        assertTrue(runtimeException.getMessage().contains("구매할 물품이 없습니다"));
    }
    @Test
    @DisplayName("order - 없는 물건 구매하려고 할 경우")
    void orderNotExistItemError() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(null).build()
                ))
                .build();
        // when
        when(memberRepository.findById(memberRequest.getId()))
                .thenReturn(Optional.of(Member.of(memberRequest, passwordEncoder)));

        // then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> itemOrderService.order(request));
        assertTrue(runtimeException.getMessage().contains("존재하지 않는 상품입니다"));
    }
    @Test
    @DisplayName("order - 없는 옵션으로 구매하려고 할 경우")
    void orderNotExistOptionError() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();

        Item blackKakao = createItem(1L, "blackKakao", 1000L, 100L);

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(1L).build()
                ))
                .build();
        // when
        when(memberRepository.findById(memberRequest.getId()))
                .thenReturn(Optional.of(Member.of(memberRequest, passwordEncoder)));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao));

        // then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> itemOrderService.order(request));
        assertTrue(runtimeException.getMessage().contains("존재하지 않는 옵션입니다"));
    }
    @Test
    @DisplayName("order - 옵션없이")
    void orderWithoutOption() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();
        Item blackKakao = createItem(1L, "blackKakao", 1000L, 100L);
        Item whiteKakao = createItem(2L, "whiteKakao", 5000L, 333L);
        Item pinkKakao = createItem(3L, "pinkKakao", 4000L, 250L);


        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(null).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(2L).optionId(null).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(3L).optionId(null).build()
                ))
                .build();

        // when
        when(memberRepository.findById(memberRequest.getId()))
                .thenReturn(Optional.of(Member.of(memberRequest, passwordEncoder)));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao));
        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(whiteKakao));
        when(itemRepository.findById(3L))
                .thenReturn(Optional.of(pinkKakao));

        ItemOrderDTO.Response order = itemOrderService.order(request);

        // then
        assertEquals(blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount(), order.getTotalPayAmount());
        assertEquals(blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount(), order.getTotalAmount());
        assertEquals(blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount(), order.getTotalDiscountAmount());
        assertFalse(order.getOrderDetails().isEmpty());
        assertEquals(request.getOrderDetailRequests().size(), order.getOrderDetails().size());
        order.getOrderDetails().stream().forEach(detail -> {
            Item item = itemRepository.findById(detail.getItemId()).get();

            assertEquals(item.getDiscountAmount(), detail.getDiscountAmount());
            assertEquals(item.getPayAmount(), detail.getPayAmount());
            assertEquals(item.getAmount(), detail.getAmount());
        });
    }
    @Test
    @DisplayName("order - 옵션 추가하여")
    void orderWithOption() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();
        Item blackKakao = createItem(1L, "blackKakao", 1000L, 100L);
        Item whiteKakao = createItem(2L, "whiteKakao", 5000L, 333L);
        Item pinkKakao = createItem(3L, "pinkKakao", 4000L, 250L);

        ItemOption blackKakaoOption = createItemOption(blackKakao, 1L,"두배로!", 500L);

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(1L).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(2L).optionId(null).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(3L).optionId(null).build()
                ))
                .build();

        // when
        when(memberRepository.findById(memberRequest.getId()))
                .thenReturn(Optional.of(Member.of(memberRequest, passwordEncoder)));
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao));
        when(itemRepository.findById(2L))
                .thenReturn(Optional.of(whiteKakao));
        when(itemRepository.findById(3L))
                .thenReturn(Optional.of(pinkKakao));
        when(itemOptionRepository.findById(1L))
                .thenReturn(Optional.of(blackKakaoOption));

        ItemOrderDTO.Response order = itemOrderService.order(request);

        // then
        long itemTotalPayAmount = blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount();
        long itemTotalAmount = blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount();
        long itemTotalDiscount = blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount();

        assertEquals(itemTotalPayAmount + blackKakaoOption.getPremium()
                , order.getTotalPayAmount());
        assertEquals(itemTotalAmount + blackKakaoOption.getPremium()
                , order.getTotalAmount());
        assertEquals(itemTotalDiscount
                , order.getTotalDiscountAmount());
        assertFalse(order.getOrderDetails().isEmpty());
        assertEquals(request.getOrderDetailRequests().size(), order.getOrderDetails().size());

        order.getOrderDetails().stream().forEach(detail -> {
            Item item = itemRepository.findById(detail.getItemId()).get();
            long discountAmount = item.getDiscountAmount();
            long payAmount = item.getPayAmount();
            long amount = item.getAmount();
            if(detail.getOptionId() != null && detail.getOptionId() > 0L) {
                ItemOption itemOption = itemOptionRepository.findById(detail.getOptionId()).get();

                payAmount += itemOption.getPremium();
                amount += itemOption.getPremium();
            }

            assertEquals(discountAmount, detail.getDiscountAmount());
            assertEquals(payAmount, detail.getPayAmount());
            assertEquals(amount, detail.getAmount());
        });
    }

    @Test
    @DisplayName("cancel - 존재하지 않는 상세정")
    @Transactional
    public void cancelNotExistsDetails() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();

        ItemOrderDetailDTO.RequestCancel requestCancel = ItemOrderDetailDTO.RequestCancel.builder()
                .detailId(1L)
                .orderId(1L)
                .build();
        Member member = Member.of(memberRequest, passwordEncoder);
        // when
        when(itemOrderRepository.findById(1L))
                .thenReturn(Optional.of(ItemOrder.of(member)));

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> itemOrderService.cancelDetail(requestCancel));
        assertTrue(illegalArgumentException.getMessage().contains("존재하지 않는 상세정보 입니다"));
    }

    @Test
    @DisplayName("cancel - 존재하지 않는 주문")
    @Transactional
    public void cancelNotExistsOrder() {
        ItemOrderDetailDTO.RequestCancel requestCancel = ItemOrderDetailDTO.RequestCancel.builder()
                .detailId(1L)
                .orderId(1L)
                .build();

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> itemOrderService.cancelDetail(requestCancel));
        assertTrue(illegalArgumentException.getMessage().contains("존재하지 않는 주문입니다"));
    }

    @Test
    @DisplayName("cancel")
    @Transactional
    public void cancel() {
        // given
        ItemOrder itemOrder = ItemOrder.of(Member.of(createMemberRequest(), passwordEncoder));
        Item blackKakao = createItem(1L, "blackKakao", 1000L, 100L);
        Item whiteKakao = createItem(2L, "whiteKakao", 5000L, 333L);
        Item pinkKakao = createItem(3L, "pinkKakao", 4000L, 250L);

        // when
        itemOrder.addOrderDetails(blackKakao, null);
        itemOrder.addOrderDetails(whiteKakao, null);
        itemOrder.addOrderDetails(pinkKakao, null);

        ReflectionTestUtils.setField(itemOrder.getItemOrderDetails().get(0), "id", 1L);
        ReflectionTestUtils.setField(itemOrder.getItemOrderDetails().get(1), "id", 2L);
        ReflectionTestUtils.setField(itemOrder.getItemOrderDetails().get(2), "id", 3L);

        ItemOrderDetailDTO.Response response = itemOrder.cancelOrderDetail(2L);
        // then
        long itemTotalPayAmount = blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount();
        long itemTotalAmount = blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount();
        long itemTotalDiscount = blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount();

        assertEquals(blackKakao, itemOrder.getItemOrderDetails().get(0).getItem());
        assertEquals(pinkKakao, itemOrder.getItemOrderDetails().get(1).getItem());
        assertEquals(whiteKakao.getPayAmount(), response.getPayAmount());
        assertEquals(whiteKakao.getAmount(), response.getAmount());
        assertEquals(whiteKakao.getDiscountAmount(), response.getDiscountAmount());
        assertEquals(itemTotalPayAmount - whiteKakao.getPayAmount(), itemOrder.getTotalPayAmount());
        assertEquals(itemTotalAmount - whiteKakao.getAmount(), itemOrder.getTotalAmount());
        assertEquals(itemTotalDiscount - whiteKakao.getDiscountAmount(), itemOrder.getTotalDiscountAmount());
    }
    private MemberDTO.Request createMemberRequest() {
        return MemberDTO.Request.builder()
                .name("soora")
                .id("soora")
                .password("qwer1234")
                .authority(MemberAuthority.ROLE_ADMIN)
                .build();
    }

    private Item createItem(Long id, String name, Long amount, Long discountAmount) {
        ItemDTO.Request build = ItemDTO.Request.builder()
                .name(name)
                .amount(amount)
                .discountAmount(discountAmount)
                .build();

        Item item = Item.of(build, null);
        ReflectionTestUtils.setField(item, "id", id);

        return item;
    }
    private ItemOption createItemOption(Item item, Long id, String name, Long premium) {
        ItemOptionDTO.Request build = ItemOptionDTO.Request.builder()
                .premium(premium)
                .name(name)
                .build();
        ItemOption itemOption = ItemOption.of(build, item);
        ReflectionTestUtils.setField(itemOption, "id", id);

        return itemOption;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}