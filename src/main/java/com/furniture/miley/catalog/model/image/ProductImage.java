package com.furniture.miley.catalog.model.image;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "image_type")
public abstract class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;
    protected String url;
}
