package com.furniture.miley.catalog.model.image;

import com.furniture.miley.catalog.model.color.ProductColor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@DiscriminatorValue("COLOR")
public class ColorProductImage extends ProductImage {
    @ManyToOne
    private ProductColor productColor;

    public ColorProductImage(String url, String imageId, ProductColor productColor) {
        super(null, imageId, url);
        this.productColor = productColor;
    }
}
