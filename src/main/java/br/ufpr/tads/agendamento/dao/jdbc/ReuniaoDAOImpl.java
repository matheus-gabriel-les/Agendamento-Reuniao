
package br.ufpr.tads.agendamento.dao.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import br.ufpr.tads.agendamento.dao.ReuniaoDAO;
import br.ufpr.tads.agendamento.model.ModoDivisao;
import br.ufpr.tads.agendamento.model.Reuniao;
import br.ufpr.tads.agendamento.util.Conexao;

public class ReuniaoDAOImpl implements ReuniaoDAO {

    @Override
    public void inserir(Reuniao reuniao) throws SQLException {
        String sql = "INSERT INTO reuniao "
                + "(titulo, descricao, local_reuniao, "
                + "nome_proponente, email_proponente, "
                + "token_publico, token_admin, data_inicio, "
                + "data_fim, modo_divisao, hora_inicio, "
                + "hora_fim, duracao_minutos) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, reuniao.getTitulo());
            stmt.setString(2, reuniao.getDescricao());
            stmt.setString(3, reuniao.getLocalReuniao());
            stmt.setString(4, reuniao.getNomeProponente());
            stmt.setString(5, reuniao.getEmailProponente());
            stmt.setString(6, reuniao.getTokenPublico());
            stmt.setString(7, reuniao.getTokenAdmin());
            stmt.setDate(8, Date.valueOf(reuniao.getDataInicio()));
            stmt.setDate(9, Date.valueOf(reuniao.getDataFim()));
            stmt.setString(10, reuniao.getModoDivisao().name());

            if (reuniao.getHoraInicio() != null) {
                stmt.setTime(11, Time.valueOf(reuniao.getHoraInicio()));
            } else {
                stmt.setNull(11, Types.TIME);
            }

            if (reuniao.getHoraFim() != null) {
                stmt.setTime(12, Time.valueOf(reuniao.getHoraFim()));
            } else {
                stmt.setNull(12, Types.TIME);
            }

            if (reuniao.getDuracaoMinutos() != null) {
                stmt.setInt(13, reuniao.getDuracaoMinutos());
            } else {
                stmt.setNull(13, Types.INTEGER);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reuniao.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public Reuniao buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM reuniao WHERE id = ?";

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
    public Reuniao buscarPorTokenPublico(String token)
            throws SQLException {
        String sql = "SELECT * FROM reuniao WHERE token_publico = ?";
        return buscarPorToken(sql, token);
    }

    @Override
    public Reuniao buscarPorTokenAdmin(String token)
            throws SQLException {
        String sql = "SELECT * FROM reuniao WHERE token_admin = ?";
        return buscarPorToken(sql, token);
    }

    private Reuniao buscarPorToken(String sql, String token)
            throws SQLException {

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
    public List<Reuniao> listar() throws SQLException {
        List<Reuniao> reunioes = new ArrayList<>();

        String sql = "SELECT * FROM reuniao ORDER BY criada_em DESC, id DESC";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reunioes.add(converter(rs));
            }
        }

        return reunioes;
    }

    @Override
    public void excluir(Long id) throws SQLException {
        String sql = "DELETE FROM reuniao WHERE id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    private Reuniao converter(ResultSet rs) throws SQLException {
        Reuniao reuniao = new Reuniao();

        reuniao.setId(rs.getLong("id"));
        reuniao.setTitulo(rs.getString("titulo"));
        reuniao.setDescricao(rs.getString("descricao"));
        reuniao.setLocalReuniao(rs.getString("local_reuniao"));
        reuniao.setNomeProponente(rs.getString("nome_proponente"));
        reuniao.setEmailProponente(rs.getString("email_proponente"));
        reuniao.setTokenPublico(rs.getString("token_publico"));
        reuniao.setTokenAdmin(rs.getString("token_admin"));

        reuniao.setDataInicio(
                rs.getDate("data_inicio").toLocalDate());
        reuniao.setDataFim(
                rs.getDate("data_fim").toLocalDate());

        reuniao.setModoDivisao(
                ModoDivisao.valueOf(rs.getString("modo_divisao")));

        Time horaInicio = rs.getTime("hora_inicio");
        if (horaInicio != null) {
            reuniao.setHoraInicio(horaInicio.toLocalTime());
        }

        Time horaFim = rs.getTime("hora_fim");
        if (horaFim != null) {
            reuniao.setHoraFim(horaFim.toLocalTime());
        }

        int duracao = rs.getInt("duracao_minutos");
        if (!rs.wasNull()) {
            reuniao.setDuracaoMinutos(duracao);
        }

        Timestamp criadaEm = rs.getTimestamp("criada_em");
        if (criadaEm != null) {
            reuniao.setCriadaEm(criadaEm.toLocalDateTime());
        }

        return reuniao;
    }
}
