package com.tib.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attraction_info")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttractionInfo {

  @Id
  @Column(name = "content_id")
  private Integer contentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "content_type_id", nullable = false)
  private ContentType contentType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumns({
          @JoinColumn(name = "gugun_code", referencedColumnName = "gugun_code", insertable = false, updatable = false),
          @JoinColumn(name = "sido_code", referencedColumnName = "sido_code", insertable = false, updatable = false)
  })
  private Gugun gugun;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sido_code", insertable = false, updatable = false)
  private Sido sido;

  @Column(name = "gugun_code")
  private Integer gugunCode;

  @Column(name = "sido_code")
  private Integer sidoCode;

  @Column(length = 100)
  private String title;

  @Column(length = 100)
  private String addr1;

  @Column(length = 50)
  private String addr2;

  @Column(length = 50)
  private String zipcode;

  @Column(length = 50)
  private String tel;

  @Column(name = "first_image", length = 200)
  private String firstImage;

  @Column(name = "first_image2", length = 200)
  private String firstImage2;

  @Builder.Default
  @Column(nullable = false)
  private Integer readcount = 0;

  @Column(precision = 20, scale = 17)
  private BigDecimal latitude;

  @Column(precision = 20, scale = 17)
  private BigDecimal longitude;

  @Column(length = 2)
  private String mlevel;

  @OneToOne(mappedBy = "attractionInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private AttractionDetail attractionDetail;

  @OneToOne(mappedBy = "attractionInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private AttractionDescription attractionDescription;

  public void increaseReadCount() {
    this.readcount++;
  }
}