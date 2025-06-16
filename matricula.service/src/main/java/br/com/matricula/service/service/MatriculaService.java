package br.com.matricula.service.service;

import br.com.matricula.service.dto.*;
import br.com.matricula.service.entity.Matricula;
import br.com.matricula.service.exception.NaoEncontradoException;
import br.com.matricula.service.mapper.MatriculaMapper;
import br.com.matricula.service.repository.MatriculaRepository;
import br.com.matricula.service.tipos.StatusMatricula;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MatriculaService {

    private final WebClient webClient;

    public MatriculaService(WebClient webClient) {
        this.webClient = webClient;
    }


    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Autowired
    MatriculaRepository repository;

    @Autowired
    MatriculaMapper matriculaMapper;

    private UsuarioDto VerificarUsuario(UUID usuarioId) {
        try {
            return webClient.get()
                    .uri("http://localhost:8081/usuario/{id}", usuarioId)
                    .retrieve()
                    .bodyToMono(UsuarioDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NaoEncontradoException("Usuário não econtrado");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar curso", e);
        }
    }

    private CursoDto VerificarCurso(UUID cursoId) {
        try {
            return webClient.get()
                    .uri("http://localhost:8082/curso/{id}", cursoId)
                    .retrieve()
                    .bodyToMono(CursoDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NaoEncontradoException("Curso não econtrado");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar curso", e);
        }
    }


    public Matricula cadastrar (CadastroDto dados){

        Matricula matricula = matriculaMapper.cadadastrarDtoToEntity(dados);

        UsuarioDto usuario = VerificarUsuario(dados.idUsuario());

        CursoDto curso = VerificarCurso(dados.idCurso());

        matricula.setStatus(StatusMatricula.MATRICULADO);
        matricula.setData(LocalDate.now());

        Matricula matriculaSalva =  repository.save(matricula);

        rabbitTemplate.convertAndSend("matricula.confirmada", usuario);

        return matriculaSalva;
    }


    public List<Matricula> listarTodos() {

        return repository.findAll();

    }

    public Matricula atualizar(UUID id, AtualizarDto dados) {

        repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Matricula não econtrada"));

        Matricula matricula = matriculaMapper.atualizarDtoToEntity(dados);
        matricula.setId(id);

        return repository.save(matricula);

    }

    public Matricula atualizarStatus(UUID id, StatusMatricula status) {

        Matricula matricula = repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Matricula não econtrada"));

        matricula.setId(id);
        matricula.setStatus(status);

        return repository.save(matricula);
    }

    public List<ListagemCursoMatricula> listarCursosPorUsuario(UUID id) {

        VerificarUsuario(id);

        return repository.listarCursosPorUsuario(id);

    }

    public List<Matricula> listarUsuariosPorCurso(UUID idCurso) {

        VerificarCurso(idCurso);

        return repository.findAllByIdCurso(idCurso);

    }
}
