package com.furniture.miley.profile.model;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue( strategy = GenerationType.UUID )
    private String id;
    private String title;
    private String body;
    private Timestamp date;
}
