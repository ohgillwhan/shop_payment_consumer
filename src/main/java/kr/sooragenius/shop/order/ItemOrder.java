package kr.sooragenius.shop.order;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.ItemOption;
import kr.sooragenius.shop.member.Member;
import kr.sooragenius.shop.order.dto.ItemOrderDetailDTO;
import kr.sooragenius.shop.order.enums.OrderStatus;
import lombok.Getter;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ItemOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ORDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", referencedColumnName = "MEMBER_ID")
    private Member member;

    @OneToMany(cascade = {CascadeType.PERSIST}, mappedBy = "itemOrder")
    private List<ItemOrderDetail> itemOrderDetails = new ArrayList<>();

    private long totalAmount;
    private long totalDiscountAmount;
    private long totalPayAmount;

    public static ItemOrder of(Member member) {
        ItemOrder itemOrder = new ItemOrder();
        itemOrder.member = member;

        return itemOrder;
    }
    public ItemOrderDetailDTO.ResponseFromOrder addOrderDetails(Item item, ItemOption option, OrderStatus orderStatus, ItemOrderDetailDTO.Request request) {
        ItemOrderDetail detail = ItemOrderDetail.of(item, option, this, orderStatus, request.getStock());
        getItemOrderDetails().add(detail);

        totalAmount += detail.getAmount();
        totalDiscountAmount += detail.getDiscountAmount();
        totalPayAmount += detail.getPayAmount();

        return ItemOrderDetailDTO.ResponseFromOrder.of(detail);
    }
    public ItemOrderDetailDTO.Response cancelOrderDetail(long detailId) {
        ItemOrderDetail itemOrderDetail = findById(detailId);
        if(itemOrderDetail == null) {
            throw new IllegalArgumentException("존재하지 않는 상세정보 입니다.");
        }

        totalAmount -= itemOrderDetail.getAmount();
        totalDiscountAmount -= itemOrderDetail.getDiscountAmount();
        totalPayAmount -= itemOrderDetail.getPayAmount();

        itemOrderDetail.cancel();

        return ItemOrderDetailDTO.Response.of(itemOrderDetail);
    }

    private ItemOrderDetail findById(long id) {
        return getItemOrderDetails().stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상세정보 입니다"));
    }
}
