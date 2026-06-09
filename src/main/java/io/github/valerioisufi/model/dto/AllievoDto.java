package io.github.valerioisufi.model.dto;

import java.time.LocalDate;

public record AllievoDto(
        int id,
        String nome,
        String cognome,
        String telefono,
        String email,
        String nomeLivelloCorso,
        int codiceCorso,
        LocalDate dataIscrizione
) {}
