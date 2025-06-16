package br.com.curso.service;

import br.com.curso.service.dto.CadastroCursoDTO;
import br.com.curso.service.dto.ListagemCursoDTO;
import br.com.curso.service.entity.Curso;
import br.com.curso.service.exception.NaoEncontradoException;
import br.com.curso.service.mapper.CursoMapper;
import br.com.curso.service.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CursoService {

    @Autowired
    CursoRepository cursoRepository;

    @Autowired
    CursoMapper cursoMapper;

    public CadastroCursoDTO criar(CadastroCursoDTO dto){
        Curso curso1 = cursoMapper.toEntity(dto);
        Curso cursoSalvo = cursoRepository.save(curso1);
        return cursoMapper.toDto(cursoSalvo);
    }

    public void deleteCurso(UUID id) {
        var cursoEncontrado = cursoRepository.findById(id);

        if (cursoEncontrado.isEmpty()) {
            throw new NaoEncontradoException("Não há curso cadastrado para o id " + id);
        }
        cursoRepository.deleteById(id);
    }

    public List<Curso> listarTodos() {
        return cursoRepository.findAll();
    }

    public Curso listarUmCurso(UUID id) {
        var endereco = cursoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Curso com ID não encontrado!"));

        return endereco;
    }

    public List<ListagemCursoDTO> getCursosDisponiveis() {
        return cursoRepository.listarCursosDisponiveis();
    }
}
