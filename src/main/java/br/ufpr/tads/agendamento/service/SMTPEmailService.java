
package br.ufpr.tads.agendamento.service;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class SMTPEmailService implements EmailService {

    private final String host;
    private final int porta;
    private final String usuario;
    private final String senha;
    private final String remetente;

    public SMTPEmailService() {

        host = exigirVariavel("EMAIL_SMTP_HOST");
        usuario = exigirVariavel("EMAIL_SMTP_USER");
        senha = exigirVariavel("EMAIL_SMTP_PASSWORD");
        remetente = exigirVariavel("EMAIL_FROM");

        String valorPorta =
                System.getenv("EMAIL_SMTP_PORT");

        if (valorPorta == null
                || valorPorta.isBlank()) {

            porta = 587;

        } else {

            try {
                porta = Integer.parseInt(
                        valorPorta.trim());

                if (porta < 1 || porta > 65535) {
                    throw new NumberFormatException();
                }

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "EMAIL_SMTP_PORT invalida.", e);
            }
        }
    }

    @Override
    public void enviar(
            String destinatario,
            String assunto,
            String mensagem)
            throws MessagingException {

        Properties propriedades = new Properties();

        propriedades.put(
                "mail.smtp.host", host);

        propriedades.put(
                "mail.smtp.port",
                String.valueOf(porta));

        propriedades.put(
                "mail.smtp.auth", "true");

        propriedades.put(
                "mail.smtp.starttls.enable", "true");

        propriedades.put(
                "mail.smtp.starttls.required", "true");

        propriedades.put(
                "mail.smtp.ssl.checkserveridentity",
                "true");

        propriedades.put(
                "mail.smtp.connectiontimeout", "10000");

        propriedades.put(
                "mail.smtp.timeout", "15000");

        propriedades.put(
                "mail.smtp.writetimeout", "15000");

        Session sessao = Session.getInstance(
                propriedades,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                            getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                usuario, senha);
                    }
                });

        MimeMessage email =
                new MimeMessage(sessao);

        email.setFrom(
                new InternetAddress(remetente));

        email.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(destinatario));

        email.setSubject(assunto, "UTF-8");

        email.setText(mensagem, "UTF-8");

        Transport.send(email);
    }

    @Override
    public boolean envioReal() {
        return true;
    }

    private String exigirVariavel(String nome) {

        String valor = System.getenv(nome);

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Variavel de ambiente ausente: "
                    + nome);
        }

        return valor.trim();
    }
}
