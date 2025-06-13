package br.com.conteudo.controller;

import br.com.conteudo.dto.CadastroConteudoDto;
import br.com.conteudo.exception.CursoNaoEncontradoException;
import br.com.conteudo.service.ConteudoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/conteudo")
public class ConteudoController {

    @Autowired
    private ConteudoService conteudoService;

    @PostMapping
    public ResponseEntity<CadastroConteudoDto> saveConteudo(@RequestBody @Valid CadastroConteudoDto dto){
            var conteudo = conteudoService.saveConteudo(dto);
            return ResponseEntity.status(201).body(conteudo);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirConteudo(@PathVariable UUID id){
            conteudoService.deletarConteudo(id);
            return ResponseEntity.status(204).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CadastroConteudoDto> atualizarConteudo(@PathVariable UUID id, @RequestBody CadastroConteudoDto dto){
            var conteudo = conteudoService.atualizarConteudo(id, dto);
            return ResponseEntity.status(200).body(conteudo);
    }

    @GetMapping
    public ResponseEntity<List<CadastroConteudoDto>> listarConteudo(){
            var conteudo = conteudoService.listar();
            return ResponseEntity.status(200).body(conteudo);

    }


}
