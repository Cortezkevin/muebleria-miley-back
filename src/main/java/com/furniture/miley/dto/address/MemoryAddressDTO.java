package com.furniture.miley.dto.address;

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
