package com.tib.service;

import com.tib.dto.RegionGugunDto;
import com.tib.dto.RegionSidoGugunRes;
import com.tib.entity.Gugun;
import com.tib.entity.Sido;
import com.tib.repository.GugunRepository;
import com.tib.repository.SidoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegionService {

  private final SidoRepository sidoRepository;
  private final GugunRepository gugunRepository;

  public RegionSidoGugunRes getGugunsBySidoCode(Integer sidoCode) {
    Sido sido = sidoRepository.findById(sidoCode)
        .orElseThrow(() -> new IllegalArgumentException("Invalid sidoCode: " + sidoCode));

    List<Gugun> guguns = gugunRepository.findAllBySido_SidoCode(sidoCode);

    List<RegionGugunDto> gugunDtos = guguns.stream()
        .map(RegionGugunDto::from)
        .collect(Collectors.toList());

    return RegionSidoGugunRes.of(sido, gugunDtos);
  }
}
