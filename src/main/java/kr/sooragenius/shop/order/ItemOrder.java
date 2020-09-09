package kr.sooragenius.shop.order;

import kr.sooragenius.shop.order.enums.OrderStatus;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ItemOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ORDER_ID")
    private Long id;

    @OneToMany(mappedBy = "itemOrder")
    private List<ItemOrderDetail> itemOrderDetails = new ArrayList<>();


    public void updateOrderStatus(OrderStatus orderStatus) {
        List<ItemOrderDetail> itemOrderDetails = getItemOrderDetails();

        for(ItemOrderDetail detail : itemOrderDetails) {
            detail.updateOrderStatus(orderStatus);
        }
    }

}
