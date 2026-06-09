package io.github.valerioisufi.model.dto;

import java.util.List;

public record SchedaAllievoDto(
        AllievoDto allievo,
        List<LezioneDto> assenze
) {}
