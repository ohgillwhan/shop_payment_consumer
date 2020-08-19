package kr.sooragenius.shop.item.service;

import kr.sooragenius.shop.item.dto.ItemOptionDTO;
import kr.sooragenius.shop.item.service.infra.ItemOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemOptionService {
    private final ItemOptionRepository itemOptionRepository;

    public void minusStockById(ItemOptionDTO.StockUpdate stockUpdate) {
        int count = itemOptionRepository.minusStockByIdWithLock(stockUpdate.getId(), stockUpdate.getStock());

        if(count == 0) throw new RuntimeException("재고가 부족합니다.");
    }

    public void plusStockById(ItemOptionDTO.StockUpdate stockUpdate) {
        itemOptionRepository.plusStockById(stockUpdate.getId(), stockUpdate.getStock());
    }
}
