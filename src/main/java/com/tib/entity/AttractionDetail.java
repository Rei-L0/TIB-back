package com.tib.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attraction_detail")
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttractionDetail {

  @Id
  @Column(name = "content_id")
  private Integer contentId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "content_id")
  private AttractionInfo attractionInfo;

  @Column(length = 3)
  private String cat1;

  @Column(length = 5)
  private String cat2;

  @Column(length = 9)
  private String cat3;

  @Column(name = "created_time", length = 14)
  private String createdTime;

  @Column(name = "modified_time", length = 14)
  private String modifiedTime;

  @Column(length = 5)
  private String booktour;
}
