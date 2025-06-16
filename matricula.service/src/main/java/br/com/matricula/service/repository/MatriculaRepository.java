package br.com.matricula.service.repository;

import br.com.matricula.service.dto.CursoDto;
import br.com.matricula.service.dto.ListagemCursoMatricula;
import br.com.matricula.service.entity.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MatriculaRepository extends JpaRepository<Matricula, UUID> {
    List<Matricula> findAllByIdUsuario(UUID id);

    List<Matricula> findAllByIdCurso(UUID idCurso);

    @Query(value = """
    SELECT
      c.titulo,
      c.descricao,
      m.data
    FROM
      matricula m
    JOIN
      curso c ON m.id_curso = c.id
    WHERE
      m.id_usuario = :id
    """, nativeQuery = true)
    List<ListagemCursoMatricula> listarCursosPorUsuario(@Param("id") UUID id);


}
