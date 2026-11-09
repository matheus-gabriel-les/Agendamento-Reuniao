
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import br.ufpr.tads.agendamento.util.Conexao;

public class FinalizacaoService {

    public void finalizar(Long reuniaoId, Long horarioId)
            throws SQLException {

        if (reuniaoId == null || reuniaoId <= 0) {
            throw new IllegalArgumentException(
                    "Reuniao invalida.");
        }

        if (horarioId == null || horarioId <= 0) {
            throw new IllegalArgumentException(
                    "Horario invalido.");
        }

        try (Connection conexao = Conexao.conectar()) {

            conexao.setAutoCommit(false);

            try {

                bloquearReuniao(reuniaoId, conexao);

                if (buscarHorarioDefinitivo(
                        reuniaoId, conexao) != null) {

                    throw new IllegalArgumentException(
                            "Esta reuniao ja possui "
                            + "um horario definitivo.");
                }

                verificarHorario(
                        reuniaoId,
                        horarioId,
                        conexao);

                String sql =
                        "INSERT INTO reuniao_finalizacao "
                        + "(reuniao_id, horario_id) "
                        + "VALUES (?, ?)";

                try (PreparedStatement stmt =
                        conexao.prepareStatement(sql)) {

                    stmt.setLong(1, reuniaoId);
                    stmt.setLong(2, horarioId);

                    stmt.executeUpdate();

                } catch (
                        SQLIntegrityConstraintViolationException e) {

                    throw new IllegalArgumentException(
                            "Esta reuniao ja possui "
                            + "um horario definitivo.", e);
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;
            }
        }
    }

    public Long buscarHorarioDefinitivo(Long reuniaoId)
            throws SQLException {

        if (reuniaoId == null || reuniaoId <= 0) {
            throw new IllegalArgumentException(
                    "Reuniao invalida.");
        }

        String sql =
                "SELECT horario_id "
                + "FROM reuniao_finalizacao "
                + "WHERE reuniao_id = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt =
                     conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong("horario_id");
                }
            }
        }

        return null;
    }

    private Long buscarHorarioDefinitivo(
            Long reuniaoId,
            Connection conexao)
            throws SQLException {

        String sql =
                "SELECT horario_id "
                + "FROM reuniao_finalizacao "
                + "WHERE reuniao_id = ? FOR UPDATE";

        try (PreparedStatement stmt =
                conexao.prepareStatement(sql)) {

            stmt.setLong(1, reuniaoId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getLong("horario_id");
                }
            }
        }

        return null;
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

    private void verificarHorario(
            Long reuniaoId,
            Long horarioId,
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
                            "O horario nao pertence a reuniao.");
                }
            }
        }
    }
}
