package br.com.curso.service.controller;

import br.com.curso.service.CursoService;
import br.com.curso.service.dto.CadastroCursoDTO;
import br.com.curso.service.dto.ListagemCursoDTO;
import br.com.curso.service.entity.Curso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    CursoService cursoService;


    @PostMapping
    public ResponseEntity<CadastroCursoDTO> postCurso(@RequestBody CadastroCursoDTO dto ){
        return ResponseEntity.status(201).body(cursoService.criar(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable UUID id) {
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> findById(@PathVariable UUID id) {
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<ListagemCursoDTO>> listarDisponiveis() {
        List<ListagemCursoDTO> cursos = cursoService.getCursosDisponiveis();
        return ResponseEntity.status(200).build();
    }

    @GetMapping
    public ResponseEntity<List<Curso>> listarTodos() {
        return ResponseEntity.status(200).build();
    }
}
