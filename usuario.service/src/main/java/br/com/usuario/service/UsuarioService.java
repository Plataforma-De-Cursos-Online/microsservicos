package br.com.usuario.service;

import br.com.usuario.dto.*;
import br.com.usuario.entity.Usuario;
import br.com.usuario.exception.EmailExistenteException;
import br.com.usuario.exception.NaoEncontradoException;
import br.com.usuario.exception.NoContentException;
import br.com.usuario.infra.security.TokenService;
import br.com.usuario.mapper.UsuarioMapper;
import br.com.usuario.repository.UsuarioRepository;
import br.com.usuario.tipo.TipoUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ListagemUsuarioDTO cadastrar(CadastroUsuarioDTO dto) {
        validarLoginExistente(dto.login());

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setTipo_usuario(TipoUsuario.fromString(dto.tipoUsuario()));
        usuario.setPassword(new BCryptPasswordEncoder().encode(dto.password()));
        usuarioRepository.save(usuario);

        return usuarioMapper.toListagemUsuarioDTO(usuario);
    }

    private void validarLoginExistente(String login) {
        if (usuarioRepository.existsByLogin(login)) {
            throw new EmailExistenteException("Este e-mail já está cadastrado.");
        }
    }

    public String login(LoginUsuarioDTO dto) {
        var username = new UsernamePasswordAuthenticationToken(dto.login(), dto.password());

        var auth = this.authenticationManager.authenticate(username);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return token;
    }

    public List<ListagemUsuarioDTO> getAllUsers() {
        var usuariosList = usuarioRepository.findAll();

        if (usuariosList.isEmpty()) {
            throw new NoContentException("Lista vazia de Usuários");
        }

        List<ListagemUsuarioDTO> listagemUsuarioDTOS = usuariosList
                .stream()
                .map(u -> usuarioMapper.toListagemUsuarioDTO(u))
                .toList();

        return listagemUsuarioDTOS;
    }

    public ListagemUsuarioDTO getUserById(UUID id) {
        var userFind = usuarioRepository.findById(id);

        if (userFind.isEmpty()) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }

        return usuarioMapper.toListagemUsuarioDTO(userFind.get());
    }

    public void deleteUserById(UUID id) {

        if (!usuarioRepository.existsById(id)) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }

        usuarioRepository.deleteById(id);
    }

    public ListagemUsuarioDTO updateUser(UUID id, CadastroUsuarioDTO dto) {

        var userFind = usuarioRepository.findById(id);

        if (userFind.isEmpty()) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }

        var user = userFind.get();
        user = usuarioMapper.toEntity(dto);
        user.setTipo_usuario(TipoUsuario.fromString(dto.tipoUsuario()));
        user.setPassword(new BCryptPasswordEncoder().encode(dto.password()));

        usuarioRepository.save(user);

        return usuarioMapper.toListagemUsuarioDTO(user);
    }

    public ListagemUsuarioDTO updateUserName(UUID id, UpdateUserNameDTO dto) {

        var userFind = usuarioRepository.findById(id);

        if (userFind.isEmpty()) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }

        var user = userFind.get();
        user.setNome(dto.name());
        usuarioRepository.save(user);

        return usuarioMapper.toListagemUsuarioDTO(user);
    }

    public ListagemUsuarioDTO updateUserLogin(UUID id, UpdateUserLogin dto) {

        var userFind = usuarioRepository.findById(id);

        if (userFind.isEmpty()) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }

        var user = userFind.get();
        user.setLogin(dto.login());
        usuarioRepository.save(user);

        return usuarioMapper.toListagemUsuarioDTO(user);
    }

    public ListagemUsuarioDTO updateUserPassword(UUID id, UpdateUserPassword dto) {

        var userFind = usuarioRepository.findById(id);

        if (userFind.isEmpty()) {
            throw new NaoEncontradoException("Usuário não encontrado");
        }

        var user = userFind.get();
        user.setPassword(new BCryptPasswordEncoder().encode(dto.password()));
        usuarioRepository.save(user);

        return usuarioMapper.toListagemUsuarioDTO(user);
    }
}
