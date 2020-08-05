package kr.sooragenius.shop.category.service;

import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.dto.CategoryDTO;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Long addCategory(CategoryDTO.Request request) {
        Category category = null;
        if(request.getParentId() == null || request.getParentId() == 0) {
            category = Category.of(request);
        }else {
            Category parent = findByIdInRepo(request.getParentId());
            category = Category.of(request, parent);
        }

        Category save = categoryRepository.save(category);
        return save.getId();
    }
    public CategoryDTO.Response findById(Long id) {
        Category category = findByIdInRepo(id);

        return CategoryDTO.Response.of(category);
    }



    private Category findByIdInRepo(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ID 입니다."));
    }
}
