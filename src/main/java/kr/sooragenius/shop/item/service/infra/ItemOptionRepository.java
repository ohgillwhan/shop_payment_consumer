package kr.sooragenius.shop.item.service.infra;

import kr.sooragenius.shop.item.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {
}
