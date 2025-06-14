package com.furniture.miley.security.dto;

import com.furniture.miley.sales.dto.address.AddressDTO;

public record ProfileDTO(
        String birthDate,
        AddressDTO address,
        String phone
) {}
