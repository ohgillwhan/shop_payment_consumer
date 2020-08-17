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

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemOrderServiceOrderTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemOrderRepository itemOrderRepository;
    @MockBean
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
    @DisplayName("order - 계정 없을시 에러")
    @Transactional
    void orderMemberError() {
        // given
        Item blackKakao = createItem(1L, 1L, "blackKakao", 1000L, 100L);
        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(null).build()
                ))
                .build();
        // when
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao));

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> itemOrderService.order(request))
                .withMessageContaining("존재하지 않는 계정입니다");
    }
    @Test
    @DisplayName("order - 구매할 물품이 없을시 에러")
    @Transactional
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
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> itemOrderService.order(request))
                .withMessageContaining("구매할 물품이 없습니다");
    }
    @Test
    @DisplayName("order - 없는 물건 구매하려고 할 경우")
    @Transactional
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
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> itemOrderService.order(request))
                .withMessageContaining("존재하지 않는 상품입니다");
    }
    @Test
    @DisplayName("order - 없는 옵션으로 구매하려고 할 경우")
    @Transactional
    void orderNotExistOptionError() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();

        Item blackKakao = createItem(1L, 1L, "blackKakao", 1000L, 100L);

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
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> itemOrderService.order(request))
                .withMessageContaining("존재하지 않는 옵션입니다");
    }
    @Test
    @DisplayName("order - 재고부족")
    @Transactional
    void orderNoStockInItem() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();
        Item blackKakao = createItem(1L, 1L,"blackKakao", 1000L, 100L, 0L);


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
        when(itemOptionRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao.getItemOptions().get(0)));
        when(itemOptionRepository.minusStockByIdWithLock(anyLong(), anyLong()))
                .thenReturn(0);

        // then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> itemOrderService.order(request))
                .withMessageContaining("재고가 부족합니다.");
    }
    @Test
    @DisplayName("order - 재고충")
    @Transactional
    void orderStockExistsInItem() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();
        Item blackKakao = createItem(1L, 1L,"blackKakao", 1000L, 100L, 2L);


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
        when(itemOptionRepository.findById(1L))
                .thenReturn(Optional.of(blackKakao.getItemOptions().get(0)));
        when(itemOptionRepository.minusStockByIdWithLock(anyLong(), anyLong()))
                .thenReturn(1);

        // then
        itemOrderService.order(request);
    }
    @Test
    @DisplayName("order - 옵션없이")
    @Transactional
    void orderWithoutOption() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();
        Item blackKakao = createItem(1L, 1L, "blackKakao", 1000L, 100L);
        Item whiteKakao = createItem(2L, 2L,"whiteKakao", 5000L, 333L);
        Item pinkKakao = createItem(3L, 3L,"pinkKakao", 4000L, 250L);


        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(1L).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(2L).optionId(2L).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(3L).optionId(3L).build()
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
                .thenReturn(Optional.of(blackKakao.getItemOptions().get(0)));
        when(itemOptionRepository.findById(2L))
                .thenReturn(Optional.of(whiteKakao.getItemOptions().get(0)));
        when(itemOptionRepository.findById(3L))
                .thenReturn(Optional.of(pinkKakao.getItemOptions().get(0)));
        when(itemOptionRepository.minusStockByIdWithLock(anyLong(), anyLong()))
                .thenReturn(1);

        ItemOrderDTO.Response order = itemOrderService.order(request);

        // then
        assertThat(order.getTotalPayAmount())
                .isGreaterThan(0L)
                .isEqualTo(blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount());

        assertThat(order.getTotalAmount())
                .isGreaterThan(0L)
                .isEqualTo(blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount());

        assertThat(order.getTotalDiscountAmount())
                .isGreaterThan(0L)
                .isEqualTo(blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount());

        assertThat(order.getOrderDetails().size())
                .isGreaterThan(0)
                .isEqualTo(request.getOrderDetailRequests().size());

        order.getOrderDetails().stream().forEach(detail -> {
            Item item = itemRepository.findById(detail.getItemId()).get();

            assertThat(detail.getDiscountAmount())
                    .isGreaterThan(0L)
                    .isEqualTo(item.getDiscountAmount());

            assertThat(detail.getPayAmount())
                    .isGreaterThan(0L)
                    .isEqualTo(item.getPayAmount());

            assertThat(detail.getAmount())
                    .isGreaterThan(0L)
                    .isEqualTo(item.getAmount());
        });
    }
    @Test
    @DisplayName("order - 옵션 추가하여")
    @Transactional
    void orderWithOption() {
        // given
        MemberDTO.Request memberRequest = createMemberRequest();
        Item blackKakao = createItem(1L, 1L,"blackKakao", 1000L, 100L);
        Item whiteKakao = createItem(2L, 2L,"whiteKakao", 5000L, 333L);
        Item pinkKakao = createItem(3L, 3L,"pinkKakao", 4000L, 250L);

        ItemOption blackKakaoOption = createItemOption(blackKakao, 1L,"두배로!", 500L);

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(memberRequest.getId())
                .orderDetailRequests(Arrays.asList(
                        ItemOrderDetailDTO.Request.builder().itemId(1L).optionId(1L).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(2L).optionId(2L).build(),
                        ItemOrderDetailDTO.Request.builder().itemId(3L).optionId(3L).build()
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
        when(itemOptionRepository.findById(2L))
                .thenReturn(Optional.of(whiteKakao.getItemOptions().get(0)));
        when(itemOptionRepository.findById(3L))
                .thenReturn(Optional.of(pinkKakao.getItemOptions().get(0)));
        when(itemOptionRepository.minusStockByIdWithLock(anyLong(), anyLong()))
                .thenReturn(1);


        ItemOrderDTO.Response order = itemOrderService.order(request);

        // then
        long itemTotalPayAmount = blackKakao.getPayAmount() + pinkKakao.getPayAmount() + whiteKakao.getPayAmount();
        long itemTotalAmount = blackKakao.getAmount() + pinkKakao.getAmount() + whiteKakao.getAmount();
        long itemTotalDiscount = blackKakao.getDiscountAmount() + pinkKakao.getDiscountAmount() + whiteKakao.getDiscountAmount();

        assertThat(order.getTotalPayAmount())
                .isGreaterThan(0L)
                .isEqualTo(itemTotalPayAmount + blackKakaoOption.getPremium());

        assertThat(order.getTotalAmount())
                .isGreaterThan(0L)
                .isEqualTo(itemTotalAmount + blackKakaoOption.getPremium());

        assertThat(order.getTotalDiscountAmount())
                .isGreaterThan(0L)
                .isEqualTo(itemTotalDiscount);

        assertThat(order.getOrderDetails().size())
                .isGreaterThan(0)
                .isEqualTo(request.getOrderDetailRequests().size());

        order.getOrderDetails().stream().forEach(detail -> {
            Item item = itemRepository.findById(detail.getItemId()).get();
            System.out.println(item.getName());
            long discountAmount = item.getDiscountAmount();
            long payAmount = item.getPayAmount();
            long amount = item.getAmount();
            if(detail.getOptionId() != null && detail.getOptionId() > 0L) {
                ItemOption itemOption = itemOptionRepository.findById(detail.getOptionId()).get();

                payAmount += itemOption.getPremium();
                amount += itemOption.getPremium();
            }

            assertThat(detail.getDiscountAmount())
                    .isGreaterThan(0L)
                    .isEqualTo(discountAmount);

            assertThat(detail.getPayAmount())
                    .isGreaterThan(0L)
                    .isEqualTo(payAmount);

            assertThat(detail.getAmount())
                    .isGreaterThan(0L)
                    .isEqualTo(amount);
        });
    }

    private MemberDTO.Request createMemberRequest() {
        return MemberDTO.Request.builder()
                .name("soora")
                .id("soora")
                .password("qwer1234")
                .authority(MemberAuthority.ROLE_ADMIN)
                .build();
    }

    private Item createItem(Long id, Long noneOptionId, String name, Long amount, Long discountAmount) {

        return createItem(id, noneOptionId, name, amount, discountAmount, 1L);
    }
    private Item createItem(Long id, Long noneOptionId, String name, Long amount, Long discountAmount, long stock) {
        ItemDTO.Request build = ItemDTO.Request.builder()
                .name(name)
                .amount(amount)
                .discountAmount(discountAmount)
                .stock(stock)
                .build();

        Item item = Item.of(build, null);
        ItemOption itemOption = item.getItemOptions().get(0);

        ReflectionTestUtils.setField(item, "id", id);
        ReflectionTestUtils.setField(itemOption, "id", noneOptionId);

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