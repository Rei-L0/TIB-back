package com.tib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "shorts")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert // Keep DynamicInsert for default values
public class Short {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String video;

  @Column(name = "created_at", nullable = false)
  @ColumnDefault("CURRENT_TIMESTAMP")
  private LocalDateTime createdAt;

  @Column(name = "recorded_at")
  private LocalDateTime recordedAt;

  @Column(precision = 20, scale = 17)
  private BigDecimal latitude;

  @Column(precision = 20, scale = 17)
  private BigDecimal longitude;

  @Builder.Default
  @ColumnDefault("0")
  private Integer good = 0;

  @Builder.Default
  @ColumnDefault("0")
  private Integer readcount = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_id")
  private AttractionInfo attractionInfo;

  @Column(name = "hls_master_url", length = 500)
  private String hlsMasterUrl;

  @Column(name = "thumbnail_url", length = 500)
  private String thumbnailUrl;

  @Builder.Default
  @Column(nullable = false, length = 20)
  @ColumnDefault("'READY'")
  private String status = "READY";
}
