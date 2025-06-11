package com.furniture.miley.dto;

import com.furniture.miley.dto.address.AddressDTO;

public record ProfileDTO(
        String birthDate,
        AddressDTO address,
        String phone
) {}
