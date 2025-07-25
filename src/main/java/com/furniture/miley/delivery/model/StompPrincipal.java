package com.furniture.miley.delivery.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StompPrincipal implements Principal  {
    private String name;
}
