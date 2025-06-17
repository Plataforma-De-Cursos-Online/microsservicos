package br.com.usuario.service;

import br.com.usuario.dto.EmailCorpoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceUsuario {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarConfirmacaoUsuarioCriado(EmailCorpoDto dados) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dados.getLogin());
        message.setSubject("Usuário criado com Sucesso");

        String corpo = "";

        message.setText(corpo);
        mailSender.send(message);
        System.out.println("E-mail enviado com sucesso para " + dados.getLogin());
    }
}
