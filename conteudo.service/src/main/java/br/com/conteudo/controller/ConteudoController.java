package br.com.conteudo.controller;

import br.com.conteudo.dto.CadastroConteudoDto;
import br.com.conteudo.service.ConteudoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conteudo")
public class ConteudoController {

    @Autowired
    private ConteudoService conteudoService;

    @PostMapping
    public ResponseEntity<CadastroConteudoDto> saveConteudo(@RequestBody @Valid CadastroConteudoDto dto){
        try {
            var conteudo = conteudoService.saveConteudo(dto);
            return ResponseEntity.status(201).body(conteudo);
        }catch (RuntimeException e){
            return ResponseEntity.status(409).build();
        }
    }


}
