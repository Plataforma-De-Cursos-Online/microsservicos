package br.com.matricula.service.repository;

import br.com.matricula.service.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {
    List<Matricula> findAllByIdUsuario(UUID id);

    List<Matricula> findAllByIdCurso(UUID idCurso);
}
