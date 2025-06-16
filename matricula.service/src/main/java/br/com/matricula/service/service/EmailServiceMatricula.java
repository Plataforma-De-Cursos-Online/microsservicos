package br.com.matricula.service.service;

import br.com.matricula.service.dto.EmailUsuarioCursoDto;
import br.com.matricula.service.dto.UsuarioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceMatricula {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarConfirmacaoTransacao(EmailUsuarioCursoDto dados) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dados.getLogin());
        message.setSubject("Matricula Realizada com Sucesso");

        String corpo = "A ser feito";

        message.setText(corpo);
        mailSender.send(message);
        System.out.println("E-mail enviado com sucesso para " + dados.getLogin());
    }
}
