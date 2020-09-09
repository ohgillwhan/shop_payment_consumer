package kr.sooragenius.shop.order;

import kr.sooragenius.shop.order.enums.OrderStatus;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ItemOrderDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ORDER_DETAIL_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ORDER_ID", referencedColumnName = "ITEM_ORDER_ID")
    private ItemOrder itemOrder;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    protected ItemOrderDetail() {}

    public void updateOrderStatus(OrderStatus orderStatus) {
        if(orderStatus == OrderStatus.COMPLETE || orderStatus == OrderStatus.CANCEL) {
            if (getOrderStatus() == OrderStatus.WAIT) {
                this.orderStatus = orderStatus;
            }
        }
    }
}
