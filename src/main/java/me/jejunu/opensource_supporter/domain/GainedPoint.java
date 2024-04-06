package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gained_points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GainedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long index;

    private double price;

    private String method;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public GainedPoint(User user, String method, double price) {
        this.user = user;
        this.method = method;
        this.price = price;
    }
}
