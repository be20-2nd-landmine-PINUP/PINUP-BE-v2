package pinup.backend.store.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pinup.backend.store.command.dto.StoreSummaryResponseDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorePageResponseDto {
    private int page;
    private int size;
    private Long totalItems;
    private int totalPages;
    private List<StoreSummaryResponseDto> items;
}
