
package br.ufpr.tads.agendamento.service;

import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.dao.jdbc.DisponibilidadeDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.HorarioDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ParticipanteDAOImpl;
import br.ufpr.tads.agendamento.model.Disponibilidade;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Participante;
import br.ufpr.tads.agendamento.model.RespostaDisponibilidade;

public class DisponibilidadeService {

    private final DisponibilidadeDAOImpl disponibilidadeDAO;
    private final HorarioDAOImpl horarioDAO;
    private final ParticipanteDAOImpl participanteDAO;

    public DisponibilidadeService() {
        this.disponibilidadeDAO = new DisponibilidadeDAOImpl();
        this.horarioDAO = new HorarioDAOImpl();
        this.participanteDAO = new ParticipanteDAOImpl();
    }

    public List<Horario> listarHorarios(String token)
            throws SQLException {

        Participante participante = validarToken(token);

        return horarioDAO.listarPorReuniao(
                participante.getReuniaoId());
    }

    public List<Disponibilidade> listarRespostas(String token)
            throws SQLException {

        Participante participante = validarToken(token);

        return disponibilidadeDAO.listarPorParticipante(
                participante.getId());
    }

    public void responder(String token, Long horarioId,
                          RespostaDisponibilidade resposta)
            throws SQLException {

        Participante participante = validarToken(token);

        if (horarioId == null || horarioId <= 0) {
            throw new IllegalArgumentException(
                    "Horario invalido.");
        }

        if (resposta == null) {
            throw new IllegalArgumentException(
                    "A resposta e obrigatoria.");
        }

        Horario horario = horarioDAO.buscarPorId(horarioId);

        if (horario == null) {
            throw new IllegalArgumentException(
                    "Horario nao encontrado.");
        }

        if (!horario.getReuniaoId().equals(
                participante.getReuniaoId())) {

            throw new IllegalArgumentException(
                    "O horario nao pertence a reuniao "
                    + "deste participante.");
        }

        Disponibilidade disponibilidade =
                new Disponibilidade(
                        participante.getId(),
                        horarioId,
                        resposta
                );

        disponibilidadeDAO.salvar(disponibilidade);

        participanteDAO.atualizarResposta(
                participante.getId());
    }

    public void removerResposta(String token, Long horarioId)
            throws SQLException {

        Participante participante = validarToken(token);

        if (horarioId == null || horarioId <= 0) {
            throw new IllegalArgumentException(
                    "Horario invalido.");
        }

        Horario horario = horarioDAO.buscarPorId(horarioId);

        if (horario == null
                || !horario.getReuniaoId().equals(
                        participante.getReuniaoId())) {

            throw new IllegalArgumentException(
                    "Horario nao pertence a reuniao.");
        }

        List<Disponibilidade> respostas =
                disponibilidadeDAO.listarPorParticipante(
                        participante.getId());

        for (Disponibilidade disponibilidade : respostas) {

            if (disponibilidade.getHorarioId().equals(
                    horarioId)) {

                disponibilidadeDAO.excluir(
                        disponibilidade.getId());

                return;
            }
        }
    }

    private Participante validarToken(String token)
            throws SQLException {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Token do participante invalido.");
        }

        Participante participante =
                participanteDAO.buscarPorToken(token);

        if (participante == null) {
            throw new IllegalArgumentException(
                    "Participante nao encontrado.");
        }

        return participante;
    }
}
