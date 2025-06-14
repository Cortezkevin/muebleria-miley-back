package com.furniture.miley.sales.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapperDTO<T> {
    private Boolean success;
    private String message;
    private String status;
    private T content;
}
