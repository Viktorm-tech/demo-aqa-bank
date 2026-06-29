package org.morski.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class InstantFrom7IntArrayDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int[] values = p.readValueAs(int[].class);
        if (values.length != 7) {
            throw new IllegalArgumentException(
                    "Expected array of 7 integers (year, month, day, hour, minute, second, nano), got " + values.length
            );
        }
        LocalDateTime ldt = LocalDateTime.of(
                values[0], values[1], values[2],
                values[3], values[4], values[5],
                values[6]
        );
        return ldt.toInstant(ZoneOffset.UTC);
    }
}
