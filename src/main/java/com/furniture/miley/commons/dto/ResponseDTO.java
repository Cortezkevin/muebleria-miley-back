package com.furniture.miley.commons.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class ResponseDTO {
       protected String message;
       protected boolean success;
       protected String statusCode;
}
