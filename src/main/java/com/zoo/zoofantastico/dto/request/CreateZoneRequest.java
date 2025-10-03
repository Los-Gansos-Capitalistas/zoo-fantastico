package com.zoo.zoofantastico.dto.request;

import lombok.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateZoneRequest {
@NotBlank private String name;
@Size(max = 255) private String description;
@NotNull @Min(1) private Integer capacity;

}
