package br.com.matricula.service.service;

import br.com.matricula.service.dto.*;
import br.com.matricula.service.entity.Matricula;
import br.com.matricula.service.exception.NaoEncontradoException;
import br.com.matricula.service.mapper.MatriculaMapper;
import br.com.matricula.service.repository.MatriculaRepository;
import br.com.matricula.service.tipos.StatusMatricula;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
                    .uri("http://localhost:8081/usuario/usuario-email-corpo/{id}", usuarioId)
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
                    .uri("http://localhost:8082/curso-aluno/{id}", cursoId)
                    .retrieve()
                    .bodyToMono(CursoDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NaoEncontradoException("Curso não econtrado");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar curso", e);
        }
    }

    private EmailDto GetEmail(String token) {
        try {
            return webClient.get()
                    .uri("http://localhost:8081/usuario/subject")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(EmailDto.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NaoEncontradoException("Email não econtrado");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar curso", e);
        }
    }

    private Object GetId(EmailDto email, String token) {
        try {
            return webClient.post()
                    .uri("http://localhost:8081/usuario/loginByEmail")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .bodyValue(email)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NaoEncontradoException("ID não encontrado");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar curso", e);
        }
    }

    public Matricula cadastrar (CadastroDto dados){

        Matricula matricula = matriculaMapper.cadadastrarDtoToEntity(dados);
        UsuarioDto usuario = VerificarUsuario(dados.idUsuario());
        CursoDto curso = VerificarCurso(dados.idCurso());

        EmailUsuarioCursoDto emailCorpo = new EmailUsuarioCursoDto(usuario.login(), usuario.nome(), curso.titulo(), curso.descricao());

        System.out.println("###########  " + usuario.nome());
        System.out.println("###########  " + emailCorpo.getNome());

        matricula.setStatus(StatusMatricula.MATRICULADO);
        matricula.setData(LocalDate.now());

        Matricula matriculaSalva =  repository.save(matricula);

        rabbitTemplate.convertAndSend("matricula.confirmada", emailCorpo);

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

    public List<ListagemUsuarioMatricula> listarUsuariosPorCurso(UUID idCurso) {

        VerificarCurso(idCurso);

        return repository.listarUsuariosPorCurso(idCurso);

    }

//    public List<CadastroConteudoDto> listarPorId(String authorizationHeader, UUID idCurso) {
//        var token = extractToken(authorizationHeader);
//
//        try {
//            ListagemUsuarioDTO usuario = webClient.get()
//                    .uri("http://localhost:8080/api/usuario/subject")
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                    .retrieve()
//                    .bodyToMono(ListagemUsuarioDTO.class)
//                    .block();
//
//            System.out.println(usuario.login());
//
//            UUID usuarioMatriculado = webClient.get()
//                    .uri(uriBuilder -> uriBuilder
//                            .path("http://localhost:8080/api/usuario/login")
//                            .queryParam("login", usuario.login())
//                            .build())
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                    .retrieve()
//                    .bodyToMono(UUID.class)
//                    .block();
//
//            System.out.println(usuarioMatriculado);
//
//            // Aqui faltou retornar algo. Por exemplo:
//            // return someService.getConteudosPorCursoEUsuario(idCurso, usuarioMatriculado);
//            return null; // Placeholder até que se saiba o que deve ser retornado
//
//        } catch (WebClientResponseException.NotFound e) {
//            return null;
//        } catch (Exception e) {
//            throw new RuntimeException("Erro ao buscar login do usuário", e);
//        }
//    }

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new RuntimeException("Token inválido ou ausente no cabeçalho Authorization");
        }
    }

    public Boolean verificarMatricula(UUID idCurso, String authorizationHeader) {

        System.out.println(authorizationHeader);

        String tokenCompleto = authorizationHeader;
        String tokenLimpo = tokenCompleto.replace("Bearer ", "");

        EmailDto email = GetEmail(tokenLimpo);

        Object idUsuario = GetId(email, tokenLimpo);

        String idBruto = idUsuario.toString().replace("id=", "");
        String idLimpo = idBruto.toString().replace("{", "");
        String id = idLimpo.toString().replace("}", "");

        System.out.println(id);

        Matricula check = repository.validarMatricula(idCurso, UUID.fromString(id)).orElseThrow(() -> new NaoEncontradoException("Nao encontrado"));

        return true;

    }
}
