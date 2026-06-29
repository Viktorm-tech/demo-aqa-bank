package org.morski.dto.kafka;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.morski.utils.InstantFrom7IntArrayDeserializer;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "eventType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AccountCreatedEvent.class, name = "ACCOUNT_CREATED"),
        @JsonSubTypes.Type(value = TransferCompletedEvent.class, name = "TRANSFER_COMPLETED")
})
public abstract class BaseEvent {
    private String eventType;
    private String accountId;
    private String relatedAccountId;

    @JsonDeserialize(using = InstantFrom7IntArrayDeserializer.class)
    private Instant timestamp;
}