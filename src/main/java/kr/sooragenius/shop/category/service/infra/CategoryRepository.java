package kr.sooragenius.shop.category.service.infra;

import kr.sooragenius.shop.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
