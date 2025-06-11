package com.furniture.miley.commons.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SuccessResponseDTO<T> extends ResponseDTO {
    private T content;
    public SuccessResponseDTO(String message, String statusCode, T content) {
        super(message, true, statusCode);
        this.content = content;
    }
}
