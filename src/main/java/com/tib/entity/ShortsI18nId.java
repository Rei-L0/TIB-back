package com.tib.entity;

import lombok.*;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ShortsI18nId implements Serializable {
    private Long shortsId;
    private String lang;
}