package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
