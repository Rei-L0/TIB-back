package com.tib.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
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

  @EmbeddedId
  private GugunId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sido_code", insertable = false, updatable = false)
  private Sido sido;

  @Column(name = "gugun_name", length = 30)
  private String gugunName;

  public Integer getGugunCode() {
    return id.getGugunCode();
  }

  public Integer getSidoCode() {
    return id.getSidoCode();
  }
}