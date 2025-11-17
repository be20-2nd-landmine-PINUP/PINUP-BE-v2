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
public class StoreHighlightResponseDto {
    private List<StoreSummaryResponseDto> limitedItems;
    private List<StoreSummaryResponseDto> latestItems;
}
