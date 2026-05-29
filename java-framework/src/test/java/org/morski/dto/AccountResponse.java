package org.morski.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morski.constants.AccountStatus;
import org.morski.constants.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {

    @JsonProperty(required = true)
    private UUID id;

    @JsonProperty(required = true)
    private String customerId;

    @JsonProperty(required = true)
    private BigDecimal balance;

    @JsonProperty(required = true)
    private Currency currency;

    @JsonProperty(required = true)
    private AccountStatus status;
}
