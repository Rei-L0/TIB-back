// GugunId.java
package com.tib.entity;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GugunId implements Serializable {

    @Column(name = "gugun_code")
    private Integer gugunCode;

    @Column(name = "sido_code")
    private Integer sidoCode;
}