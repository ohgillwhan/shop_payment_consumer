package kr.sooragenius.shop.order.service.infra;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
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
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemOrderRepositoryTest {
    private final ItemOrderRepository itemOrderRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;


    @DisplayName("주문 과 이벤트 실행")
    @Test
    @Transactional
    public void addOrderAndPublishEventWithoutOption() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        // given
        Category category = addTopCategory();
        Item item = addItem(category);
        Member member = addMember();
        ItemOrder itemOrder = ItemOrder.of(member);

        // when
        itemOrder.publishItemOrderEvent();
        itemOrder = itemOrderRepository.save(itemOrder);

        List<ItemOrderDetail> itemOrderDetails = Arrays.asList(
                createItemOrderDetail(item, null, itemOrder),
                createItemOrderDetail(item, null, itemOrder),
                createItemOrderDetail(item, null, itemOrder)
        );
        ReflectionTestUtils.setField(itemOrder, "itemOrderDetails", itemOrderDetails);
        flush();
        itemOrder = itemOrderRepository.findById(itemOrder.getId()).get();

        // then
        assertEquals(itemOrder.getMember().getId(), member.getId());
        assertFalse(itemOrder.getItemOrderDetails().isEmpty());
        assertEquals(itemOrderDetails.size(), itemOrder.getItemOrderDetails().size());

        // 함수를 통해 save를 한것이 아니라서 0원
        assertEquals(0L, itemOrder.getTotalAmount());
        assertEquals(0L, itemOrder.getTotalDiscountAmount());
        assertEquals(0L, itemOrder.getTotalPayAmount());
    }


    @DisplayName("주문삭제")
    @Test
    @Transactional
    public void remove() throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        // given
        Category category = addTopCategory();
        Item item = addItem(category);
        Member member = addMember();

        // when
        ItemOrder itemOrder = itemOrderRepository.save(ItemOrder.of(member));
        List<ItemOrderDetail> itemOrderDetails = Arrays.asList(
                createItemOrderDetail(item, null, itemOrder),
                createItemOrderDetail(item, null, itemOrder),
                createItemOrderDetail(item, null, itemOrder)
        );
        ReflectionTestUtils.setField(itemOrder, "itemOrderDetails", itemOrderDetails);
        flush();
        itemOrder = itemOrderRepository.findById(itemOrder.getId()).get();
        List<ItemOrderDetail> itemOrderDetails1 = itemOrder.getItemOrderDetails();
        itemOrderDetails1.remove(0);
        flush();
        itemOrder = itemOrderRepository.findById(itemOrder.getId()).get();
        // then
        assertEquals(itemOrderDetails.size() - 1, itemOrder.getItemOrderDetails().size());
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
                .build();
        return itemRepository.save(Item.of(build, category));
    }
    private Member addMember() {
        MemberDTO.Request request = MemberDTO.Request.builder().authority(MemberAuthority.ROLE_ADMIN).id("A1").name("A1").password("A1").build();

        Member save = memberRepository.save(Member.of(request, passwordEncoder));
        return save;
    }
    private ItemOrderDetail createItemOrderDetail(Item item, ItemOption itemOption, ItemOrder itemOrder) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        Method of = ItemOrderDetail.class.getDeclaredMethod("of", Item.class, ItemOption.class, ItemOrder.class);
        of.setAccessible(true);

        ItemOrderDetail itemOrderDetail = (ItemOrderDetail) of.invoke(null, item, itemOption, itemOrder);

        return itemOrderDetail;
    }
    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}