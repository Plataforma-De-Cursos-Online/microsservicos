package br.com.conteudo.service;

import br.com.conteudo.dto.CadastroConteudoDto;
import br.com.conteudo.entity.Conteudo;
import br.com.conteudo.exception.ConteudoNaoEncontradoException;
import br.com.conteudo.exception.CursoNaoEncontradoException;
import br.com.conteudo.exception.NenhumConteudoCadastradoException;
import br.com.conteudo.mapper.ConteudoMapper;
import br.com.conteudo.repository.ConteudoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConteudoService {

    @Autowired
    private ConteudoRepository conteudoRepository;

    @Autowired
    private ConteudoMapper conteudoMapper;

    @Autowired
    private RestTemplate restTemplate;

    private final WebClient webClient;

    public ConteudoService(WebClient webClient) {
        this.webClient = webClient;
    }


    public CadastroConteudoDto saveConteudo(CadastroConteudoDto dto) {
       boolean verificarCurso = VerificarCurso(dto.idCurso());

        if (verificarCurso == false){
            throw new CursoNaoEncontradoException("Curso com esse ID não existe!");
        }

        var conteudo = new Conteudo();
        conteudo.setPdf(dto.pdf());
        conteudo.setTitulo(dto.titulo());
        conteudo.setVideo(dto.video());
        conteudo.setIdCursos(dto.idCurso());
        conteudoRepository.save(conteudo);

        return conteudoMapper.toDto(conteudo);

    }

    private boolean VerificarCurso(UUID cursoId) {
        try {
            webClient.get()
                    .uri("http://localhost:8082/curso", cursoId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar curso", e);
        }
    }

    public void deletarConteudo(UUID id) {
        if (!conteudoRepository.existsById(id)){
            throw new CursoNaoEncontradoException("Curso com esse ID não existe!");
        }

        conteudoRepository.deleteById(id);

    }

    public CadastroConteudoDto atualizarConteudo(UUID id, CadastroConteudoDto dto){
        var conteudoEncontrado =conteudoRepository.findById(id);

        if (conteudoEncontrado.isEmpty()) {
            throw new ConteudoNaoEncontradoException("Conteúdo com esse ID não existe!");
        }

        var conteudo = conteudoEncontrado.get();
        conteudo.setPdf(dto.pdf());
        conteudo.setTitulo(dto.titulo());
        conteudo.setVideo(dto.video());
        conteudo.setIdCursos(dto.idCurso());
        conteudoRepository.save(conteudo);

        return conteudoMapper.toDto(conteudo);
    }

    public List<CadastroConteudoDto> listar(){
        var listaConteudo = conteudoRepository.findAll();

        if (listaConteudo.isEmpty()) {
            throw new NenhumConteudoCadastradoException("Nenhum conteúdo cadastrado");
        }

        var listaListagemConteudo = new ArrayList<CadastroConteudoDto>();

        listaConteudo.stream().forEach(
                c ->listaListagemConteudo.add(conteudoMapper.toDto(c))
        );

        return listaListagemConteudo;
    }


}
