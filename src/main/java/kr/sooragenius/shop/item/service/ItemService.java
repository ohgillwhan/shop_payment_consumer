package kr.sooragenius.shop.item.service;

import kr.sooragenius.shop.item.Item;
import kr.sooragenius.shop.item.dto.ItemDTO;
import kr.sooragenius.shop.item.service.infra.ItemRepository;
import kr.sooragenius.shop.category.Category;
import kr.sooragenius.shop.category.service.infra.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    public Long addItem(ItemDTO.Request request) {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리 입니다."));

        Item of = Item.of(request, category);

        Item save = itemRepository.save(of);
        return save.getId();
    }
    public ItemDTO.Response findById(Long id) {
        Item item = findByIdInRepo(id);

        return ItemDTO.Response.of(item);
    }

    private Item findByIdInRepo(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다"));
        return item;
    }

    public void minusStockById(ItemDTO.StockUpdate stockUpdate) {
        int count = itemRepository.minusStockByIdWithLock(stockUpdate.getId(), -stockUpdate.getAddStock());
        System.out.println(stockUpdate.getId());
        System.out.println(stockUpdate.getAddStock());
        if(count == 0) throw new RuntimeException("재고가 부족합니다.");
    }
}
