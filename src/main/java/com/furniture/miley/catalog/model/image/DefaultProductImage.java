package com.furniture.miley.catalog.model.image;

import com.furniture.miley.catalog.model.Product;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
@DiscriminatorValue("PRODUCT")
public class DefaultProductImage extends ProductImage {
    @ManyToOne
    private Product product;

    public DefaultProductImage( String url, Product product) {
        super(null, url);
        this.product = product;
    }
}
