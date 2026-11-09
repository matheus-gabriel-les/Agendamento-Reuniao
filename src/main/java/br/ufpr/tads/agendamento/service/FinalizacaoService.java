
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

        String sqlHorario =
                "SELECT id FROM horario "
                + "WHERE id = ? AND reuniao_id = ?";

        String sqlFinalizacao =
                "INSERT INTO reuniao_finalizacao "
                + "(reuniao_id, horario_id) VALUES (?, ?)";

        try (Connection conexao = Conexao.conectar()) {

            try (PreparedStatement consulta =
                    conexao.prepareStatement(sqlHorario)) {

                consulta.setLong(1, horarioId);
                consulta.setLong(2, reuniaoId);

                try (ResultSet resultado =
                        consulta.executeQuery()) {

                    if (!resultado.next()) {
                        throw new IllegalArgumentException(
                                "O horario nao pertence a reuniao.");
                    }
                }
            }

            try (PreparedStatement comando =
                    conexao.prepareStatement(sqlFinalizacao)) {

                comando.setLong(1, reuniaoId);
                comando.setLong(2, horarioId);

                comando.executeUpdate();

            } catch (SQLIntegrityConstraintViolationException e) {

                if (buscarHorarioDefinitivo(reuniaoId) != null) {
                    throw new IllegalArgumentException(
                            "Esta reuniao ja possui "
                            + "um horario definitivo.");
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
             PreparedStatement comando =
                     conexao.prepareStatement(sql)) {

            comando.setLong(1, reuniaoId);

            try (ResultSet resultado =
                    comando.executeQuery()) {

                if (resultado.next()) {
                    return resultado.getLong("horario_id");
                }
            }
        }

        return null;
    }
}
