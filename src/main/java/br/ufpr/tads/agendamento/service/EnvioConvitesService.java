
package br.ufpr.tads.agendamento.service;

import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.model.Participante;
import br.ufpr.tads.agendamento.model.Reuniao;

public class EnvioConvitesService {

    private final ParticipanteService participanteService;

    public EnvioConvitesService() {
        participanteService = new ParticipanteService();
    }

    public static class ResultadoEnvio {

        private int enviados;
        private int simulados;
        private int ignorados;
        private int falhas;

        public int getEnviados() {
            return enviados;
        }

        public int getSimulados() {
            return simulados;
        }

        public int getIgnorados() {
            return ignorados;
        }

        public int getFalhas() {
            return falhas;
        }
    }

    public ResultadoEnvio enviarConvites(
            Reuniao reuniao,
            String enderecoBase,
            EmailService emailService)
            throws SQLException {

        if (reuniao == null
                || reuniao.getId() == null) {

            throw new IllegalArgumentException(
                    "Reuniao invalida.");
        }

        if (emailService == null) {
            throw new IllegalArgumentException(
                    "Servico de email invalido.");
        }

        if (enderecoBase == null
                || enderecoBase.isBlank()) {

            throw new IllegalArgumentException(
                    "Endereco de acesso invalido.");
        }

        List<Participante> participantes =
                participanteService.listarPorReuniao(
                        reuniao.getId());

        ResultadoEnvio resultado =
                new ResultadoEnvio();

        String base = enderecoBase.endsWith("/")
                ? enderecoBase.substring(
                        0, enderecoBase.length() - 1)
                : enderecoBase;

        for (Participante participante : participantes) {

            if (participante.getEmailEnviadoEm() != null) {
                resultado.ignorados++;
                continue;
            }

            String assunto =
                    "Convite para reuniao: "
                    + reuniao.getTitulo();

            String mensagem =
                    montarMensagem(
                            reuniao,
                            participante,
                            base);

            try {
                emailService.enviar(
                        participante.getEmail(),
                        assunto,
                        mensagem);

                if (emailService.envioReal()) {

                    participanteService.registrarEmailEnviado(
                            participante.getId());

                    resultado.enviados++;

                } else {
                    resultado.simulados++;
                }

            } catch (Exception e) {

                resultado.falhas++;

                // Nao registra token nem mensagem
                // completa nos logs de falha.
                System.err.println(
                        "Falha ao processar convite "
                        + "do participante ID "
                        + participante.getId()
                        + ": "
                        + e.getClass().getSimpleName());
            }
        }

        return resultado;
    }

    private String montarMensagem(
            Reuniao reuniao,
            Participante participante,
            String base) {

        return "Ola, " + participante.getNome() + "!\n\n"
                + "Voce foi convidado para participar "
                + "da reuniao abaixo.\n\n"
                + "Reuniao: " + reuniao.getTitulo() + "\n"
                + "Proponente: "
                + reuniao.getNomeProponente() + "\n"
                + "Local: "
                + valor(reuniao.getLocalReuniao()) + "\n"
                + "Periodo: "
                + reuniao.getDataInicio()
                + " ate "
                + reuniao.getDataFim() + "\n\n"
                + "Acesse a pagina:\n"
                + base
                + "/reunioes/disponibilidade\n\n"
                + "Seu token individual:\n"
                + participante.getToken() + "\n\n"
                + "Informe esse token na pagina "
                + "para registrar sua disponibilidade.\n\n"
                + "Nao compartilhe seu token com "
                + "outras pessoas.\n";
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank()
                ? "Nao informado"
                : texto;
    }
}
