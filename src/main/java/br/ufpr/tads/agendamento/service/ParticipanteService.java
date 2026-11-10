
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.ufpr.tads.agendamento.dao.jdbc.ParticipanteDAOImpl;
import br.ufpr.tads.agendamento.model.Participante;
import br.ufpr.tads.agendamento.util.Conexao;

public class ParticipanteService {

    private final ParticipanteDAOImpl participanteDAO;

    public ParticipanteService() {
        participanteDAO = new ParticipanteDAOImpl();
    }

    public Participante cadastrar(
            Long reuniaoId,
            String nome,
            String email,
            boolean proponente) throws SQLException {

        validarReuniaoId(reuniaoId);
        validarNome(nome);
        validarEmail(email);

        String emailNormalizado =
                email.trim().toLowerCase(Locale.ROOT);

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                bloquearReuniao(reuniaoId, conexao);
                verificarReuniaoAberta(reuniaoId, conexao);

                String sqlDuplicado =
                        "SELECT id FROM participante "
                        + "WHERE reuniao_id = ? AND email = ?";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(sqlDuplicado)) {

                    stmt.setLong(1, reuniaoId);
                    stmt.setString(2, emailNormalizado);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            throw new IllegalArgumentException(
                                    "Este email ja esta cadastrado "
                                    + "na reuniao.");
                        }
                    }
                }

                Participante participante = new Participante();

                participante.setReuniaoId(reuniaoId);
                participante.setNome(
                        nome == null ? null : nome.trim());
                participante.setEmail(emailNormalizado);
                participante.setToken(
                        UUID.randomUUID().toString());
                participante.setProponente(proponente);

                String sqlInserir =
                        "INSERT INTO participante "
                        + "(reuniao_id, nome, email, "
                        + "token, proponente) "
                        + "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(
                                sqlInserir,
                                Statement.RETURN_GENERATED_KEYS)) {

                    stmt.setLong(1, reuniaoId);
                    stmt.setString(2, participante.getNome());
                    stmt.setString(3, participante.getEmail());
                    stmt.setString(4, participante.getToken());
                    stmt.setBoolean(
                            5, participante.isProponente());

                    stmt.executeUpdate();

                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            participante.setId(rs.getLong(1));
                        }
                    }
                }

                conexao.commit();
                return participante;

            } catch (SQLException | RuntimeException e) {
                rollback(conexao, e);
                throw e;
            }
        }
    }

    public Participante buscarPorId(Long id)
            throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "ID do participante invalido.");
        }

        return participanteDAO.buscarPorId(id);
    }

    public Participante buscarPorToken(String token)
            throws SQLException {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Token do participante invalido.");
        }

        return participanteDAO.buscarPorToken(token.trim());
    }

    public List<Participante> listarPorReuniao(Long reuniaoId)
            throws SQLException {

        validarReuniaoId(reuniaoId);

        return participanteDAO.listarPorReuniao(reuniaoId);
    }

    public void registrarResposta(Long participanteId)
            throws SQLException {

        if (participanteId == null || participanteId <= 0) {
            throw new IllegalArgumentException(
                    "Participante invalido.");
        }

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                Long reuniaoId =
                        buscarReuniaoDoParticipante(
                                participanteId, conexao);

                bloquearReuniao(reuniaoId, conexao);
                verificarReuniaoAberta(reuniaoId, conexao);

                String sql =
                        "UPDATE participante "
                        + "SET respondido_em = CURRENT_TIMESTAMP "
                        + "WHERE id = ? AND reuniao_id = ?";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(sql)) {

                    stmt.setLong(1, participanteId);
                    stmt.setLong(2, reuniaoId);

                    if (stmt.executeUpdate() != 1) {
                        throw new IllegalArgumentException(
                                "Participante nao encontrado.");
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {
                rollback(conexao, e);
                throw e;
            }
        }
    }

    public void registrarEmailEnviado(Long participanteId)
            throws SQLException {

        if (buscarPorId(participanteId) == null) {
            throw new IllegalArgumentException(
                    "Participante nao encontrado.");
        }

        participanteDAO.atualizarEmailEnviado(participanteId);
    }

    public void excluir(Long participanteId)
            throws SQLException {

        if (participanteId == null || participanteId <= 0) {
            throw new IllegalArgumentException(
                    "Participante invalido.");
        }

        try (Connection conexao = Conexao.conectar()) {
            conexao.setAutoCommit(false);

            try {
                Long reuniaoId =
                        buscarReuniaoDoParticipante(
                                participanteId, conexao);

                bloquearReuniao(reuniaoId, conexao);
                verificarReuniaoAberta(reuniaoId, conexao);

                String sql =
                        "DELETE FROM participante "
                        + "WHERE id = ? AND reuniao_id = ?";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(sql)) {

                    stmt.setLong(1, participanteId);
                    stmt.setLong(2, reuniaoId);

                    if (stmt.executeUpdate() != 1) {
                        throw new IllegalArgumentException(
                                "Participante nao encontrado.");
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {
                rollback(conexao, e);
                throw e;
            }
        }
    }

    private Long buscarReuniaoDoParticipante(
            Long participanteId,
            Connection conexao) throws SQLException {

        String sql =
                "SELECT reuniao_id FROM participante "
                + "WHERE id = ?";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setLong(1, participanteId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("reuniao_id");
                }
            }
        }

        throw new IllegalArgumentException(
                "Participante nao encontrado.");
    }

    private void bloquearReuniao(
            Long reuniaoId,
            Connection conexao) throws SQLException {

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
            Connection conexao) throws SQLException {

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
                            + "Nao e possivel alterar participantes.");
                }
            }
        }
    }

    private void validarReuniaoId(Long reuniaoId) {
        if (reuniaoId == null || reuniaoId <= 0) {
            throw new IllegalArgumentException(
                    "Informe uma reuniao valida.");
        }
    }

    private void validarNome(String nome) {
        if (nome != null && nome.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "O nome deve ter ate 100 caracteres.");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()
                || email.trim().length() > 254
                || !email.trim().matches(
                        "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {

            throw new IllegalArgumentException(
                    "Informe um email valido.");
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
