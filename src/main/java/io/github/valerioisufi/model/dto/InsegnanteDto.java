package io.github.valerioisufi.model.dto;

public record InsegnanteDto(
        int id,
        String nome,
        String cognome,
        String nazioneProvenienza,
        String via,
        String numeroCivico,
        String cap,
        String citta
) {}
