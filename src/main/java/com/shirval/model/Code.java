package com.shirval.model;

import java.time.Instant;

public record Code(
        int customerId,
        String code,
        Instant validFrom,
        Instant validTill
) {
}
