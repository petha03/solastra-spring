package com.solastra.adapters.in.rest.model;

import java.time.Instant;

public record AccountDto(
        String id,
        String name,
        String ownerEmail,
        Instant createdAt
) {}