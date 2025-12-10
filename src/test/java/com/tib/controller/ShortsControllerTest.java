package com.tib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tib.dto.ShortsDto;
import com.tib.dto.ShortsListReq;
import com.tib.dto.ShortsListRes;
import com.tib.service.ShortsService;
import java.time.LocalDateTime;
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
class ShortsControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ShortsService shortsService;

  @InjectMocks
  private ShortsController shortsController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(shortsController).build();
  }

  @Test
  @DisplayName("GET /shorts - Should return paginated shorts list")
  void getShortsList() throws Exception {
    // Given
    ShortsDto mockShorts = ShortsDto.builder()
        .id(1L)
        .title("Busan Night View")
        .thumbnailUrl("http://example.com/thumb.jpg")
        .good(100)
        .readcount(500)
        .liked(true)
        .createdAt(LocalDateTime.now())
        .build();

    ShortsListRes mockResponse = ShortsListRes.builder()
        .content(List.of(mockShorts))
        .page(1)
        .size(20)
        .totalElements(1)
        .totalPages(1)
        .build();

    given(shortsService.getShortsList(any(ShortsListReq.class))).willReturn(mockResponse);

    // When & Then
    mockMvc.perform(get("/shorts")
        .param("page", "1")
        .param("size", "20")
        .param("sort", "good")
        .param("hashtag", "nightview"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(1L))
        .andExpect(jsonPath("$.content[0].title").value("Busan Night View"))
        .andExpect(jsonPath("$.content[0].liked").value(true))
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.totalElements").value(1));
  }
}
