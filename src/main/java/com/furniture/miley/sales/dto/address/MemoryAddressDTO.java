package com.furniture.miley.sales.dto.address;

public record MemoryAddressDTO(
        Double lta,
        Double lng,
        String department,
        String province,
        String district,
        String urbanization,
        String street,
        Integer postalCode,
        String fullAddress
){}
