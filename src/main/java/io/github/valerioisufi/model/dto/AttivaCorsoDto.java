package io.github.valerioisufi.model.dto;

import java.util.List;

public record AttivaCorsoDto(
        String nomeLivello,
        List<LezioneDto> lezioni
) {}
