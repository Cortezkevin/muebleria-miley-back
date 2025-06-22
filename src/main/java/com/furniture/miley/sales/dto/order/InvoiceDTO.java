package com.furniture.miley.sales.dto.order;

import org.springframework.http.HttpHeaders;

public record InvoiceDTO(
        Integer invoiceLength,
        byte[] resource,
        HttpHeaders headers
) {
}
