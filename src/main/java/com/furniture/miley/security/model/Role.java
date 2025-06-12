package com.furniture.miley.security.model;

import com.furniture.miley.security.enums.RolName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    private String id;
    @Enumerated( EnumType.STRING )
    private RolName rolName;
}
