package kr.sooragenius.shop.order.service;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.config.EmbededRedisTestConfiguration;
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
import kr.sooragenius.shop.order.ItemOrderDetail;
import kr.sooragenius.shop.order.dto.ItemOrderDTO;
import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import kr.sooragenius.shop.order.enums.OrderStatus;
import kr.sooragenius.shop.order.service.infra.ItemOrderRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_= @Autowired)
@Import(EmbededRedisTestConfiguration.class)
public class ItemOrderServiceEventTest {
    private static String REDIS_STOCK_KEY = "item::%d::%d::stock";
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemOrderRepository itemOrderRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private final RedisTemplate redisTemplate;


    private final ItemOrderService itemOrderService;
    @Test
    @DisplayName("order - 재고부족")
    @Transactional
    void orderNoStockInItem() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // given
        Category category = addTopCategory();
        Item item = addItem(category);
        Member member = addMember();
        flush();

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(member.getId())
                .orderDetailRequests(
                        Arrays.asList(
                                ItemOrderDetailDTO.Request.builder()
                                .itemId(item.getId())
                                .optionId(item.getNoneOptionId())
                                .stock(2L)
                                .orderStatus(OrderStatus.COMPLETE)
                                .build()
                        )
                )
                .build();

        // when / then
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> itemOrderService.order(request))
                .withMessageContaining("재고가 부족합니다.");


    }
    @Test
    @DisplayName("order - 재고충")
    @Transactional
    void orderStockExistsInItem() {
        // given
        Category category = addTopCategory();
        Item item = addItem(category);
        Member member = addMember();
        flush();

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(member.getId())
                .orderDetailRequests(
                        Arrays.asList(
                                ItemOrderDetailDTO.Request.builder()
                                        .itemId(item.getId())
                                        .optionId(item.getNoneOptionId())
                                        .stock(1L)
                                        .orderStatus(OrderStatus.COMPLETE)
                                        .build()
                        )
                )
                .build();

        // when
        itemOrderService.order(request);
        flush();

        ItemOption itemOption = itemOptionRepository.findById(item.getNoneOptionId()).get();
        // then
        assertThat(redisTemplate.opsForValue().get(String.format(REDIS_STOCK_KEY, item.getId(), item.getNoneOptionId())))
                .isEqualTo("0");

    }
    @Test
    @DisplayName("order - 취소")
    @Transactional
    void orderCancel() {
        // given
        Category category = addTopCategory();
        Item item = addItem(category);
        Member member = addMember();
        flush();

        String stockRedisKey = String.format(REDIS_STOCK_KEY, item.getId(), item.getNoneOptionId());

        ItemOrderDTO.Request request = ItemOrderDTO.Request.builder()
                .memberId(member.getId())
                .orderDetailRequests(
                        Arrays.asList(
                                ItemOrderDetailDTO.Request.builder()
                                        .itemId(item.getId())
                                        .optionId(item.getNoneOptionId())
                                        .stock(1L)
                                        .orderStatus(OrderStatus.COMPLETE)
                                        .build()
                        )
                )
                .build();

        ItemOrderDTO.Response order = itemOrderService.order(request);
        flush();

        ItemOrder itemOrder = itemOrderRepository.findById(order.getId()).get();

        ItemOrderDetailDTO.RequestCancel cancel = ItemOrderDetailDTO.RequestCancel.builder()
                .orderId(itemOrder.getId())
                .detailId(itemOrder.getItemOrderDetails().get(0).getId())
                .build();
        
        redisTemplate.opsForValue().set(stockRedisKey, "0");
        // when

        itemOrderService.cancelDetail(cancel);
        flush();

        ItemOption itemOption = itemOptionRepository.findById(item.getNoneOptionId()).get();
        // then
        assertThat(redisTemplate.opsForValue().get(stockRedisKey))
                .isEqualTo("1");

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


    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    private Item addItem(Category category) {
        ItemDTO.Request build = ItemDTO.Request.builder()
                .name("Kakao")
                .contents("Kakao")
                .amount(10000L)
                .discountAmount(1000L)
                .deliveryDescription("무료배송")
                .stock(1L)
                .build();

        Item save = itemRepository.save(Item.of(build, category));

        // 재고셋팅
        redisTemplate.opsForValue().set(String.format(REDIS_STOCK_KEY, save.getId(), save.getNoneOptionId()), build.getStock().toString());
        return save;
    }
    private Member addMember() {
        MemberDTO.Request request = MemberDTO.Request.builder().authority(MemberAuthority.ROLE_ADMIN).id("A1").name("A1").password("A1").build();

        Member save = memberRepository.save(Member.of(request, passwordEncoder));
        return save;
    }
    private ItemOrderDetail createItemOrderDetail(Item item, ItemOption itemOption, ItemOrder itemOrder) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Method of = ItemOrderDetail.class.getDeclaredMethod("of", Item.class, ItemOption.class, ItemOrder.class, OrderStatus.class, Long.class);
        of.setAccessible(true);

        ItemOrderDetail itemOrderDetail = (ItemOrderDetail) of.invoke(null, item, itemOption, itemOrder, OrderStatus.COMPLETE, 1L);

        return itemOrderDetail;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}
