package org.asupg.asupgservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.CompanyDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response class for searching companies")
public class CompanySearchResponse {

    @Schema(description = "List of companies")
    List<CompanyDTO> companies;

    @Schema(description = "Amount of companies in current page", example = "1")
    int count;

    @Schema(description = "Are there any more pages")
    boolean hasMore;

    @Schema(description = "Cursor token to retrieve next page", example = "token")
    String nextCursor;

    public CompanySearchResponse(List<CompanyDTO> companies, String nextCursor) {
        this.companies = companies;
        this.nextCursor = nextCursor;
        this.count = companies != null ? companies.size() : 0;
        this.hasMore = nextCursor != null && !nextCursor.isEmpty();
    }

}