package br.com.matricula.service.service;

import br.com.matricula.service.dto.UsuarioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceMatricula {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarConfirmacaoTransacao(UsuarioDto usuario) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(usuario.login());
        message.setSubject("Matricula Realizada com Sucesso");

        String corpo = "A ser feito";

        message.setText(corpo);
        mailSender.send(message);
        System.out.println("E-mail enviado com sucesso para " + usuario.login());
    }
}
