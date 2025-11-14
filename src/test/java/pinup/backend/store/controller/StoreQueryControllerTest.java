package pinup.backend.store.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pinup.backend.store.command.dto.StoreDetailResponseDto;
import pinup.backend.store.command.dto.StoreSummaryResponseDto;
import pinup.backend.store.query.controller.StoreQueryController;
import pinup.backend.store.query.dto.StoreHighlightResponseDto;
import pinup.backend.store.query.dto.StorePageResponseDto;
import pinup.backend.store.query.service.StoreQueryService;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class StoreQueryControllerTest {

    @Mock
    private StoreQueryService storeQueryService;
    @InjectMocks
    private StoreQueryController storeQueryController;
    private MockMvc mockMvc;

    private StoreSummaryResponseDto summaryDto;
    private StoreDetailResponseDto detailDto;
    private StoreHighlightResponseDto highlightDto;
    private StorePageResponseDto pageResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(storeQueryController).build();

        summaryDto = StoreSummaryResponseDto.builder()
                .itemId(1)
                .name("테스트 배경")
                .price(100)
                .category("BUILDING")
                .limited("NORMAL")
                .isActive(true)
                .imageUrl("test.png")
                .createdAt(LocalDateTime.now())
                .build();

        detailDto = StoreDetailResponseDto.builder()
                .itemId(1)
                .name("테스트 배경")
                .description("테스트용 배경 아이템")
                .price(100)
                .category("BUILDING")
                .limitType("NORMAL")
                .isActive(true)
                .imageUrl("test.png")
                .createdAt(LocalDateTime.now())
                .regionName("서울")
                .build();

        highlightDto = StoreHighlightResponseDto.builder()
                .limitedItems(List.of(summaryDto))
                .latestItems(List.of(summaryDto))
                .build();

        pageResponseDto = StorePageResponseDto.builder()
                .page(0)
                .size(6)
                .totalItems(1L)
                .totalPages(1)
                .items(List.of(summaryDto))
                .build();

    }


    @Test
    @DisplayName("전체 판매 아이템 조회 성공")
    void getActiveItems() throws Exception {
        given(storeQueryService.getActiveItems()).willReturn(List.of(summaryDto));

        mockMvc.perform(get("/store/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("테스트 배경"))
                .andExpect(jsonPath("$[0].price").value(100))
                .andExpect(jsonPath("$[0].category").value("BUILDING"))
                .andDo(print());

        verify(storeQueryService, times(1)).getActiveItems();
    }

    @Test
    @DisplayName("단일 아이템 상세 조회 성공")
    void getItemDetail() throws Exception {
        given(storeQueryService.getItemById(1)).willReturn(detailDto);

        mockMvc.perform(get("/store/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("테스트 배경"))
                .andExpect(jsonPath("$.description").value("테스트용 배경 아이템"))
                .andExpect(jsonPath("$.regionName").value("서울"))
                .andDo(print());

        verify(storeQueryService, times(1)).getItemById(1);
    }

    @Test
    @DisplayName("기간 한정 및 최신 아이템 조회 성공")
    void getHighlights() throws Exception {
        given(storeQueryService.getHighlights(null, null))
                .willReturn(highlightDto);

        mockMvc.perform(get("/store/items/highlights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limitedItems[0].name").value("테스트 배경"))
                .andExpect(jsonPath("$.latestItems[0].name").value("테스트 배경"))
                .andDo(print());

        verify(storeQueryService, times(1)).getHighlights(null, null);
    }

    @Test
    @DisplayName("스토어 아이템 페이징 조회 성공")
    void getPagedItems() throws Exception {
        given(storeQueryService.getPagedItems(0, 6)).willReturn(pageResponseDto);

        mockMvc.perform(get("/store/items/page").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].name").value("테스트 배경"))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andDo(print());

        verify(storeQueryService, times(1)).getPagedItems(0, 6);
    }
}
