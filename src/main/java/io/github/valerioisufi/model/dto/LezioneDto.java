package io.github.valerioisufi.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record LezioneDto(
        int codice,
        LocalDate data,
        LocalTime oraInizio,
        LocalTime oraFine,
        int idInsegnante,
        String nomeLivelloCorso,
        int codiceCorso,
        InsegnanteDto insegnante
) {}
