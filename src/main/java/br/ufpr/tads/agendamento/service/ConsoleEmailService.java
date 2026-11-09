
package br.ufpr.tads.agendamento.service;

public class ConsoleEmailService implements EmailService {

    @Override
    public void enviar(
            String destinatario,
            String assunto,
            String mensagem) {

        System.out.println(
                "=== SIMULACAO DE E-MAIL ===");

        System.out.println(
                "Destinatario: " + destinatario);

        System.out.println(
                "Assunto: " + assunto);

        System.out.println(
                "Mensagem:\n" + mensagem);

        System.out.println(
                "=== FIM DA SIMULACAO ===");
    }

    @Override
    public boolean envioReal() {
        return false;
    }
}
