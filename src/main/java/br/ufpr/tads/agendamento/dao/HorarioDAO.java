
package br.ufpr.tads.agendamento.dao;

import java.sql.SQLException;
import java.util.List;

import br.ufpr.tads.agendamento.model.Horario;

public interface HorarioDAO {

    void inserir(Horario horario) throws SQLException;

    Horario buscarPorId(Long id) throws SQLException;

    List<Horario> listarPorReuniao(Long reuniaoId)
            throws SQLException;

    void excluir(Long id) throws SQLException;

    void excluirPorReuniao(Long reuniaoId)
            throws SQLException;
}
