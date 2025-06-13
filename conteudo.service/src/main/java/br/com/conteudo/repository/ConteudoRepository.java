package br.com.conteudo.repository;

import br.com.conteudo.entity.Conteudo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConteudoRepository extends JpaRepository<Conteudo, UUID> {
}
