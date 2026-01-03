package com.shirval.model;

import java.time.Instant;

public record Customer(
        int id,
        Instant lastValidTill
) {}
