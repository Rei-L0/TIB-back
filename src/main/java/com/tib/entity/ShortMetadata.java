package com.tib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shorts_metadata")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortMetadata {

  @Id
  @Column(name = "shorts_id")
  private Long shortsId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "shorts_id")
  private Short shorts;

  @Column(length = 10)
  private String weather;

  @Column(length = 30)
  private String theme;

  @Column(length = 10)
  private String season;

  @Column(name = "DateTimeOriginal")
  private LocalDateTime dateTimeOriginal;
}
