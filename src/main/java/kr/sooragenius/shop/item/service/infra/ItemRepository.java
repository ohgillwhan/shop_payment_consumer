package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Modifying
    @Query("update Item as item set item.stock = stock - :stock where item.id = :id and item.stock >= :stock")
    int minusStockByIdWithLock(@Param("id") long id, @Param("stock") long stock);
}
