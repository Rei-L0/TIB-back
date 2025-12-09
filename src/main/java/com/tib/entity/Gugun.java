package com.tib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gugun")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gugun {

  @Id
  @Column(name = "gugun_code")
  private Integer gugunCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sido_code", nullable = false)
  private Sido sido;

  @Column(name = "gugun_name", length = 30)
  private String gugunName;
}
