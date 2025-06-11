package com.furniture.miley.commons.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorResponseDTO extends ResponseDTO {
    public ErrorResponseDTO(String message, String statusCode) {
        super(message, false, statusCode);
    }
}
