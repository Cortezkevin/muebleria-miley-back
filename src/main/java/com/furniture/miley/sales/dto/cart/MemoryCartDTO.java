package com.furniture.miley.sales.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record MemoryCartDTO(
        List<MemoryItemDTO> itemList,
        BigDecimal shippingCost
) {
}
