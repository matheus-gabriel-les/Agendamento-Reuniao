
package br.ufpr.tads.agendamento.dao.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.ufpr.tads.agendamento.dao.HorarioDAO;
import br.ufpr.tads.agendamento.model.Horario;
import br.ufpr.tads.agendamento.model.Periodo;
import br.ufpr.tads.agendamento.util.Conexao;

public class HorarioDAOImpl implements HorarioDAO {

    @Override
    public void inserir(Horario horario) throws SQLException {

        try (Connection conexao = Conexao.conectar()) {
            inserir(horario, conexao);
        }
    }

    // Insercao utilizando uma conexao compartilhada.
    public void inserir(Horario horario, Connection conexao)
            throws SQLException {

        String sql = "INSERT INTO horario "
                + "(reuniao_id, dia, periodo, hora_inicio, hora_fim) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, horario.getReuniaoId());
            stmt.setDate(2, Date.valueOf(horario.getDia()));

            if (horario.getPeriodo() != null) {
                stmt.setString(3, horario.getPeriodo().name());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            if (horario.getHoraInicio() != null) {
                stmt.setTime(4,
                        Time.valueOf(horario.getHoraInicio()));
            } else {
                stmt.setNull(4, Types.TIME);
            }

            if (horario.getHoraFim() != null) {
                stmt.setTime(5,
                        Time.valueOf(horario.getHoraFim()));
            } else {
                stmt.setNull(5, Types.TIME);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    horario.setId(rs.getLong(1));
                } else {
                    throw new SQLException(
                            "Nao foi possivel obter o ID do horario.");
                }
            }
        }
    }

    @Override
    public Horario buscarPorId(Long id) throws SQLException {

        String sql = "SELECT * FROM horario WHERE id = ?";

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
    public List<Horario> listarPorReuniao(Long reuniaoId)
            throws SQLException {

        List<Horario> horarios = new ArrayList<>();

        String sql = "SELECT * FROM horario "
                + "WHERE reuniao_id = ? "
                + "ORDER BY dia, hora_inicio, periodo, id";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    horarios.add(converter(rs));
                }
            }
        }

        return horarios;
    }

    @Override
    public void excluir(Long id) throws SQLException {

        String sql = "DELETE FROM horario WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public void excluirPorReuniao(Long reuniaoId)
            throws SQLException {

        String sql = "DELETE FROM horario WHERE reuniao_id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);
            stmt.executeUpdate();
        }
    }

    private Horario converter(ResultSet rs) throws SQLException {

        Horario horario = new Horario();

        horario.setId(rs.getLong("id"));
        horario.setReuniaoId(rs.getLong("reuniao_id"));
        horario.setDia(rs.getDate("dia").toLocalDate());

        String periodo = rs.getString("periodo");

        if (periodo != null) {
            horario.setPeriodo(Periodo.valueOf(periodo));
        }

        Time horaInicio = rs.getTime("hora_inicio");

        if (horaInicio != null) {
            horario.setHoraInicio(horaInicio.toLocalTime());
        }

        Time horaFim = rs.getTime("hora_fim");

        if (horaFim != null) {
            horario.setHoraFim(horaFim.toLocalTime());
        }

        return horario;
    }
}
