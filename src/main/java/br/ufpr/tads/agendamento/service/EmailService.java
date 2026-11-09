
package br.ufpr.tads.agendamento.service;

public interface EmailService {

    void enviar(
            String destinatario,
            String assunto,
            String mensagem) throws Exception;

    boolean envioReal();
}
