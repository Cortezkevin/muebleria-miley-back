package com.furniture.miley.catalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Table(name = "sub_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {
    @Id
    private String id;
    private String name;
    private String description;
    private String url_image;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private Category category;

    @OneToMany(mappedBy = "subCategory", fetch = FetchType.LAZY)
    private List<Product> productList = new ArrayList<>();
}
