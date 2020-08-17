package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.item.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {
    @Modifying
    @Query("update ItemOption as itemOption set itemOption.stock = itemOption.stock - :stock where itemOption.id = :id and itemOption.stock >= :stock")
    int minusStockByIdWithLock(@Param("id") long id, @Param("stock") long stock);
}
