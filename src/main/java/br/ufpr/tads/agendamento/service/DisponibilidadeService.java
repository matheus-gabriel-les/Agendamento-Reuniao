
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.dao.jdbc.DisponibilidadeDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.HorarioDAOImpl;
import br.ufpr.tads.agendamento.dao.jdbc.ParticipanteDAOImpl;
import br.ufpr.tads.agendamento.model.Disponibilidade;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Participante;
import br.ufpr.tads.agendamento.model.RespostaDisponibilidade;
import br.ufpr.tads.agendamento.util.Conexao;

public class DisponibilidadeService {

    private final DisponibilidadeDAOImpl disponibilidadeDAO;
    private final HorarioDAOImpl horarioDAO;
    private final ParticipanteDAOImpl participanteDAO;

    public DisponibilidadeService() {
        disponibilidadeDAO = new DisponibilidadeDAOImpl();
        horarioDAO = new HorarioDAOImpl();
        participanteDAO = new ParticipanteDAOImpl();
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

    public void responder(
            String token,
            Long horarioId,
            RespostaDisponibilidade resposta)
            throws SQLException {

        validarHorarioId(horarioId);

        if (resposta == null) {
            throw new IllegalArgumentException(
                    "A resposta e obrigatoria.");
        }

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                Participante participante =
                        buscarParticipante(token, conexao);

                bloquearReuniao(
                        participante.getReuniaoId(), conexao);

                verificarReuniaoAberta(
                        participante.getReuniaoId(), conexao);

                verificarHorario(
                        horarioId,
                        participante.getReuniaoId(),
                        conexao);

                String sql =
                        "INSERT INTO disponibilidade "
                        + "(participante_id, horario_id, resposta) "
                        + "VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "resposta = VALUES(resposta)";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(sql)) {

                    stmt.setLong(1, participante.getId());
                    stmt.setLong(2, horarioId);
                    stmt.setString(3, resposta.name());
                    stmt.executeUpdate();
                }

                String atualizacao =
                        "UPDATE participante "
                        + "SET respondido_em = CURRENT_TIMESTAMP "
                        + "WHERE id = ?";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(atualizacao)) {

                    stmt.setLong(1, participante.getId());
                    stmt.executeUpdate();
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {
                rollback(conexao, e);
                throw e;
            }
        }
    }

    public void removerResposta(
            String token,
            Long horarioId)
            throws SQLException {

        validarHorarioId(horarioId);

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                Participante participante =
                        buscarParticipante(token, conexao);

                bloquearReuniao(
                        participante.getReuniaoId(), conexao);

                verificarReuniaoAberta(
                        participante.getReuniaoId(), conexao);

                verificarHorario(
                        horarioId,
                        participante.getReuniaoId(),
                        conexao);

                String excluir =
                        "DELETE FROM disponibilidade "
                        + "WHERE participante_id = ? "
                        + "AND horario_id = ?";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(excluir)) {

                    stmt.setLong(1, participante.getId());
                    stmt.setLong(2, horarioId);
                    stmt.executeUpdate();
                }

                String contagem =
                        "SELECT COUNT(*) FROM disponibilidade "
                        + "WHERE participante_id = ?";

                long quantidade;

                try (PreparedStatement stmt =
                        conexao.prepareStatement(contagem)) {

                    stmt.setLong(1, participante.getId());

                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        quantidade = rs.getLong(1);
                    }
                }

                if (quantidade == 0) {

                    String limpar =
                            "UPDATE participante "
                            + "SET respondido_em = NULL "
                            + "WHERE id = ?";

                    try (PreparedStatement stmt =
                            conexao.prepareStatement(limpar)) {

                        stmt.setLong(1, participante.getId());
                        stmt.executeUpdate();
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {
                rollback(conexao, e);
                throw e;
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
                participanteDAO.buscarPorToken(token.trim());

        if (participante == null) {
            throw new IllegalArgumentException(
                    "Participante nao encontrado.");
        }

        return participante;
    }

    private Participante buscarParticipante(
            String token,
            Connection conexao)
            throws SQLException {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Token do participante invalido.");
        }

        String sql =
                "SELECT id, reuniao_id FROM participante "
                + "WHERE token = ?";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setString(1, token.trim());

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    Participante participante =
                            new Participante();

                    participante.setId(rs.getLong("id"));
                    participante.setReuniaoId(
                            rs.getLong("reuniao_id"));

                    return participante;
                }
            }
        }

        throw new IllegalArgumentException(
                "Participante nao encontrado.");
    }

    private void bloquearReuniao(
            Long reuniaoId,
            Connection conexao)
            throws SQLException {

        String sql =
                "SELECT id FROM reuniao "
                + "WHERE id = ? FOR UPDATE";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Reuniao nao encontrada.");
                }
            }
        }
    }

    private void verificarReuniaoAberta(
            Long reuniaoId,
            Connection conexao)
            throws SQLException {

        String sql =
                "SELECT horario_id FROM reuniao_finalizacao "
                + "WHERE reuniao_id = ? FOR UPDATE";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalArgumentException(
                            "Esta reuniao ja foi finalizada. "
                            + "Nao e possivel alterar "
                            + "a disponibilidade.");
                }
            }
        }
    }

    private void verificarHorario(
            Long horarioId,
            Long reuniaoId,
            Connection conexao)
            throws SQLException {

        String sql =
                "SELECT id FROM horario "
                + "WHERE id = ? AND reuniao_id = ?";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setLong(1, horarioId);
            stmt.setLong(2, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "O horario nao pertence a reuniao "
                            + "deste participante.");
                }
            }
        }
    }

    private void validarHorarioId(Long horarioId) {

        if (horarioId == null || horarioId <= 0) {
            throw new IllegalArgumentException(
                    "Horario invalido.");
        }
    }

    private void rollback(
            Connection conexao,
            Exception erro) {

        try {
            conexao.rollback();
        } catch (SQLException e) {
            erro.addSuppressed(e);
        }
    }
}
