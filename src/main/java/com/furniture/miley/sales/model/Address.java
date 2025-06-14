package com.furniture.miley.sales.model;

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

    public static Address createEmpty(){
        return Address.builder()
                .urbanization("")
                .postalCode(0)
                .street("")
                .fullAddress("")
                .province("")
                .district("")
                .department("")
                .build();
    }
}
