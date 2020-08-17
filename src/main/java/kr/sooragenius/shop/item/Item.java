package kr.sooragenius.shop.item;

import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.review.Review;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;
    private String contents;
    private String deliveryDescription; // 배달안내문
    private Long amount; // 원가
    private Long discountAmount; // 할인
    private Long payAmount; // 할인된 가격

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "CATEGORY_ID", name = "CATEGORY_ID")
    private Category category;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "item")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, mappedBy = "item")
    private List<ItemOption> itemOptions = new ArrayList<>();

    public static Item of(ItemDTO.Request request, Category category) {
        Item item = new Item();

        item.name = request.getName();
        item.contents = request.getContents();
        item.deliveryDescription = request.getDeliveryDescription();
        item.amount = request.getAmount();
        item.discountAmount = request.getDiscountAmount();
        item.payAmount = request.getAmount() - request.getDiscountAmount();

        item.category = category;

        item.itemOptions.add(ItemOption.createNoneOption(item, request.getStock()));
        return item;
    }
}
