package kr.sooragenius.shop.category.service;

import kr.sooragenius.shop.category.dto.CategoryDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CategoryServiceTest {
    private final CategoryService categoryService;
    private final EntityManager entityManager;

    @Test
    @Transactional
    void addSelf() {
        CategoryDTO.Request top = CategoryDTO.Request.builder().name("TOP").build();

        Long id = categoryService.addCategory(top);
        flush();

        CategoryDTO.Response byId = categoryService.findById(id);

        assertTrue(byId.getId() > 0L);
        assertEquals(byId.getId(), byId.getParentId());
        assertEquals(top.getName(), byId.getName());
    }
    @Test
    @Transactional
    void addWithParent() {
        CategoryDTO.Request parent = CategoryDTO.Request.builder().name("parent").build();
        Long parentId = categoryService.addCategory(parent);

        CategoryDTO.Request child = CategoryDTO.Request.builder().name("TOP").parentId(parentId).build();
        Long childId = categoryService.addCategory(child);

        flush();

        CategoryDTO.Response byId = categoryService.findById(childId);

        assertTrue(byId.getId() > 0L);
        assertEquals(parentId, byId.getParentId());
    }

    private void flush() {
        entityManager.flush();
        entityManager.clear();
    }
}