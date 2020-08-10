package kr.sooragenius.shop.basket.service.infra;

import kr.sooragenius.shop.basket.Basket;
import kr.sooragenius.shop.basket.BasketItem;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.member.dto.MemberDTO;
import kr.sooragenius.shop.member.enums.MemberAuthority;
import kr.sooragenius.shop.member.service.infra.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BasketItemRepositoryTest {
    private final BasketRepository basketRepository;
    private final BasketItemRepository basketItemRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Test
    @Transactional
    @DisplayName("장바구니에 상품 추가")
    public void addBasketItem() {
        // given
        Basket basket = addBasket();
        Category category = addTopCategory();

        ItemDTO.Request itemKakaoRequest = ItemDTO.Request.builder().name("Kakao").amount(1000L).discountAmount(100L).build();
        ItemDTO.Request itemClockRequest = ItemDTO.Request.builder().name("Clock").amount(1000L).discountAmount(100L).build();
        ItemDTO.Request itemPenRequest = ItemDTO.Request.builder().name("Pen").amount(1000L).discountAmount(100L).build();

        Item kakao = itemRepository.save(Item.of(itemKakaoRequest, category));
        Item clock = itemRepository.save(Item.of(itemClockRequest, category));
        Item pen = itemRepository.save(Item.of(itemPenRequest, category));

        // when
        BasketItem kakaoBasketItem = basketItemRepository.save(BasketItem.of(basket, kakao));
        BasketItem clockBasketItem = basketItemRepository.save(BasketItem.of(basket, clock));
        BasketItem penBasketItem = basketItemRepository.save(BasketItem.of(basket, pen));
        flush();

        Map<Long, Long> basketItemIdToItemId = new HashMap<>();
        basketItemIdToItemId.put(kakaoBasketItem.getId(), kakao.getId());
        basketItemIdToItemId.put(clockBasketItem.getId(), clock.getId());
        basketItemIdToItemId.put(penBasketItem.getId(), pen.getId());


        // then
        basketItemIdToItemId.entrySet().stream().forEach(entry -> {
            Long basketItemId = entry.getKey();
            Long itemId = entry.getValue();

            BasketItem basketItem = basketItemRepository.findById(basketItemId).get();
            Item item = itemRepository.findById(itemId).get();

            assertEquals(basket.getId(), basketItem.getBasket().getId());
            assertEquals(item.getId(), basketItem.getItem().getId());
        });
    }

    public Member addMember() {
        MemberDTO.Request request = MemberDTO.Request.builder().authority(MemberAuthority.ROLE_ADMIN).id("A1").name("A1").password("A1").build();

        Member save = memberRepository.save(Member.of(request, passwordEncoder));
        return save;
    }
    public Basket addBasket() {
        Member member = addMember();
        Basket save = basketRepository.save(Basket.of(member));

        return save;
    }
    private Category addTopCategory() {
        return categoryRepository.save(Category.of(CategoryDTO.Request.builder().name("TOP").build()));
    }
    public void flush() {
        entityManager.flush();
        entityManager.clear();
    }

}