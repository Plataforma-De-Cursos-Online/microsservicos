package br.com.matricula.service.listen;

import br.com.matricula.service.dto.UsuarioDto;
import br.com.matricula.service.service.EmailServiceMatricula;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class MatriculaListen {

    @Autowired
    EmailServiceMatricula emailServiceMatricula;

    @RabbitListener(queues = "matricula.confirmada")
    public void ouvirPedido(UsuarioDto usuario) {

        emailServiceMatricula.enviarConfirmacaoTransacao(usuario);
        System.out.println("Login: " + usuario.login() + " Nome: " + usuario.nome());
    }

}
