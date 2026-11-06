
package br.ufpr.tads.agendamento.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.ufpr.tads.agendamento.dao.DisponibilidadeDAO;
import br.ufpr.tads.agendamento.model.Disponibilidade;
import br.ufpr.tads.agendamento.model.RespostaDisponibilidade;
import br.ufpr.tads.agendamento.util.Conexao;

public class DisponibilidadeDAOImpl implements DisponibilidadeDAO {

    @Override
    public void inserir(Disponibilidade disponibilidade)
            throws SQLException {

        String sql = "INSERT INTO disponibilidade "
                + "(participante_id, horario_id, resposta) "
                + "VALUES (?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, disponibilidade.getParticipanteId());
            stmt.setLong(2, disponibilidade.getHorarioId());
            stmt.setString(3, disponibilidade.getResposta().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    disponibilidade.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public void salvar(Disponibilidade disponibilidade)
            throws SQLException {

        String sql = "INSERT INTO disponibilidade "
                + "(participante_id, horario_id, resposta) "
                + "VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE resposta = VALUES(resposta)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, disponibilidade.getParticipanteId());
            stmt.setLong(2, disponibilidade.getHorarioId());
            stmt.setString(3, disponibilidade.getResposta().name());

            stmt.executeUpdate();
        }
    }

    @Override
    public Disponibilidade buscarPorId(Long id)
            throws SQLException {

        String sql = "SELECT * FROM disponibilidade WHERE id = ?";

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
    public List<Disponibilidade> listarPorParticipante(
            Long participanteId) throws SQLException {

        List<Disponibilidade> disponibilidades =
                new ArrayList<>();

        String sql = "SELECT * FROM disponibilidade "
                + "WHERE participante_id = ? ORDER BY horario_id";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, participanteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    disponibilidades.add(converter(rs));
                }
            }
        }

        return disponibilidades;
    }

    @Override
    public List<Disponibilidade> listarPorHorario(Long horarioId)
            throws SQLException {

        List<Disponibilidade> disponibilidades =
                new ArrayList<>();

        String sql = "SELECT * FROM disponibilidade "
                + "WHERE horario_id = ? ORDER BY participante_id";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, horarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    disponibilidades.add(converter(rs));
                }
            }
        }

        return disponibilidades;
    }

    @Override
    public void excluir(Long id) throws SQLException {

        String sql = "DELETE FROM disponibilidade WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void excluirPorParticipante(Long participanteId)
            throws SQLException {

        String sql = "DELETE FROM disponibilidade "
                + "WHERE participante_id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, participanteId);
            stmt.executeUpdate();
        }
    }

    private Disponibilidade converter(ResultSet rs)
            throws SQLException {

        Disponibilidade disponibilidade = new Disponibilidade();

        disponibilidade.setId(rs.getLong("id"));
        disponibilidade.setParticipanteId(
                rs.getLong("participante_id"));
        disponibilidade.setHorarioId(rs.getLong("horario_id"));

        disponibilidade.setResposta(
                RespostaDisponibilidade.valueOf(
                        rs.getString("resposta")));

        return disponibilidade;
    }
}
