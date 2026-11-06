
package br.ufpr.tads.agendamento.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.ufpr.tads.agendamento.dao.ParticipanteDAO;
import br.ufpr.tads.agendamento.model.Participante;
import br.ufpr.tads.agendamento.util.Conexao;

public class ParticipanteDAOImpl implements ParticipanteDAO {

    @Override
    public void inserir(Participante participante) throws SQLException {

        String sql = "INSERT INTO participante "
                + "(reuniao_id, nome, email, token, proponente) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, participante.getReuniaoId());
            stmt.setString(2, participante.getNome());
            stmt.setString(3, participante.getEmail());
            stmt.setString(4, participante.getToken());
            stmt.setBoolean(5, participante.isProponente());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    participante.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public Participante buscarPorId(Long id) throws SQLException {

        String sql = "SELECT * FROM participante WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return converter(rs);
                }
            }
        }

        return null;
    }

    @Override
    public Participante buscarPorToken(String token)
            throws SQLException {

        String sql = "SELECT * FROM participante WHERE token = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, token);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return converter(rs);
                }
            }
        }

        return null;
    }

    @Override
    public List<Participante> listarPorReuniao(Long reuniaoId)
            throws SQLException {

        List<Participante> participantes = new ArrayList<>();

        String sql = "SELECT * FROM participante "
                + "WHERE reuniao_id = ? ORDER BY id";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    participantes.add(converter(rs));
                }
            }
        }

        return participantes;
    }

    @Override
    public void atualizarResposta(Long id) throws SQLException {

        String sql = "UPDATE participante "
                + "SET respondido_em = CURRENT_TIMESTAMP "
                + "WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void atualizarEmailEnviado(Long id)
            throws SQLException {

        String sql = "UPDATE participante "
                + "SET email_enviado_em = CURRENT_TIMESTAMP "
                + "WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void excluir(Long id) throws SQLException {

        String sql = "DELETE FROM participante WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Participante converter(ResultSet rs)
            throws SQLException {

        Participante participante = new Participante();

        participante.setId(rs.getLong("id"));
        participante.setReuniaoId(rs.getLong("reuniao_id"));
        participante.setNome(rs.getString("nome"));
        participante.setEmail(rs.getString("email"));
        participante.setToken(rs.getString("token"));
        participante.setProponente(rs.getBoolean("proponente"));

        Timestamp convidadoEm = rs.getTimestamp("convidado_em");
        if (convidadoEm != null) {
            participante.setConvidadoEm(
                    convidadoEm.toLocalDateTime());
        }

        Timestamp respondidoEm = rs.getTimestamp("respondido_em");
        if (respondidoEm != null) {
            participante.setRespondidoEm(
                    respondidoEm.toLocalDateTime());
        }

        Timestamp emailEnviadoEm =
                rs.getTimestamp("email_enviado_em");

        if (emailEnviadoEm != null) {
            participante.setEmailEnviadoEm(
                    emailEnviadoEm.toLocalDateTime());
        }

        return participante;
    }
}
