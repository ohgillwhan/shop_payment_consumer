package kr.sooragenius.shop.item;

import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import lombok.Getter;
import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;

@Entity
@Getter
public class
ItemOption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_OPTION_ID")
    private Long id;
    private String name;
    private Long premium;
    private boolean noneOptionAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITME_ID", referencedColumnName = "ITEM_ID")
    private Item item;

    public static ItemOption of(ItemOptionDTO.Request request, Item item) {
        ItemOption itemOption = new ItemOption();
        itemOption.name = request.getName();
        itemOption.premium = request.getPremium();
        itemOption.item = item;

        return itemOption;
    }
    public static ItemOption createNoneOption(Item item, long stock) {
        ItemOptionDTO.Request request = new ItemOptionDTO.Request("None", 0L, stock);
        ItemOption itemOption = of(request, item);
        itemOption.noneOptionAt = true;

        return itemOption;
    }


}
