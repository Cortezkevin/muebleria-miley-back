package com.furniture.miley.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Double lta;
    private Double lng;
    private String department;
    private String province;
    private String district;
    private String urbanization;
    private String street;
    private Integer postalCode;
    private String fullAddress;
    @OneToOne(mappedBy = "address")
    private PersonalInformation personalInformation;
}
