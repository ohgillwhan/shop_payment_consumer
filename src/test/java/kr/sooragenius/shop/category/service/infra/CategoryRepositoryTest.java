package kr.sooragenius.shop.category.service;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceTest {
    private final CategoryRepository categoryRepository;
    private final EntityManager entityManager;

    @Test
    @Transactional
    @DisplayName("최상위 카테고리 추가")
    void saveTopCategory() {
        // given
        CategoryDTO.Request request = CategoryDTO.Request.builder().name("TOP").build();
        Category top = Category.of(request);

        // when
        top = categoryRepository.save(top);
        Long id = top.getId();
        flush();

        Category byId = categoryRepository.findById(id).get();

        // then
        assertTrue(byId.getId() > 0L);
        assertEquals(byId.getId(), byId.getParent().getId());
        assertEquals(top.getName(), byId.getName());
    }
    @Test
    @Transactional
    @DisplayName("부모 카테고리 포함하여 추가")
    void addWithParent() {
        // given
        CategoryDTO.Request parentRequest = CategoryDTO.Request.builder().name("parent").build();
        Category parent = Category.of(parentRequest);
        parent = categoryRepository.save(parent);

        CategoryDTO.Request childRequest = CategoryDTO.Request.builder().name("TOP").build();
        Category child = Category.of(childRequest, parent);

        // when
        child = categoryRepository.save(child);
        Long childId = child.getId();
        flush();

        Category byId = categoryRepository.findById(childId).get();

        // then
        assertTrue(byId.getId() > 0L);
        assertEquals(parent.getId(), byId.getParent().getId());
    }

    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}