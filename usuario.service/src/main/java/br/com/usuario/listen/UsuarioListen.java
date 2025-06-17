package br.com.usuario.listen;

import br.com.usuario.dto.EmailCorpoDto;
import br.com.usuario.service.EmailServiceUsuario;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

public class UsuarioListen {

    @Autowired
    EmailServiceUsuario emailServiceMatricula;

    @RabbitListener(queues = "usuario.criado")
    public void ouvirUsuario(EmailCorpoDto emailCorpo) {

        emailServiceMatricula.enviarConfirmacaoUsuarioCriado(emailCorpo);
        System.out.println("Login: " + emailCorpo.getLogin() + " Nome: " + emailCorpo.getNome());
    }

}
