package kr.sooragenius.shop.category;

import kr.sooragenius.shop.category.dto.CategoryDTO;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID", referencedColumnName = "CATEGORY_ID")
    private Category parent;

    public static Category of(CategoryDTO.Request request, Category parent) {
        Category category = of(request);
        category.parent = parent;

        return category;
    }
    public static Category of(CategoryDTO.Request request) {
        Category category = new Category();
        category.name = request.getName();
        category.parent = category;

        return category;
    }
}
