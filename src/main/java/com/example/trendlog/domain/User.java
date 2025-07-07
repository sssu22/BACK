package com.example.trendlog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String provider; // ex: "local", "google"

    @Column(columnDefinition = "TEXT")
    private String profileImage;

    @Column(nullable = false)
    private LocalDateTime signDate;

    private LocalDate birth;

    @Column(length = 100)
    private String address;

    @Column(length = 200)
    private String stateMessage;

    @Column(nullable = false)
    private Boolean publicProfile;

    @Column(nullable = false)
    private Boolean locationTracing;

    @Column(nullable = false)
    private Boolean alarm;
}
