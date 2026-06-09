package io.github.valerioisufi.model.dto;

import java.time.LocalDate;

public record CorsoDto(
        String nomeLivello,
        int codice,
        LocalDate dataAttivazione,
        int numAllievi,
        String libro,
        boolean esameObbligatorio
) {}
