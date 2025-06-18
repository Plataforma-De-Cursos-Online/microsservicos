package br.com.curso.service.controller;

import br.com.curso.service.dto.*;
import br.com.curso.service.entity.Curso;
import br.com.curso.service.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/curso-aluno")
public class CursoAlunoController {

    @Autowired
    CursoService cursoService;

    @GetMapping("/{id}")
    public ResponseEntity<Curso> findById(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(cursoService.listarUmCurso(id));
    }

    @GetMapping("disponiveis")
    public ResponseEntity<List<ListagemCursoDTO>> listarDisponiveis() {
        List<ListagemCursoDTO> cursos = cursoService.getCursosDisponiveis();
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/indisponiveis")
    public ResponseEntity<List<Curso>> listarIndisponiveis() {
        List<Curso> cursos = cursoService.getCursosIndisponiveis();
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/nomes")
    public ResponseEntity<List<NomeCursoDTO>> listarNomesDosCursos() {
        List<NomeCursoDTO> nomes = cursoService.getNomesDosCursos();
        return ResponseEntity.ok(nomes);
    }

    @GetMapping
    public ResponseEntity<List<Curso>> listarTodos() {
        return ResponseEntity.status(200).body(cursoService.listarTodos());
    }
}
