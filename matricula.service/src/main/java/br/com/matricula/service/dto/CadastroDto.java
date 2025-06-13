package br.com.matricula.service.dto;

import br.com.matricula.service.tipos.StatusMatricula;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.util.UUID;

public record CadastroDto(
        LocalDate data,
        @Enumerated(EnumType.STRING)
        StatusMatricula status,
        UUID idUsuario,
        UUID idCurso
    )
{ }
