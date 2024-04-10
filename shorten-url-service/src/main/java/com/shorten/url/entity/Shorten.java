package com.shorten.url.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "shortens")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Shorten {

    @Id
    @Column(name = "shorten_url")
    private String shortenUrl;

    @Column(name = "long_url")
    private String longUrl;

}
