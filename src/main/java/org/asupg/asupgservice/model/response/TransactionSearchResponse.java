package org.asupg.asupgservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.TransactionDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response class for searching transactions")
public class TransactionSearchResponse {

    @Schema(description = "List of transactions")
    List<TransactionDTO> transactions;

    @Schema(description = "Amount of transaction in current page", example = "1")
    int count;

    @Schema(description = "Are there any more pages")
    boolean hasMore;

    @Schema(description = "Cursor token to retrieve next page", example = "token")
    String nextCursor;

    public TransactionSearchResponse(List<TransactionDTO> transactions, String nextCursor) {
        this.transactions = transactions;
        this.nextCursor = nextCursor;
        this.count = transactions != null ? transactions.size() : 0;
        this.hasMore = nextCursor != null && !nextCursor.isEmpty();
    }
}
