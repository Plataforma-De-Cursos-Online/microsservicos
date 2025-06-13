package br.com.conteudo.service;

import br.com.conteudo.dto.CadastroConteudoDto;
import br.com.conteudo.entity.Conteudo;
import br.com.conteudo.mapper.ConteudoMapper;
import br.com.conteudo.repository.ConteudoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConteudoService {

    @Autowired
    private ConteudoRepository conteudoRepository;

    @Autowired
    private ConteudoMapper conteudoMapper;

    @Autowired
    private RestTemplate restTemplate;


    public CadastroConteudoDto saveConteudo(CadastroConteudoDto dto){
        var requisicaoURL = "http://localhost:8084/conteudo/" + dto.idCurso();

        var curso = restTemplate.getForObject(requisicaoURL, Object.class);

        if (!conteudoRepository.existsById(dto.idCurso())){
            throw new RuntimeException("Curso com esse ID não existe!");
        }

        var conteudo = new Conteudo();
        conteudo.setPdf(dto.pdf());
        conteudo.setTitulo(dto.titulo());
        conteudo.setVideo(dto.video());
        conteudo.setIdCursos(dto.idCurso());
        conteudoRepository.save(conteudo);

        return conteudoMapper.toDto(conteudo);

    }


}
