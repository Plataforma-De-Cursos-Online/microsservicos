package br.com.usuario.controller;

import br.com.usuario.dto.CadastroUsuarioDTO;
import br.com.usuario.dto.ListagemUsuarioDTO;
import br.com.usuario.dto.LoginUsuarioDTO;
import br.com.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity cadastrar(@RequestBody @Valid CadastroUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.cadastrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    @GetMapping
    public ResponseEntity<List<ListagemUsuarioDTO>> getAllUsers() {
        return ResponseEntity.ok(usuarioService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListagemUsuarioDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        usuarioService.deleteUserById(id);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListagemUsuarioDTO> updateUser(@PathVariable UUID id, @RequestBody @Valid CadastroUsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.updateUser(id, dto));
    }
}
