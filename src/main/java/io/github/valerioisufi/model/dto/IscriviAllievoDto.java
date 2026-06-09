package io.github.valerioisufi.model.dto;

public record IscriviAllievoDto(
        String nome,
        String cognome,
        String telefono,
        String email,
        String nomeLivelloCorso,
        int codiceCorso
) {}
