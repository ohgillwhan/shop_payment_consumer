package kr.sooragenius.shop.order.service;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.ItemService;
import kr.sooragenius.shop.item.service.infra.ItemOptionRepository;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import kr.sooragenius.shop.member.service.infra.MemberRepository;
import kr.sooragenius.shop.order.ItemOrder;
import kr.sooragenius.shop.order.dto.ItemOrderDTO;
import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import kr.sooragenius.shop.order.enums.OrderStatus;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemOrderServiceCancelTest {
    @MockBean
    private ItemRepository itemRepository;
    @Mock
    private ItemOrderRepository itemOrderRepository;
    @Mock
    private ItemOptionRepository itemOptionRepository;
    @Mock
    private MemberRepository memberRepository;


    private ItemOrderService itemOrderService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    public void beforeEach() {
        itemOrderService = new ItemOrderService(itemRepository, itemOrderRepository, itemOptionRepository, memberRepository, applicationEventPublisher);
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

        assertThatIllegalArgumentException()
                .isThrownBy(() -> itemOrderService.cancelDetail(requestCancel))
                .withMessageContaining("존재하지 않는 상세정보 입니다");
    }

    @Test
    @DisplayName("cancel - 존재하지 않는 주문")
    @Transactional
    public void cancelNotExistsOrder() {
        ItemOrderDetailDTO.RequestCancel requestCancel = ItemOrderDetailDTO.RequestCancel.builder()
                .detailId(1L)
                .orderId(1L)
                .build();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> itemOrderService.cancelDetail(requestCancel))
                .withMessageContaining("존재하지 않는 주문입니다");
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

        ItemOption blackKakaoOption = createItemOption(blackKakao, 1L,"두배로!", 500L);
        ItemOption blackNoneOption = blackKakao.getItemOptions().get(0);
        ItemOption whiteNoneOption = whiteKakao.getItemOptions().get(0);
        ItemOption pinkNoneOption = pinkKakao.getItemOptions().get(0);


        ItemOrderDetailDTO.Request blackKakaoDetailRequest = ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(1L).stock(10L).build();
        ItemOrderDetailDTO.Request blackNoneDetailRequest = ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(2L).stock(20L).build();
        ItemOrderDetailDTO.Request whiteNoneDetailRequest = ItemOrderDetailDTO.Request.builder().itemId(2L).optionId(3L).stock(30L).build();
        ItemOrderDetailDTO.Request pinkNoneDetailRequest = ItemOrderDetailDTO.Request.builder().itemId(3L).optionId(4L).stock(40L).build();

        // when
        itemOrder.addOrderDetails(blackKakao, blackNoneOption, OrderStatus.COMPLETE, blackKakaoDetailRequest);
        itemOrder.addOrderDetails(whiteKakao, whiteNoneOption, OrderStatus.COMPLETE, blackNoneDetailRequest);
        itemOrder.addOrderDetails(pinkKakao, pinkNoneOption, OrderStatus.COMPLETE, whiteNoneDetailRequest);
        itemOrder.addOrderDetails(blackKakao, blackKakaoOption, OrderStatus.COMPLETE, pinkNoneDetailRequest);

        for(int i = 0; i<4; i++) {
            long id = i + 1;

            ReflectionTestUtils.setField(itemOrder.getItemOrderDetails().get(i), "id", id);
        }

        when(itemOptionRepository.findById(1L))
                .thenReturn(Optional.of(blackKakaoOption));
        when(itemOptionRepository.findById(2L))
                .thenReturn(Optional.of(blackNoneOption));
        when(itemOptionRepository.findById(3L))
                .thenReturn(Optional.of(whiteNoneOption));
        when(itemOptionRepository.findById(4L))
                .thenReturn(Optional.of(pinkNoneOption));

        ItemOrderDetailDTO.Response response = itemOrder.cancelOrderDetail(2L);
        itemOrder.cancelOrderDetail(4L);
        // then
        long itemTotalPayAmount = blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount() + blackKakao.getPayAmount() + blackKakaoOption.getPremium();
        long itemTotalAmount = blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount() + blackKakao.getAmount() + blackKakaoOption.getPremium();
        long itemTotalDiscount = blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount();

        System.out.println(itemOrder.getItemOrderDetails().get(1).getItem().getName());

        assertThat(itemOrder.getItemOrderDetails().get(0).getItem())
                .isNotNull()
                .isEqualTo(blackKakao);

        assertThat(itemOrder.getItemOrderDetails().get(1).getItem())
                .isNotNull()
                .isEqualTo(pinkKakao);

        assertThat(blackNoneOption.getStock())
                .isNotNull()
                .isEqualTo(1L);
        assertThat(whiteNoneOption.getStock())
                .isNotNull()
                .isEqualTo(1L);
        assertThat(pinkNoneOption.getStock())
                .isNotNull()
                .isEqualTo(1L);

        assertThat(response.getPayAmount())
                .isGreaterThan(0L)
                .isEqualTo(whiteKakao.getPayAmount());

        assertThat(response.getAmount())
                .isGreaterThan(0L)
                .isEqualTo(whiteKakao.getAmount());

        assertThat(response.getDiscountAmount())
                .isGreaterThan(0L)
                .isEqualTo(whiteKakao.getDiscountAmount());

        assertThat(itemOrder.getTotalPayAmount())
                .isGreaterThan(0L)
                .isEqualTo(itemTotalPayAmount - whiteKakao.getPayAmount() - blackKakao.getPayAmount() - blackKakaoOption.getPremium());

        assertThat(itemOrder.getTotalAmount())
                .isGreaterThan(0L)
                .isEqualTo(itemTotalAmount - whiteKakao.getAmount() - blackKakao.getAmount() - blackKakaoOption.getPremium());

        assertThat(itemOrder.getTotalDiscountAmount())
                .isGreaterThan(0L)
                .isEqualTo(itemTotalDiscount - whiteKakao.getDiscountAmount());
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

        return createItem(id, name, amount, discountAmount, 1L);
    }
    private Item createItem(Long id, String name, Long amount, Long discountAmount, long stock) {
        ItemDTO.Request build = ItemDTO.Request.builder()
                .name(name)
                .amount(amount)
                .discountAmount(discountAmount)
                .stock(stock)
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