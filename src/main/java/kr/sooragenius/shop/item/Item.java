package kr.sooragenius.shop.item;

import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.category.Category;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(referencedColumnName = "CATEGORY_ID", name = "CATEGORY_ID")
    private Category category;

    public static Item of(ItemDTO.Request request, Category category) {
        Item item = new Item();

        item.name = request.getName();
        item.category = category;

        return item;
    }
}
