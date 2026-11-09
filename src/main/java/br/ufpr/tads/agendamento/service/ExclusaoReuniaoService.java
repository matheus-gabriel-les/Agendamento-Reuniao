
package br.ufpr.tads.agendamento.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import br.ufpr.tads.agendamento.util.Conexao;

public class ExclusaoReuniaoService {

    public void excluir(Long reuniaoId) throws SQLException {

        if (reuniaoId == null || reuniaoId <= 0) {
            throw new IllegalArgumentException(
                    "ID da reuniao invalido.");
        }

        String sqlBuscar =
                "SELECT id FROM reuniao WHERE id = ? FOR UPDATE";

        String sqlExcluirFinalizacao =
                "DELETE FROM reuniao_finalizacao WHERE reuniao_id = ?";

        String sqlExcluirReuniao =
                "DELETE FROM reuniao WHERE id = ?";

        try (Connection conexao = Conexao.conectar()) {

            conexao.setAutoCommit(false);

            try {
                try (PreparedStatement consulta =
                        conexao.prepareStatement(sqlBuscar)) {

                    consulta.setLong(1, reuniaoId);

                    try (ResultSet resultado =
                            consulta.executeQuery()) {

                        if (!resultado.next()) {
                            throw new IllegalArgumentException(
                                    "Reuniao nao encontrada.");
                        }
                    }
                }

                // Remove primeiro o registro de finalizacao.
                try (PreparedStatement comando =
                        conexao.prepareStatement(
                                sqlExcluirFinalizacao)) {

                    comando.setLong(1, reuniaoId);
                    comando.executeUpdate();
                }

                // As demais tabelas usam ON DELETE CASCADE.
                try (PreparedStatement comando =
                        conexao.prepareStatement(sqlExcluirReuniao)) {

                    comando.setLong(1, reuniaoId);

                    int linhas = comando.executeUpdate();

                    if (linhas != 1) {
                        throw new SQLException(
                                "A reuniao nao foi excluida.");
                    }
                }

                conexao.commit();

            } catch (SQLException | RuntimeException e) {

                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    e.addSuppressed(erroRollback);
                }

                throw e;

            } finally {
                conexao.setAutoCommit(true);
            }
        }
    }
}
