package org.morski.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morski.constants.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionData {

    @JsonProperty(required = true)
    private UUID id;

    @JsonProperty(required = true)
    private BigDecimal amount;

    @JsonProperty(required = true)
    private TransactionType type;

    @JsonProperty(required = true)
    private UUID relatedAccountId;

    @JsonProperty(required = true)
    private String description;

    @JsonProperty(required = true)
    private LocalDateTime timestamp;
}
