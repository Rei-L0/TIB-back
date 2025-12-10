package com.tib.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tib.dto.RegionGugunDto;
import com.tib.dto.RegionSidoGugunRes;
import com.tib.service.RegionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class RegionControllerTest {

  private MockMvc mockMvc;

  @Mock
  private RegionService regionService;

  @InjectMocks
  private RegionController regionController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(regionController).build();
  }

  @Test
  @DisplayName("특정 시도의 구군 목록 조회")
  void getGugunsBySidoCode() throws Exception {
    // given
    RegionGugunDto gugun1 = RegionGugunDto.builder().gugunCode(1).gugunName("강남구").build();
    RegionGugunDto gugun2 = RegionGugunDto.builder().gugunCode(4).gugunName("용산구").build();
    RegionSidoGugunRes response = RegionSidoGugunRes.builder()
        .sidoCode(1)
        .sidoName("서울")
        .guguns(List.of(gugun1, gugun2))
        .build();

    given(regionService.getGugunsBySidoCode(anyInt())).willReturn(response);

    // when & then
    mockMvc.perform(get("/regions/sidos/1/guguns"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sidoCode").value(1))
        .andExpect(jsonPath("$.sidoName").value("서울"))
        .andExpect(jsonPath("$.guguns[0].gugunCode").value(1))
        .andExpect(jsonPath("$.guguns[0].gugunName").value("강남구"))
        .andExpect(jsonPath("$.guguns[1].gugunCode").value(4))
        .andExpect(jsonPath("$.guguns[1].gugunName").value("용산구"));
  }
}
