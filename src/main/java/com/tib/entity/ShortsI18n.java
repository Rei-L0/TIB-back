package com.tib.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shorts_i18n")
@IdClass(ShortsI18nId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortsI18n {

    @Id
    @Column(name = "shorts_id")
    private Long shortsId;

    @Id
    private String lang;

    private String title;
}