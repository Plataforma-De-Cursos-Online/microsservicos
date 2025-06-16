package br.com.matricula.service.service;

import br.com.matricula.service.dto.AtualizarDto;
import br.com.matricula.service.dto.CadastroDto;
import br.com.matricula.service.entity.Matricula;
import br.com.matricula.service.exception.NaoEncontradoException;
import br.com.matricula.service.mapper.MatriculaMapper;
import br.com.matricula.service.repository.MatriculaRepository;
import br.com.matricula.service.tipos.StatusMatricula;
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
    MatriculaRepository repository;

    @Autowired
    MatriculaMapper matriculaMapper;

    private boolean VerificarUsuario(UUID usuarioId) {
        try {
            webClient.get()
                    .uri("http://localhost:8081/usuario/{id}", usuarioId)
                    .retrieve()
                    .toBodilessEntity() // Só verifica se a resposta 2xx foi recebida
                    .block();
            return true;
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar usuário", e);
        }
    }

    private boolean VerificarCurso(UUID cursoId) {
        try {
            webClient.get()
                    .uri("http://localhost:8082/curso/{id}", cursoId)
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


    public Matricula cadastrar (CadastroDto dados){

        Matricula matricula = matriculaMapper.cadadastrarDtoToEntity(dados);
//
        boolean usuarioExiste = VerificarUsuario(dados.idUsuario());
//
        boolean cursoExiste = VerificarCurso(dados.idCurso());
//
        if (!usuarioExiste) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }
//
        if (!cursoExiste) {
            throw new NaoEncontradoException("Curso não encontrado");
        }

        matricula.setStatus(StatusMatricula.MATRICULADO);
        matricula.setData(LocalDate.now());

        return repository.save(matricula);
    }


    public List<Matricula> listarTodos() {

        return repository.findAll();

    }

    public Matricula atualizar(UUID id, AtualizarDto dados) {
        boolean usuarioExiste = VerificarUsuario(dados.idUsuario());
//
        boolean cursoExiste = VerificarCurso(dados.idCurso());
//
        if (!usuarioExiste) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }
//
        if (!cursoExiste) {
            throw new NaoEncontradoException("Curso não encontrado");
        }

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

    public List<Matricula> listarCursosPorUsuario(UUID id) {

        boolean usuarioExiste = VerificarUsuario(id);

        if (!usuarioExiste) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }

        return repository.findAllByIdUsuario(id);

    }

    public List<Matricula> listarUsuariosPorCurso(UUID idCurso) {
        boolean cursoExiste = VerificarCurso(idCurso);

        if (!cursoExiste) {
            throw new NaoEncontradoException("Curso não encontrado");
        }

        return repository.findAllByIdCurso(idCurso);

    }
}
